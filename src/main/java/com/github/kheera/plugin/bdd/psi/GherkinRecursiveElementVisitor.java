package com.github.kheera.plugin.bdd.psi;

import com.intellij.psi.PsiElement;

/**
 * @author yole
 */
public class GherkinRecursiveElementVisitor extends GherkinElementVisitor {
    @Override
    public void visitElement(PsiElement element) {
        element.acceptChildren(this);
    }
}
