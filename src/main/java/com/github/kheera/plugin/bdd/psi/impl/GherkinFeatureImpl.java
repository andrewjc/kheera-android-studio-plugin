package com.github.kheera.plugin.bdd.psi.impl;

import com.github.kheera.plugin.bdd.psi.GherkinElementVisitor;
import com.github.kheera.plugin.bdd.psi.GherkinFeature;
import com.github.kheera.plugin.bdd.psi.GherkinStepsHolder;
import com.github.kheera.plugin.bdd.psi.GherkinTokenTypes;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author yole
 */
public class GherkinFeatureImpl extends GherkinPsiElementBase implements GherkinFeature {
    public GherkinFeatureImpl(@NotNull final ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "GherkinFeature:" + getFeatureName();
    }

    public String getFeatureName() {
        ASTNode node = getNode();
        final ASTNode firstText = node.findChildByType(GherkinTokenTypes.TEXT);
        if (firstText != null) {
            return firstText.getText();
        }
        final GherkinFeatureHeaderImpl header = PsiTreeUtil.getChildOfType(this, GherkinFeatureHeaderImpl.class);
        if (header != null) {
            return header.getElementText();
        }
        return getElementText();
    }

    public GherkinStepsHolder[] getScenarios() {
        final GherkinStepsHolder[] children = PsiTreeUtil.getChildrenOfType(this, GherkinStepsHolder.class);
        return children == null ? GherkinStepsHolder.EMPTY_ARRAY : children;
    }

    @Override
    protected String getPresentableText() {
        return "Feature: " + getFeatureName();
    }

    protected void acceptGherkin(GherkinElementVisitor gherkinElementVisitor) {
        gherkinElementVisitor.visitFeature(this);
    }
}
