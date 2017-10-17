package com.github.kheera.plugin.bdd.java.steps;

import com.github.kheera.plugin.bdd.java.config.CucumberConfigUtil;
import com.github.kheera.plugin.bdd.psi.GherkinStep;
import com.github.kheera.plugin.bdd.psi.GherkinFile;

import static java.lang.String.format;

public class AnnotationPackageProvider {
  private static final String CUCUMBER_1_1_ANNOTATION_BASE_PACKAGE = "cucumber.api.java";
  private static final String CUCUMBER_1_0_ANNOTATION_BASE_PACKAGE = "cucumber.annotation";

  public AnnotationPackageProvider() {
    this(new CucumberVersionProvider());
  }

  public AnnotationPackageProvider(CucumberVersionProvider cucumberVersionProvider) {
    myVersionProvider = cucumberVersionProvider;
  }

  public String getAnnotationPackageFor(GherkinStep step) {
    return format("%s.%s", annotationBasePackage(step), locale(step));
  }

  private static String locale(GherkinStep step) {
    GherkinFile file = (GherkinFile)step.getContainingFile();
    return file.getLocaleLanguage().replaceAll("-", "_");
  }

  private final CucumberVersionProvider myVersionProvider;

  private String annotationBasePackage(GherkinStep step) {
    final String version = myVersionProvider.getVersion(step);
    if (version != null && version.compareTo(CucumberConfigUtil.CUCUMBER_VERSION_1_1) < 0) {
      return CUCUMBER_1_0_ANNOTATION_BASE_PACKAGE;
    }
    return CUCUMBER_1_1_ANNOTATION_BASE_PACKAGE;
  }
}