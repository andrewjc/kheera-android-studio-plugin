package com.github.kheera.plugin.bdd.java;

import com.github.kheera.plugin.bdd.java.config.CucumberConfigUtil;
import com.github.kheera.plugin.bdd.java.steps.reference.CucumberJavaAnnotationProvider;
import com.github.kheera.plugin.bdd.psi.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static com.intellij.psi.util.PsiTreeUtil.getChildOfType;
import static com.intellij.psi.util.PsiTreeUtil.getChildrenOfTypeAsList;

/**
 * User: Andrey.Vokin
 * Date: 7/25/12
 */
public class CucumberJavaUtil {
  public static final String CUCUMBER_STEP_ANNOTATION_PREFIX_1_0 = "cucumber.annotation.";
  public static final String CUCUMBER_STEP_ANNOTATION_PREFIX_1_1 = "cucumber.api.java.";
  public static final String TEST_STEP_ANNOTATION_PREFIX_1_0 = "com.kheera.annotations.";

  private static String getCucumberAnnotationSuffix(@NotNull String name) {
    if (name.startsWith(CUCUMBER_STEP_ANNOTATION_PREFIX_1_0)) {
      return name.substring(CUCUMBER_STEP_ANNOTATION_PREFIX_1_0.length());
    }
    else if (name.startsWith(CUCUMBER_STEP_ANNOTATION_PREFIX_1_1)) {
      return name.substring(CUCUMBER_STEP_ANNOTATION_PREFIX_1_1.length());
    } else {
      name = "";
    }

    return name;
  }

  private static String getKheeraAnnotationName(@NotNull String name) {
    if (name.startsWith(TEST_STEP_ANNOTATION_PREFIX_1_0)) {
      return name.substring(TEST_STEP_ANNOTATION_PREFIX_1_0.length(), name.length());
    }

    return name;
  }


  @Nullable
  private static String getAnnotationName(@NotNull final PsiAnnotation annotation) {
    final Ref<String> qualifiedAnnotationName = new Ref<String>();
    ApplicationManager.getApplication().runReadAction(new Runnable() {
      public void run() {
        String qualifiedName = annotation.getQualifiedName();
        qualifiedAnnotationName.set(qualifiedName);
      }
    }
    );
    return qualifiedAnnotationName.get();
  }

  public static boolean isCucumberStepAnnotation(@NotNull final PsiAnnotation annotation) {
    final String annotationName = getAnnotationName(annotation);
    if (annotationName == null) return false;

    final String annotationSuffix = getKheeraAnnotationName(annotationName);
    for (String providedAnnotations : CucumberJavaAnnotationProvider.getCucumberStepAnnotations()) {
      if (providedAnnotations.equals(annotationSuffix)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isCucumberHookAnnotation(@NotNull final PsiAnnotation annotation) {
    final String annotationName = getAnnotationName(annotation);
    if (annotationName == null) return false;

    final String annotationSuffix = getCucumberAnnotationSuffix(annotationName);
    for (String providedAnnotations : CucumberJavaAnnotationProvider.getCucumberHookAnnotations()) {
      if (providedAnnotations.equals(annotationSuffix)) {
        return true;
      }
    }
    return false;
  }

  @Nullable
  public static PsiAnnotationMemberValue getAnnotationValue(@NotNull final PsiAnnotation stepAnnotation) {
    final PsiNameValuePair[] attributes = stepAnnotation.getParameterList().getAttributes();
    PsiNameValuePair valuePair = null;
    if (attributes.length > 0) {
      for (int i = 1; i < attributes.length; i++) {
        PsiNameValuePair pair = attributes[i];
        final String pairName = pair.getName();
        if (pairName != null && pairName.equals("value")) {
          valuePair = pair;
          break;
        }
      }
      if (valuePair == null) {
        valuePair = attributes[0];
      }
    }
    return valuePair != null ? valuePair.getValue() : null;
  }

  public static boolean isStepDefinition(@NotNull final PsiMethod method) {
    final PsiAnnotation stepAnnotation = getCucumberStepAnnotation(method);
    return stepAnnotation != null && getAnnotationValue(stepAnnotation) != null;
  }

  public static boolean isHook(@NotNull final PsiMethod method) {
    return getCucumberHookAnnotation(method) != null;
  }

  public static boolean isStepDefinitionClass(@NotNull final PsiClass clazz) {
    PsiMethod[] methods = clazz.getAllMethods();
    for (PsiMethod method : methods) {
      if (getCucumberStepAnnotation(method) != null || getCucumberHookAnnotation(method) != null) return true;
    }
    return false;
  }

  @Nullable
  public static PsiAnnotation getCucumberStepAnnotation(PsiMethod method) {
    final PsiAnnotation[] annotations = method.getModifierList().getAnnotations();

    for (PsiAnnotation annotation : annotations) {
      if (annotation != null && isCucumberStepAnnotation(annotation)) {
        return annotation;
      }
    }
    return null;
  }

  @Nullable
  public static PsiAnnotation getCucumberHookAnnotation(PsiMethod method) {
    final PsiAnnotation[] annotations = method.getModifierList().getAnnotations();

    for (PsiAnnotation annotation : annotations) {
      if (annotation != null && isCucumberHookAnnotation(annotation)) {
        return annotation;
      }
    }
    return null;
  }

  @Nullable
  public static String getPatternFromStepDefinition(@NotNull final PsiAnnotation stepAnnotation) {
    String result = null;
    if (stepAnnotation.getParameterList().getAttributes().length > 0) {
      final PsiElement annotationValue = stepAnnotation.getParameterList().getAttributes()[0].getValue();
      if (annotationValue != null) {
        final PsiElement patternLiteral = annotationValue.getFirstChild();
        if (patternLiteral != null) {
          final String patternContainer = patternLiteral.getText();
          result = patternContainer.substring(1, patternContainer.length() - 1).replace("\\\\", "\\");
        }
      }
    }
    return result;
  }

  @Nullable
  private static String getPackageOfStepDef(GherkinStep[] steps) {
    for (GherkinStep step : steps) {
      final String pack = getPackageOfStep(step);
      if (pack != null) return pack;
    }
    return null;
  }

  @NotNull
  public static String getPackageOfStepDef(final PsiElement element) {
    PsiFile file = element.getContainingFile();
    if (file instanceof GherkinFile) {
      GherkinFeature feature = getChildOfType(file, GherkinFeature.class);
      if (feature != null) {
        List<GherkinScenario> scenarioList = getChildrenOfTypeAsList(feature, GherkinScenario.class);
        for (GherkinScenario scenario : scenarioList) {
          String result = getPackageOfStepDef(scenario.getSteps());
          if (result != null) {
            return result;
          }
        }

        List<GherkinScenarioOutline> scenarioOutlineList = getChildrenOfTypeAsList(feature, GherkinScenarioOutline.class);
        for (GherkinScenarioOutline scenario : scenarioOutlineList) {
          String result = getPackageOfStepDef(scenario.getSteps());
          if (result != null) {
            return result;
          }
        }
      }
    }
    return "";
  }

  public static String getPackageOfStep(GherkinStep step) {
    for (PsiReference ref : step.getReferences()) {
      PsiElement refElement = ref.resolve();
      if (refElement != null && refElement instanceof PsiMethod) {
        PsiClassOwner file = (PsiClassOwner)refElement.getContainingFile();
        final String packageName = file.getPackageName();
        if (StringUtil.isNotEmpty(packageName)) {
          return packageName;
        }
      }
    }
    return null;
  }

  public static void addGlue(String glue, Set<String> glues) {
    boolean covered = false;
    final Set<String> toRemove = ContainerUtil.newHashSet();
    for (String existedGlue : glues) {
      if (glue.startsWith(existedGlue + ".")) {
        covered = true;
        break;
      }
      else if (existedGlue.startsWith(glue + ".")) {
        toRemove.add(existedGlue);
      }
    }

    for (String removing : toRemove) {
      glues.remove(removing);
    }

    if (!covered) {
      glues.add(glue);
    }
  }
}
