package com.github.kheera.plugin.bdd.psi.structure;

import com.github.kheera.plugin.bdd.psi.*;
import com.github.kheera.plugin.bdd.icons.CucumberIcons;
import com.github.kheera.plugin.bdd.psi.impl.GherkinFeatureHeaderImpl;
import com.github.kheera.plugin.bdd.psi.impl.GherkinTableImpl;
import com.github.kheera.plugin.bdd.psi.impl.GherkinTagImpl;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author yole
 */
public class GherkinStructureViewElement extends PsiTreeElementBase<PsiElement> {
  protected GherkinStructureViewElement(PsiElement psiElement) {
    super(psiElement);
  }

  @NotNull
  public Collection<StructureViewTreeElement> getChildrenBase() {
    List<StructureViewTreeElement> result = new ArrayList<>();
    for (PsiElement element : getElement().getChildren()) {
      if (element instanceof GherkinPsiElement &&
          !(element instanceof GherkinFeatureHeaderImpl) &&
          !(element instanceof GherkinTableImpl) &&
          !(element instanceof GherkinTagImpl) &&
          !(element instanceof GherkinPystring)) {
        result.add(new GherkinStructureViewElement(element));
      }
    }
    return result;
  }

  @Override
  public Icon getIcon(boolean open) {
    final PsiElement element = getElement();
    if (element instanceof GherkinFeature
        || element instanceof GherkinStepsHolder) {
      return open ? CucumberIcons.Steps_group_opened : CucumberIcons.Steps_group_closed;
    }
    if (element instanceof GherkinStep) {
      return CucumberIcons.Cucumber;
    }
    return null;
  }


  public String getPresentableText() {
    return ((NavigationItem) getElement()).getPresentation().getPresentableText();
  }
}
