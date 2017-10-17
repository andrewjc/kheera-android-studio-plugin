package com.github.kheera.plugin.bdd.inspections.suppress;

import com.github.kheera.plugin.bdd.psi.GherkinStepsHolder;
import com.intellij.codeInsight.daemon.impl.actions.AbstractBatchSuppressByNoInspectionCommentFix;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import com.github.kheera.plugin.bdd.CucumberBundle;

public class GherkinSuppressForScenarioCommentFix extends AbstractBatchSuppressByNoInspectionCommentFix {
  GherkinSuppressForScenarioCommentFix(@NotNull final String toolId) {
    super(toolId, false);
  }

  @NotNull
  @Override
  public String getText() {
    return CucumberBundle.message("cucumber.inspection.suppress.scenario");
  }

  @Override
  public PsiElement getContainer(PsiElement context) {
    // steps holder
    return PsiTreeUtil.getNonStrictParentOfType(context, GherkinStepsHolder.class);
  }
}
