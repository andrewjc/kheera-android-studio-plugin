package com.github.kheera.plugin.bdd.java;

import com.github.kheera.plugin.bdd.BDDFrameworkType;
import com.github.kheera.plugin.bdd.StepDefinitionCreator;
import com.github.kheera.plugin.bdd.java.steps.JavaStepDefinition;
import com.github.kheera.plugin.bdd.java.steps.JavaStepDefinitionCreator;
import com.github.kheera.plugin.bdd.steps.AbstractStepDefinition;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.impl.java.stubs.index.JavaFullClassNameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.intellij.psi.search.searches.AnnotatedPackagesSearch;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CucumberJavaExtension extends AbstractCucumberJavaExtension {
    public static final String TEST_STEP_ANNOTATION_CLASS = "com.kheera.annotations.TestStep";
    public static final String TEST_MODULE_ANNOTATION_CLASS = "com.kheera.annotations.TestModule";


    @NotNull
    @Override
    public BDDFrameworkType getStepFileType() {
        return new BDDFrameworkType(JavaFileType.INSTANCE);
    }

    @NotNull
    @Override
    public StepDefinitionCreator getStepDefinitionCreator() {
        return new JavaStepDefinitionCreator();
    }

    @Override
    public List<AbstractStepDefinition> loadStepsFor(@Nullable PsiFile featureFile, @NotNull Module module) {
        final GlobalSearchScope dependenciesScope = module.getModuleWithDependenciesAndLibrariesScope(true);
        final List<AbstractStepDefinition> result = new ArrayList<AbstractStepDefinition>();

        Collection<PsiClass> testModuleAnnotationClassCandidates = JavaFullClassNameIndex.getInstance().get(TEST_MODULE_ANNOTATION_CLASS.hashCode(), module.getProject(), dependenciesScope);
        Collection<PsiClass> testStepAnnotationClassCandidates = JavaFullClassNameIndex.getInstance().get(TEST_STEP_ANNOTATION_CLASS.hashCode(), module.getProject(), dependenciesScope);
        PsiClass testModuleAnnotationClass = null;
        PsiClass testStepAnnotationClass = null;

        // Locate the annotation for test module
        for (PsiClass candidate : testModuleAnnotationClassCandidates) {
            if (TEST_MODULE_ANNOTATION_CLASS.equals(candidate.getQualifiedName())) {
                testModuleAnnotationClass = candidate;
                break;
            }
        }
        if (testModuleAnnotationClass == null)
            return Collections.emptyList();

        // Locate the annotation for test step
        for(PsiClass candidate : testStepAnnotationClassCandidates) {
            if(TEST_STEP_ANNOTATION_CLASS.equals(candidate.getQualifiedName())) {
                testStepAnnotationClass = candidate;
                break;
            }
        }

        if(testStepAnnotationClass == null)
            return Collections.emptyList();

        Query<PsiClass> moduleClasses = AnnotatedElementsSearch.searchPsiClasses(testModuleAnnotationClass, dependenciesScope);
        for (PsiClass moduleClass : moduleClasses) {
            final Query<PsiMethod> javaStepDefinitions = AnnotatedElementsSearch.searchPsiMethods(testStepAnnotationClass, dependenciesScope);

            for(PsiMethod m : moduleClass.getMethods()) {
                PsiModifierList mn = m.getModifierList();

                int i=0;i++;
            }


            for (PsiMethod stepDefMethod : javaStepDefinitions) {

                result.add(new JavaStepDefinition(stepDefMethod));
            }
        }
        return result;

        /*

        Collection<PsiClass> stepDefAnnotationCandidates = JavaFullClassNameIndex.getInstance().get(
                TEST_STEP_ANNOTATION_CLASS.hashCode(), module.getProject(), dependenciesScope);

        PsiClass stepDefAnnotationClass = null;
        for (PsiClass candidate : stepDefAnnotationCandidates) {
            if (TEST_STEP_ANNOTATION_CLASS.equals(candidate.getQualifiedName())) {
                stepDefAnnotationClass = candidate;
                break;
            }
        }
        if (stepDefAnnotationClass == null) {
            return Collections.emptyList();
        }

        final List<AbstractStepDefinition> result = new ArrayList<AbstractStepDefinition>();
        final Query<PsiClass> stepDefAnnotations = AnnotatedElementsSearch.searchPsiClasses(stepDefAnnotationClass, dependenciesScope);
        for (PsiClass annotationClass : stepDefAnnotations) {
            if (annotationClass.isAnnotationType()) {
                final Query<PsiMethod> javaStepDefinitions = AnnotatedElementsSearch.searchPsiMethods(annotationClass, dependenciesScope);
                for (PsiMethod stepDefMethod : javaStepDefinitions) {
                    result.add(new JavaStepDefinition(stepDefMethod));
                }
            }
        }
        return result;*/
    }
}
