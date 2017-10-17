package com.github.kheera.plugin.bdd.psi.impl;

import com.github.kheera.plugin.bdd.psi.GherkinElementTypes;
import com.github.kheera.plugin.bdd.psi.GherkinElementVisitor;
import com.github.kheera.plugin.bdd.psi.GherkinExamplesBlock;
import com.github.kheera.plugin.bdd.psi.GherkinTable;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yole
 */
public class GherkinExamplesBlockImpl extends GherkinPsiElementBase implements GherkinExamplesBlock {
  private static final TokenSet TABLE_FILTER = TokenSet.create(GherkinElementTypes.TABLE);

  public GherkinExamplesBlockImpl(@NotNull final ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "GherkinExamplesBlock:" + getElementText();
  }

  @Override
  protected String getPresentableText() {
    return buildPresentableText("Examples");
  }

  protected void acceptGherkin(GherkinElementVisitor gherkinElementVisitor) {
    gherkinElementVisitor.visitExamplesBlock(this);
  }

  @Nullable
  public GherkinTable getTable() {
    final ASTNode node = getNode();

    final ASTNode tableNode = node.findChildByType(TABLE_FILTER);
    return tableNode == null ? null : (GherkinTable)tableNode.getPsi();
  }
}
