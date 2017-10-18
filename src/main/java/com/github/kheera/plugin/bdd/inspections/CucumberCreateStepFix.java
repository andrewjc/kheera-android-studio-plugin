package com.github.kheera.plugin.bdd.inspections;

import com.github.kheera.plugin.bdd.BDDFrameworkType;
import com.github.kheera.plugin.bdd.CucumberBundle;
import com.github.kheera.plugin.bdd.psi.GherkinStep;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yole
 */
public class CucumberCreateStepFix extends CucumberCreateStepFixBase implements HighPriorityAction {
    @NotNull
    public String getName() {
        return CucumberBundle.message("cucumber.create.step.title");
    }

    @Override
    protected void createStepOrSteps(GherkinStep step, PsiFile file) {
        super.createStepOrSteps(step, file);
    }
}
