package com.github.kheera.plugin.bdd.steps.search;

import com.github.kheera.plugin.bdd.psi.GherkinFileType;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;

public class CucumberStepSearchUtil {
  public static SearchScope restrictScopeToGherkinFiles(final Computable<SearchScope> originalScopeComputation) {
    return ReadAction.compute(() -> {
      final SearchScope originalScope = originalScopeComputation.compute();
      if (originalScope instanceof GlobalSearchScope) {
        return GlobalSearchScope.getScopeRestrictedByFileTypes((GlobalSearchScope)originalScope,
                                                               GherkinFileType.INSTANCE);
      }
      return originalScope;
    });
  }
}
