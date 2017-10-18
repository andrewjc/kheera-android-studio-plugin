package com.github.kheera.plugin.bdd.psi.impl;

import com.github.kheera.plugin.bdd.psi.GherkinElementVisitor;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author yole
 */
public class GherkinTableHeaderRowImpl extends GherkinTableRowImpl {
    public GherkinTableHeaderRowImpl(@NotNull final ASTNode node) {
        super(node);
    }

    @Override
    protected void acceptGherkin(GherkinElementVisitor gherkinElementVisitor) {
        gherkinElementVisitor.visitTableHeaderRow(this);
    }

    @Override
    public String toString() {
        return "GherkinTableHeaderRow";
    }
}