package com.github.kheera.plugin.bdd.inspections;

import com.github.kheera.plugin.bdd.CucumberBundle;
import com.intellij.codeInspection.LocalInspectionTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author Roman.Chernyatchik
 */
public abstract class GherkinInspection extends LocalInspectionTool {
    @NotNull
    public String getGroupDisplayName() {
        return CucumberBundle.message("cucumber.inspection.group.name");
    }

}