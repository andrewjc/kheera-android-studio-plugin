package com.github.kheera.plugin.bdd.psi.structure;

import com.github.kheera.plugin.bdd.psi.GherkinFeature;
import com.github.kheera.plugin.bdd.psi.GherkinFile;
import com.github.kheera.plugin.bdd.psi.GherkinStep;
import com.github.kheera.plugin.bdd.psi.GherkinStepsHolder;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yole
 */
public class GherkinStructureViewFactory implements PsiStructureViewFactory {
    @Override
    public StructureViewBuilder getStructureViewBuilder(@NotNull final PsiFile psiFile) {
        return new TreeBasedStructureViewBuilder() {
            @NotNull
            @Override
            public StructureViewModel createStructureViewModel(@Nullable Editor editor) {
                PsiElement root = PsiTreeUtil.getChildOfType(psiFile, GherkinFeature.class);
                if (root == null) {
                    root = psiFile;
                }
                return
                        new StructureViewModelBase(psiFile, editor, new GherkinStructureViewElement(root))
                                .withSuitableClasses(GherkinFile.class, GherkinFeature.class, GherkinStepsHolder.class, GherkinStep.class);
            }
        };
    }
}