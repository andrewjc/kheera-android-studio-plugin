package com.github.kheera.plugin.bdd.psi.impl;

import com.github.kheera.plugin.bdd.psi.GherkinElementVisitor;
import com.github.kheera.plugin.bdd.psi.GherkinTag;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author yole
 */
public class GherkinTagImpl extends GherkinPsiElementBase implements GherkinTag {
    public GherkinTagImpl(@NotNull final ASTNode node) {
        super(node);
    }

    @Override
    protected void acceptGherkin(GherkinElementVisitor gherkinElementVisitor) {
        gherkinElementVisitor.visitTag(this);
    }

    @Override
    public String getName() {
        return getText();
    }

    @Override
    public String toString() {
        return "GherkinTag:" + getText();
    }
}
