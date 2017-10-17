package com.github.kheera.plugin.bdd.psi;

/**
 * @author yole
 */
public interface GherkinFeature extends GherkinPsiElement, GherkinSuppressionHolder {
  String getFeatureName();
  GherkinStepsHolder[] getScenarios();
}
