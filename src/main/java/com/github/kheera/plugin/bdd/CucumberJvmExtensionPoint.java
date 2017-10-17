package com.github.kheera.plugin.bdd;

import com.github.kheera.plugin.bdd.psi.GherkinFile;
import com.github.kheera.plugin.bdd.steps.AbstractStepDefinition;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface CucumberJvmExtensionPoint {
  ExtensionPointName<CucumberJvmExtensionPoint> EP_NAME =
    ExtensionPointName.create("com.github.kheera.plugin.bdd.steps.cucumberJvmExtensionPoint");

  // ToDo: remove parent
  /**
   * Checks if the child could be step definition file
   * @param child a PsiFile
   * @param parent container of the child
   * @return true if the child could be step definition file, else otherwise
   */
  boolean isStepLikeFile(@NotNull PsiElement child, @NotNull PsiElement parent);

  /**
   * Checks if the child could be a step definition container
   * @param child PsiElement to check
   * @param parent it's container
   * @return true if child could be step definition container and it's possible to write in it
   */
  boolean isWritableStepLikeFile(@NotNull PsiElement child, @NotNull PsiElement parent);

  /**
   * Provides type of step definition file
   * @return type
   */
  @NotNull
  BDDFrameworkType getStepFileType();


  @NotNull
  StepDefinitionCreator getStepDefinitionCreator();

  /**
   * Provide resolving of step
   * @param step to be resolved
   * @return list of elements where step is resolved
   */
  List<PsiElement> resolveStep(@NotNull PsiElement step);

  /**
   * Infers all 'glue' parameters for the file which it can find out.
   * @return inferred 'glue' parameters
   */
  @NotNull
  Collection<String> getGlues(@NotNull GherkinFile file, Set<String> gluesFromOtherFiles);

  /**
   * Provides all possible step definitions available from current feature file.
   * @param featureFile
   * @param module
   * @return
   */
  List<AbstractStepDefinition> loadStepsFor(@Nullable PsiFile featureFile, @NotNull Module module);

  void flush(@NotNull Project project);

  void reset(@NotNull Project project);

  Object getDataObject(@NotNull Project project);

  Collection<? extends PsiFile> getStepDefinitionContainers(@NotNull GherkinFile file);
}
