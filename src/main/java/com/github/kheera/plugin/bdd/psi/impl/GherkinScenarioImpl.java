package com.github.kheera.plugin.bdd.psi.impl;

import com.github.kheera.plugin.bdd.psi.GherkinElementVisitor;
import com.github.kheera.plugin.bdd.psi.GherkinScenario;
import com.github.kheera.plugin.bdd.psi.GherkinTokenTypes;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author yole
 */
public class GherkinScenarioImpl extends GherkinStepsHolderBase implements GherkinScenario {
  public GherkinScenarioImpl(@NotNull final ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    if (isBackground()) {
      return "GherkinScenario(Background):";
    }
    return "GherkinScenario:" + getScenarioName();
  }

  public boolean isBackground() {
    return getNode().getFirstChildNode().getElementType() == GherkinTokenTypes.BACKGROUND_KEYWORD;
  }

  @Override
  protected String getPresentableText() {
    return buildPresentableText(isBackground() ? "Background" : "Scenario");
  }

  protected void acceptGherkin(GherkinElementVisitor gherkinElementVisitor) {
    gherkinElementVisitor.visitScenario(this);
  }
}
