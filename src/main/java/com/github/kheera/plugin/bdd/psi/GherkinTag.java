package com.github.kheera.plugin.bdd.psi;

public interface GherkinTag extends GherkinPsiElement {
    GherkinTag[] EMPTY_ARRAY = new GherkinTag[0];

    public String getName();
}
