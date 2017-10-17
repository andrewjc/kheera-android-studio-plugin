package com.github.kheera.plugin.bdd.psi.impl;

import com.github.kheera.plugin.bdd.psi.GherkinElementVisitor;
import com.github.kheera.plugin.bdd.psi.GherkinPystring;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class GherkinPystringImpl extends GherkinPsiElementBase implements GherkinPystring {
  public GherkinPystringImpl(@NotNull final ASTNode node) {
    super(node);
  }

  @Override
  protected void acceptGherkin(GherkinElementVisitor gherkinElementVisitor) {
    gherkinElementVisitor.visitPystring(this);
  }

  @Override
  public String toString() {
    return "GherkinPystring";
  }
}
