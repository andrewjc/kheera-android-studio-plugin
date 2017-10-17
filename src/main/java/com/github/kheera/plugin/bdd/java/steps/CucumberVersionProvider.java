package com.github.kheera.plugin.bdd.java.steps;

import com.github.kheera.plugin.bdd.java.config.CucumberConfigUtil;
import com.github.kheera.plugin.bdd.psi.GherkinStep;

public class CucumberVersionProvider {
  public String getVersion(GherkinStep step) {
    return CucumberConfigUtil.getCucumberCoreVersion(step);
  }
}