package com.github.kheera.plugin.bdd.inspections.suppress;

import com.github.kheera.plugin.bdd.CucumberBundle;
import com.github.kheera.plugin.bdd.psi.GherkinFeature;
import com.intellij.codeInsight.daemon.impl.actions.AbstractBatchSuppressByNoInspectionCommentFix;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class GherkinSuppressForFeatureCommentFix extends AbstractBatchSuppressByNoInspectionCommentFix {
  GherkinSuppressForFeatureCommentFix(@NotNull final String toolId) {
    super(toolId, false);
  }

  @NotNull
  @Override
  public String getText() {
    return CucumberBundle.message("cucumber.inspection.suppress.feature");
  }

  @Override
  public PsiElement getContainer(PsiElement context) {
    // step
    return PsiTreeUtil.getNonStrictParentOfType(context, GherkinFeature.class);
  }
}
