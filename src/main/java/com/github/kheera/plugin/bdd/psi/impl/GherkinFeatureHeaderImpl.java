package com.github.kheera.plugin.bdd.psi.impl;

import com.github.kheera.plugin.bdd.psi.GherkinElementVisitor;
import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;

/**
 * @author yole
 */
public class GherkinFeatureHeaderImpl extends GherkinPsiElementBase {
  public GherkinFeatureHeaderImpl(@NotNull final ASTNode node) {
    super(node);
  }

  @Override
  protected void acceptGherkin(GherkinElementVisitor gherkinElementVisitor) {
    gherkinElementVisitor.visitFeatureHeader(this);
  }

  @Override
  public String toString() {
    return "GherkinFeatureHeader";
  }
}
