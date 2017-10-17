package com.github.kheera.plugin.bdd.steps.reference;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;
import com.github.kheera.plugin.bdd.psi.impl.GherkinStepImpl;

public class CucumberReferenceContributor extends PsiReferenceContributor {
  @Override
  public void registerReferenceProviders(@NotNull final PsiReferenceRegistrar registrar) {
    registrar.registerReferenceProvider(PlatformPatterns.psiElement(GherkinStepImpl.class),
                                        new CucumberStepReferenceProvider());

  }
}
