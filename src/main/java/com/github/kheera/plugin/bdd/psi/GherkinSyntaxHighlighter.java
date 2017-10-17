package com.github.kheera.plugin.bdd.psi;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yole
 */
public class GherkinSyntaxHighlighter extends SyntaxHighlighterBase {
  private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<>();

  private final GherkinKeywordProvider myKeywordProvider;

  public GherkinSyntaxHighlighter(GherkinKeywordProvider keywordProvider) {
    myKeywordProvider = keywordProvider;
  }

  static {
    ATTRIBUTES.put(GherkinTokenTypes.COMMENT, GherkinHighlighter.COMMENT);
    ATTRIBUTES.put(GherkinTokenTypes.TEXT, GherkinHighlighter.TEXT);
    ATTRIBUTES.put(GherkinTokenTypes.STEP_KEYWORD, GherkinHighlighter.KEYWORD);
    ATTRIBUTES.put(GherkinTokenTypes.TAG, GherkinHighlighter.TAG);
    ATTRIBUTES.put(GherkinTokenTypes.FEATURE_KEYWORD, GherkinHighlighter.KEYWORD);
    ATTRIBUTES.put(GherkinTokenTypes.SCENARIO_KEYWORD, GherkinHighlighter.KEYWORD);
    ATTRIBUTES.put(GherkinTokenTypes.BACKGROUND_KEYWORD, GherkinHighlighter.KEYWORD);
    ATTRIBUTES.put(GherkinTokenTypes.EXAMPLES_KEYWORD, GherkinHighlighter.KEYWORD);
    ATTRIBUTES.put(GherkinTokenTypes.SCENARIO_OUTLINE_KEYWORD, GherkinHighlighter.KEYWORD);
    ATTRIBUTES.put(GherkinTokenTypes.PYSTRING, GherkinHighlighter.PYSTRING);
    ATTRIBUTES.put(GherkinTokenTypes.PYSTRING_TEXT, GherkinHighlighter.PYSTRING);
    ATTRIBUTES.put(GherkinTokenTypes.TABLE_CELL, GherkinHighlighter.TABLE_CELL);
    ATTRIBUTES.put(GherkinTokenTypes.PIPE, GherkinHighlighter.PIPE);
    ATTRIBUTES.put(GherkinTokenTypes.COLON, GherkinHighlighter.KEYWORD);
  }

  @NotNull
  public Lexer getHighlightingLexer() {
    return new GherkinLexer(myKeywordProvider);
  }

  @NotNull
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    return SyntaxHighlighterBase.pack(ATTRIBUTES.get(tokenType));
  }
}
