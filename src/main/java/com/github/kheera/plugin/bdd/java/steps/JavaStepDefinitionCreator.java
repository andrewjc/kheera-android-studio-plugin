package com.github.kheera.plugin.bdd.java.steps;

import com.github.kheera.plugin.bdd.AbstractStepDefinitionCreator;
import com.github.kheera.plugin.bdd.codegen.JavaCodeTemplates;
import com.github.kheera.plugin.bdd.java.CucumberJavaUtil;
import com.github.kheera.plugin.bdd.java.snippet.CamelCaseConcatenator;
import com.github.kheera.plugin.bdd.java.snippet.FunctionNameGenerator;
import com.github.kheera.plugin.bdd.java.snippet.SnippetGenerator;
import com.github.kheera.plugin.bdd.psi.GherkinStep;
import com.intellij.codeInsight.CodeInsightUtilCore;
import com.intellij.codeInsight.template.TemplateBuilder;
import com.intellij.codeInsight.template.TemplateBuilderFactory;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.JavaCreateFromTemplateHandler;
import com.intellij.ide.util.PackageUtil;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.util.CreateClassUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ObjectUtils;
import com.intellij.util.text.StringTokenizer;
import gherkin.pickles.Argument;
import gherkin.pickles.PickleLocation;
import gherkin.pickles.PickleStep;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;


/**
 * User: Andrey.Vokin
 * Date: 8/1/12
 */
public class JavaStepDefinitionCreator extends AbstractStepDefinitionCreator {
    public static final String STEP_DEFINITION_SUFFIX = "StepDefs";

    @NonNls
    public static final String DEFAULT_CLASS_TEMPLATE = "#DEFAULT_CLASS_TEMPLATE";
    @NonNls private static final String DO_NOT_CREATE_CLASS_TEMPLATE = "#DO_NOT_CREATE_CLASS_TEMPLATE";
    @NonNls private static final String CLASS_NAME_PROPERTY = "Class_Name";
    @NonNls private static final String INTERFACE_NAME_PROPERTY = "Interface_Name";
    @NonNls private static final String PACKAGE_NAME = "Package_Name";
    @NonNls private static final String FEATURE_FILE_NAME = "Feature_File_Name";



    private static PsiMethod buildStepDefinitionByStep(@NotNull final GherkinStep step, Language language) {
        String annotationPackage = new AnnotationPackageProvider().getAnnotationPackageFor(step);
        String methodAnnotation = String.format("@%s.", "com.kheera.annotations");

        //  final Step cucumberStep = new Step(new ArrayList<Comment>(), "TestStep", step.getStepName(), 0, null, null);
//    final Step cucumberStep = new Step(new ArrayList<Comment>(), step.getKeyword().getText(), step.getStepName(), 0, null, null);
        final SnippetGenerator generator = new SnippetGenerator(new JavaSnippet());

        final PickleStep cucumberStep = new PickleStep(step.getStepName(), new ArrayList<Argument>(), new ArrayList<PickleLocation>(), null);

        final String snippet = generator.getSnippet(cucumberStep, "TestStep", new FunctionNameGenerator(new CamelCaseConcatenator()))
                .replace("PendingException", "com.kheera.PendingException")
                .replaceFirst("@", methodAnnotation)
                .replaceAll("\\\\\\\\", "\\\\")
                .replaceAll("\\\\d", "\\\\\\\\d");


        JVMElementFactory factory = JVMElementFactories.requireFactory(language, step.getProject());
        return factory.createMethodFromText(snippet, step);
    }

    @NotNull
    public PsiFile createStepDefinitionContainer(@NotNull PsiFile featureFile, @NotNull PsiDirectory dir, @NotNull String name) {

        Properties props = FileTemplateManager.getInstance(dir.getProject()).getDefaultProperties();

        final String className = extractClassName(name);
        props.setProperty(CLASS_NAME_PROPERTY, className);
        props.setProperty(FEATURE_FILE_NAME, featureFile.getName());
//        properties.setProperty(PACKAGE_NAME, )

        PsiClass newClass =  createClassFromTemplate(props, "StepDefClassTemplate", dir, name);

        assert newClass != null;
        return newClass.getContainingFile();
    }

    @Override
    public boolean createStepDefinition(@NotNull GherkinStep step, @NotNull PsiFile file) {
        if (!(file instanceof PsiClassOwner)) return false;

        final Project project = file.getProject();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        assert editor != null;

        closeActiveTemplateBuilders(file);

        final PsiClass clazz = PsiTreeUtil.getChildOfType(file, PsiClass.class);
        if (clazz != null) {
            PsiDocumentManager.getInstance(project).commitAllDocuments();

            // snippet text
            final PsiMethod element = buildStepDefinitionByStep(step, file.getLanguage());
            PsiMethod addedElement = (PsiMethod) clazz.add(element);
            addedElement = CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(addedElement);
            JavaCodeStyleManager.getInstance(project).shortenClassReferences(addedElement);
            editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            assert editor != null;

            final PsiParameterList blockVars = addedElement.getParameterList();
            final PsiCodeBlock body = addedElement.getBody();
            final PsiAnnotation annotation = addedElement.getModifierList().getAnnotations()[0];
            final PsiElement regexpElement = annotation.getParameterList().getAttributes()[0];

            runTemplateBuilderOnAddedStep(editor, addedElement, regexpElement, blockVars, body);
        }

        return true;
    }

    protected void runTemplateBuilderOnAddedStep(Editor editor,
                                                 PsiElement addedElement,
                                                 PsiElement regexpElement,
                                                 PsiParameterList blockVars, PsiCodeBlock body) {
        Project project = regexpElement.getProject();
        final TemplateBuilder builder = TemplateBuilderFactory.getInstance().createTemplateBuilder(addedElement);

        final TextRange range = new TextRange(1, regexpElement.getTextLength() - 1);
        builder.replaceElement(regexpElement, range, regexpElement.getText().substring(range.getStartOffset(), range.getEndOffset()));

        for (PsiParameter var : blockVars.getParameters()) {
            final PsiElement nameIdentifier = var.getNameIdentifier();
            if (nameIdentifier != null) {
                builder.replaceElement(nameIdentifier, nameIdentifier.getText());
            }
        }

        if (body.getStatements().length > 0) {
            final PsiElement firstStatement = body.getStatements()[0];
            final TextRange pendingRange = new TextRange(0, firstStatement.getTextLength() - 1);
            builder.replaceElement(firstStatement, pendingRange,
                    firstStatement.getText().substring(pendingRange.getStartOffset(), pendingRange.getEndOffset()));
        }

        final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        documentManager.doPostponedOperationsAndUnblockDocument(editor.getDocument());
        builder.run(editor, false);
    }

    @Override
    public boolean validateNewStepDefinitionFileName(@NotNull final Project project, @NotNull final String name) {
        if (name.length() == 0) return false;
        if (!Character.isJavaIdentifierStart(name.charAt(0))) return false;
        for (int i = 1; i < name.length(); i++) {
            if (!Character.isJavaIdentifierPart(name.charAt(i))) return false;
        }
        return true;
    }

    @NotNull
    @Override
    public PsiDirectory getDefaultStepDefinitionFolder(@NotNull final GherkinStep step) {
        PsiFile featureFile = step.getContainingFile();
        if (featureFile != null) {
            PsiDirectory psiDirectory = featureFile.getContainingDirectory();
            final Project project = step.getProject();
            if (psiDirectory != null) {
                ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
                VirtualFile directory = psiDirectory.getVirtualFile();
                if (projectFileIndex.isInContent(directory)) {
                    VirtualFile sourceRoot = projectFileIndex.getSourceRootForFile(directory);
                    //noinspection ConstantConditions
                    final Module module = projectFileIndex.getModuleForFile(featureFile.getVirtualFile());
                    if (module != null) {
                        final VirtualFile[] sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots();
                        if (sourceRoot != null && sourceRoot.getName().equals("resources")) {
                            final VirtualFile resourceParent = sourceRoot.getParent();
                            for (VirtualFile vFile : sourceRoots) {
                                if (vFile.getPath().startsWith(resourceParent.getPath()) && vFile.getName().equals("java")) {
                                    sourceRoot = vFile;
                                    break;
                                }
                            }
                        } else {
                            if (sourceRoots.length > 0) {

                                boolean foundVFile = false;
                                for (VirtualFile vFile : sourceRoots) {
                                    if (!vFile.getPath().contains("/generated") && vFile.getPath().contains("/androidTest/java")) {
                                        sourceRoot = vFile;
                                        foundVFile = true;
                                        break;
                                    }
                                }

                                if (!foundVFile)
                                    sourceRoot = sourceRoots[sourceRoots.length - 1];
                            }
                        }
                    }
                    String packageName = "";
                    if (sourceRoot != null) {
                        packageName = CucumberJavaUtil.getPackageOfStepDef(step);
                    }

                    final String packagePath = packageName.replace('.', '/');
                    final String path = sourceRoot != null ? sourceRoot.getPath() : directory.getPath();
                    // ToDo: I shouldn't create directories, only create VirtualFile object.
                    final Ref<PsiDirectory> resultRef = new Ref<PsiDirectory>();
                    new WriteAction() {
                        protected void run(@NotNull Result result) throws Throwable {
                            final VirtualFile packageFile = VfsUtil.createDirectoryIfMissing(path + '/' + packagePath);
                            if (packageFile != null) {
                                resultRef.set(PsiDirectoryFactory.getInstance(project).createDirectory(packageFile));
                            }
                        }
                    }.execute();
                    return resultRef.get();
                }
            }
        }

        assert featureFile != null;
        return ObjectUtils.assertNotNull(featureFile.getParent());
    }

    @NotNull
    @Override
    public String getStepDefinitionFilePath(@NotNull final PsiFile file) {
        final VirtualFile vFile = file.getVirtualFile();
        if (file instanceof PsiClassOwner && vFile != null) {
            String packageName = ((PsiClassOwner) file).getPackageName();
            if (StringUtil.isEmptyOrSpaces(packageName)) {
                return vFile.getNameWithoutExtension();
            } else {
                return vFile.getNameWithoutExtension() + " (" + packageName + ")";
            }
        }
        return file.getName();
    }

    @NotNull
    @Override
    public String getDefaultStepFileName(@NotNull final GherkinStep step) {
        final FunctionNameGenerator concatenator = new FunctionNameGenerator(new CamelCaseConcatenator());
        String filename = step.getContainingFile().getName();
        if (filename.endsWith(".feature")) {
            filename = filename.substring(0, filename.lastIndexOf(".feature"));
        }
        filename = concatenator.generateFunctionName(filename);

        filename = Character.toUpperCase(filename.charAt(0)) + filename.substring(1);
        return filename + STEP_DEFINITION_SUFFIX;
    }


    @Nullable
    private static PsiClass createClassFromTemplate(@NotNull final Properties attributes, @Nullable final String templateName,
                                                    @NotNull final PsiDirectory directoryRoot,
                                                    @NotNull final String className) throws IncorrectOperationException {
        if (templateName == null) return null;
        if (templateName.equals(DO_NOT_CREATE_CLASS_TEMPLATE)) return null;

        final Project project = directoryRoot.getProject();
        try {
            final PsiDirectory directory = createParentDirectories(directoryRoot, className);
            final PsiFile psiFile = directory.findFile(className + "." + StdFileTypes.JAVA.getDefaultExtension());
            if (psiFile != null) {
                psiFile.delete();
            }

            final String rawClassName = extractClassName(className);
            final PsiFile existing = directory.findFile(rawClassName + ".java");
            if (existing instanceof PsiJavaFile) {
                final PsiClass[] classes = ((PsiJavaFile)existing).getClasses();
                if (classes.length > 0) {
                    return classes[0];
                }
                return null;
            }

            final PsiClass aClass;
            if (templateName.equals(DEFAULT_CLASS_TEMPLATE)) {
                aClass = JavaDirectoryService.getInstance().createClass(directory, rawClassName);
            }
            else {
                final FileTemplateManager fileTemplateManager = FileTemplateManager.getInstance(project);
                FileTemplate fileTemplate = fileTemplateManager.getJ2eeTemplate(templateName);

                if (fileTemplate == null){
                    fileTemplate = fileTemplateManager.addTemplate(templateName,"java");
                    String text = FileUtil.loadTextAndClose(new InputStreamReader(JavaStepDefinitionCreator.class.getClassLoader().getResourceAsStream("/fileTemplates/" + templateName + ".ft")));
                    fileTemplate.setText(text);
                }

                final String text = fileTemplate.getText(attributes);
                aClass = JavaCreateFromTemplateHandler.createClassOrInterface(project, directory, text, true, fileTemplate.getExtension());
            }
            return (PsiClass)JavaCodeStyleManager.getInstance(project).shortenClassReferences(aClass);
        }
        catch (IOException e) {
            throw new IncorrectOperationException(e);
        }
    }

    @NotNull
    private static PsiDirectory createParentDirectories(@NotNull PsiDirectory directoryRoot, @NotNull String className) throws IncorrectOperationException {
        final PsiPackage currentPackage = JavaDirectoryService.getInstance().getPackage(directoryRoot);
        final String packagePrefix = currentPackage == null? null : currentPackage.getQualifiedName() + ".";
        final String packageName = extractPackage(packagePrefix != null && className.startsWith(packagePrefix)?
                className.substring(packagePrefix.length()) : className);
        final StringTokenizer tokenizer = new StringTokenizer(packageName, ".");
        while (tokenizer.hasMoreTokens()) {
            String packagePart = tokenizer.nextToken();
            PsiDirectory subdirectory = directoryRoot.findSubdirectory(packagePart);
            if (subdirectory == null) {
                directoryRoot.checkCreateSubdirectory(packagePart);
                subdirectory = directoryRoot.createSubdirectory(packagePart);
            }
            directoryRoot = subdirectory;
        }
        return directoryRoot;
    }

    public static String extractClassName(String fqName) {
        return StringUtil.getShortName(fqName);
    }

    public static String extractPackage(String fqName) {
        int i = fqName.lastIndexOf('.');
        return i == -1 ? "" : fqName.substring(0, i);
    }

    public static String makeFQName(String aPackage, String className) {
        String fq = aPackage;
        if (!"".equals(aPackage)) {
            fq += ".";
        }
        fq += className;
        return fq;
    }

}

