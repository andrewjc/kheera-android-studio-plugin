package org.jetbrains.plugins.cucumber.java;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.java.stubs.index.JavaFullClassNameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.cucumber.BDDFrameworkType;
import org.jetbrains.plugins.cucumber.StepDefinitionCreator;
import org.jetbrains.plugins.cucumber.java.steps.JavaStepDefinition;
import org.jetbrains.plugins.cucumber.java.steps.JavaStepDefinitionCreator;
import org.jetbrains.plugins.cucumber.steps.AbstractStepDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CucumberJavaExtension extends AbstractCucumberJavaExtension {
  public static final String CUCUMBER_RUNTIME_JAVA_STEP_DEF_ANNOTATION = "com.kheera.annotations.TestStepAnnotation";

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

    Collection<PsiClass> stepDefAnnotationCandidates = JavaFullClassNameIndex.getInstance().get(
      CUCUMBER_RUNTIME_JAVA_STEP_DEF_ANNOTATION.hashCode(), module.getProject(), dependenciesScope);

    PsiClass stepDefAnnotationClass = null;
    for (PsiClass candidate : stepDefAnnotationCandidates) {
      if (CUCUMBER_RUNTIME_JAVA_STEP_DEF_ANNOTATION.equals(candidate.getQualifiedName())) {
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
    return result;
  }
}