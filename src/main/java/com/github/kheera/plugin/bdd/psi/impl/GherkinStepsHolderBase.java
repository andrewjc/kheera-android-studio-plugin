package com.github.kheera.plugin.bdd.psi.impl;

import com.github.kheera.plugin.bdd.psi.GherkinStep;
import com.github.kheera.plugin.bdd.psi.GherkinStepsHolder;
import com.github.kheera.plugin.bdd.psi.GherkinTag;
import com.github.kheera.plugin.bdd.psi.GherkinTokenTypes;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public abstract class GherkinStepsHolderBase extends GherkinPsiElementBase implements GherkinStepsHolder {
    protected GherkinStepsHolderBase(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public final String getScenarioName() {
        final StringBuilder result = new StringBuilder();

        ASTNode node = getNode().getFirstChildNode();
        while (node != null && node.getElementType() != GherkinTokenTypes.COLON) {
            node = node.getTreeNext();
        }
        if (node != null) {
            node = node.getTreeNext();
        }

        while (node != null && !node.getText().contains("\n")) {
            result.append(node.getText());
            node = node.getTreeNext();
        }
        return result.toString().trim();
    }

    @NotNull
    @Override
    public final GherkinStep[] getSteps() {
        final GherkinStep[] steps = PsiTreeUtil.getChildrenOfType(this, GherkinStep.class);
        return steps == null ? GherkinStep.EMPTY_ARRAY : steps;
    }

    @Override
    public final GherkinTag[] getTags() {
        final GherkinTag[] tags = PsiTreeUtil.getChildrenOfType(this, GherkinTag.class);
        return tags == null ? GherkinTag.EMPTY_ARRAY : tags;
    }
}
