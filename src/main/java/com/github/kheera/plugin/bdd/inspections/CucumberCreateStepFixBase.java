package com.github.kheera.plugin.bdd.inspections;

import com.github.kheera.plugin.bdd.*;
import com.github.kheera.plugin.bdd.inspections.model.CreateStepDefinitionFileModel;
import com.github.kheera.plugin.bdd.inspections.ui.CreateStepDefinitionFileDialog;
import com.github.kheera.plugin.bdd.java.steps.JavaStepDefinitionCreator;
import com.github.kheera.plugin.bdd.psi.GherkinFile;
import com.github.kheera.plugin.bdd.psi.GherkinStep;
import com.github.kheera.plugin.bdd.steps.CucumberStepsIndex;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class CucumberCreateStepFixBase implements LocalQuickFix {
    private static final Logger LOG = Logger.getInstance("#CucumberCreateStepFixBase");

    public static PsiFile getStepDefinitionContainer(@NotNull final GherkinFile featureFile) {
        final PsiFile result = CucumberStepsIndex.getInstance(featureFile.getProject()).getStepDefinitionContainer(featureFile);

        return result;
    }

    private static void createStepDefinitionFile(final GherkinStep step) {
        final PsiFile featureFile = step.getContainingFile();
        assert featureFile != null;

        final CreateStepDefinitionFileModel model = askUserForFilePath(step);
        if (model == null) {
            return;
        }
        String filePath = FileUtil.toSystemDependentName(model.getFilePath());
        final BDDFrameworkType frameworkType = model.getSelectedFileType();

        // show error if file already exists
        Project project = step.getProject();
        if (LocalFileSystem.getInstance().findFileByPath(filePath) == null) {
            final String parentDirPath = model.getDirectory().getVirtualFile().getPath();

            ApplicationManager.getApplication().invokeLater(
                    () -> CommandProcessor.getInstance().executeCommand(project, () -> {
                        try {
                            VirtualFile parentDir = VfsUtil.createDirectories(parentDirPath);
                            PsiDirectory parentPsiDir = PsiManager.getInstance(project).findDirectory(parentDir);
                            assert parentPsiDir != null;
                            PsiFile newFile = CucumberStepsIndex.getInstance(project)
                                    .createStepDefinitionFile(step.getContainingFile(), model.getDirectory(), model.getFileName(), frameworkType);

                            createStepDefinition(step, newFile);
                        } catch (IOException e) {
                            LOG.error(e);
                        }
                    }, CucumberBundle.message("cucumber.quick.fix.create.step.command.name.create"), null));
        } else {
            Messages.showErrorDialog(project,
                    CucumberBundle.message("cucumber.quick.fix.create.step.error.already.exist.msg", filePath),
                    CucumberBundle.message("cucumber.quick.fix.create.step.file.name.title"));
        }
    }

    @Nullable
    private static CreateStepDefinitionFileModel askUserForFilePath(@NotNull final GherkinStep step) {
        final InputValidator validator = new InputValidator() {
            public boolean checkInput(final String filePath) {
                return !StringUtil.isEmpty(filePath);
            }

            public boolean canClose(final String fileName) {
                return true;
            }
        };

        Map<BDDFrameworkType, String> supportedFileTypesAndDefaultFileNames = new HashMap<>();
        Map<BDDFrameworkType, PsiDirectory> fileTypeToDefaultDirectoryMap = new HashMap<>();
        for (CucumberJvmExtensionPoint e : Extensions.getExtensions(CucumberJvmExtensionPoint.EP_NAME)) {
            if (e instanceof OptionalStepDefinitionExtensionPoint) {
                // Skip if framework file creation support is optional
                if (!((OptionalStepDefinitionExtensionPoint) e).participateInStepDefinitionCreation(step)) {
                    continue;
                }
            }
            supportedFileTypesAndDefaultFileNames.put(e.getStepFileType(), e.getStepDefinitionCreator().getDefaultStepFileName(step));
            fileTypeToDefaultDirectoryMap.put(e.getStepFileType(), e.getStepDefinitionCreator().getDefaultStepDefinitionFolder(step));
        }

        CreateStepDefinitionFileModel model =
                new CreateStepDefinitionFileModel(step.getProject(), supportedFileTypesAndDefaultFileNames, fileTypeToDefaultDirectoryMap);
        CreateStepDefinitionFileDialog createStepDefinitionFileDialog = new CreateStepDefinitionFileDialog(step.getProject(), model, validator);
        if (createStepDefinitionFileDialog.showAndGet()) {
            return model;
        } else {
            return null;
        }
    }

    private static void createStepDefinition(GherkinStep step, PsiFile file) {
        StepDefinitionCreator stepDefCreator = new JavaStepDefinitionCreator();
        WriteCommandAction.runWriteCommandAction(step.getProject(), null, null, () -> stepDefCreator.createStepDefinition(step, file), file);
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @NotNull
    public String getFamilyName() {
        return getName();
    }

    public void applyFix(@NotNull final Project project, @NotNull ProblemDescriptor descriptor) {

        final GherkinStep step = (GherkinStep) descriptor.getPsiElement();
        final GherkinFile featureFile = (GherkinFile) step.getContainingFile();

        final PsiFile result = CucumberStepsIndex.getInstance(featureFile.getProject()).getStepDefinitionContainer(featureFile);

        createStepOrSteps(step, result);
    }

    protected void createStepOrSteps(final GherkinStep step, PsiFile file) {
        if (file == null) {
            createStepDefinitionFile(step);
        } else {
            createStepDefinition(step, file);
        }
    }
}
