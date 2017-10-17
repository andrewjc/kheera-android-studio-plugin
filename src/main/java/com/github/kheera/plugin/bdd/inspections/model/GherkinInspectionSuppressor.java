package com.github.kheera.plugin.bdd.inspections.model;

import com.github.kheera.plugin.bdd.inspections.suppress.GherkinSuppressionUtil;
import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GherkinInspectionSuppressor implements InspectionSuppressor {
  @Override
  public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String toolId) {
    return GherkinSuppressionUtil.isSuppressedFor(element, toolId);
  }

  @NotNull
  @Override
  public SuppressQuickFix[] getSuppressActions(@Nullable PsiElement element, @NotNull String toolId) {
    return GherkinSuppressionUtil.getDefaultSuppressActions(toolId);
  }
}
