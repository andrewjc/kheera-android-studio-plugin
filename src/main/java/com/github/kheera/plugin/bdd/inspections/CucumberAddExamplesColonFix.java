package com.github.kheera.plugin.bdd.inspections;

import com.github.kheera.plugin.bdd.psi.GherkinElementFactory;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author Dennis.Ushakov
 */
public class CucumberAddExamplesColonFix implements LocalQuickFix {

    @NotNull
    public String getFamilyName() {
        return "Add missing ':' after examples keyword";
    }


    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        final PsiElement examples = descriptor.getPsiElement();
        final PsiElement[] elements = GherkinElementFactory.getTopLevelElements(project, ":");
        examples.getParent().addAfter(elements[0], examples);
    }
}
