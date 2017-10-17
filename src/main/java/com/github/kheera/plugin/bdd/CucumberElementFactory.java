package com.github.kheera.plugin.bdd;

import com.github.kheera.plugin.bdd.psi.GherkinFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.LocalTimeCounter;
import org.jetbrains.annotations.NotNull;

public class CucumberElementFactory {

  public static PsiElement createTempPsiFile(@NotNull final Project project, @NotNull final String text) {

    return PsiFileFactory.getInstance(project).createFileFromText("temp." + GherkinFileType.INSTANCE.getDefaultExtension(),
                                                                  GherkinFileType.INSTANCE,
                                                                  text, LocalTimeCounter.currentTime(), true);
  }
}
