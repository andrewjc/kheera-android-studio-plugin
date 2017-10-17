package com.github.kheera.plugin.bdd;

import com.github.kheera.plugin.bdd.psi.GherkinSyntaxHighlighter;
import com.github.kheera.plugin.bdd.psi.PlainGherkinKeywordProvider;
import com.github.kheera.plugin.bdd.psi.impl.GherkinFileImpl;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Roman.Chernyatchik
 * @date Jun 24, 2009
 */
public class GherkinLiveTemplateContextType extends TemplateContextType {
  @NonNls
  private static final String CONTEXT_NAME = "CUCUMBER_FEATURE_FILE";

  public GherkinLiveTemplateContextType() {
    super(CONTEXT_NAME, CucumberBundle.message("live.templates.context.cucumber.name"));
  }

  public boolean isInContext(@NotNull final PsiFile file, final int offset) {
    return file instanceof GherkinFileImpl;
  }

  public SyntaxHighlighter createHighlighter() {
    return new GherkinSyntaxHighlighter(new PlainGherkinKeywordProvider());
  }
}
