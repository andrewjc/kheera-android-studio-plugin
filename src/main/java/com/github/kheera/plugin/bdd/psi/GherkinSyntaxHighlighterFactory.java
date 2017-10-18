package com.github.kheera.plugin.bdd.psi;

import com.github.kheera.plugin.bdd.psi.i18n.JsonGherkinKeywordProvider;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author yole
 */
public class GherkinSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    @NotNull
    public SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile) {
        return new GherkinSyntaxHighlighter(JsonGherkinKeywordProvider.getKeywordProvider());
    }
}
