package com.github.kheera.plugin.bdd.psi.impl;

import com.github.kheera.plugin.bdd.psi.GherkinTableRow;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;

/**
 * @author Roman.Chernyatchik
 * @date Sep 10, 2009
 */
public class GherkinTableNavigator {
  private GherkinTableNavigator() {
  }

  @Nullable
  public static GherkinTableImpl getTableByRow(final GherkinTableRow row) {
    final PsiElement element = row.getParent();
    return element instanceof GherkinTableImpl ? (GherkinTableImpl)element : null;
  }
}
