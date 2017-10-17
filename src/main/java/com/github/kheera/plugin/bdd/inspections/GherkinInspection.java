package com.github.kheera.plugin.bdd.inspections;

import com.intellij.codeInspection.*;
import org.jetbrains.annotations.NotNull;
import com.github.kheera.plugin.bdd.CucumberBundle;

/**
 * @author Roman.Chernyatchik
 */
public abstract class GherkinInspection extends LocalInspectionTool {
  @NotNull
  public String getGroupDisplayName() {
    return CucumberBundle.message("cucumber.inspection.group.name");
  }

}