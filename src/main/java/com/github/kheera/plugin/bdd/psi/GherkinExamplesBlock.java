package com.github.kheera.plugin.bdd.psi;

/**
 * @author yole
 */
public interface GherkinExamplesBlock extends GherkinPsiElement {
    GherkinTable getTable();
}
