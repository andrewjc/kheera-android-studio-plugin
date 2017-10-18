package com.github.kheera.plugin.bdd.psi.formatter;

import com.github.kheera.plugin.bdd.psi.GherkinLanguage;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.psi.codeStyle.CodeStyleSettings;

/**
 * @author Rustam Vishnyakov
 */
public class GherkinCodeStylePanel extends TabbedLanguageCodeStylePanel {
    protected GherkinCodeStylePanel(CodeStyleSettings currentSettings, CodeStyleSettings settings) {
        super(GherkinLanguage.INSTANCE, currentSettings, settings);
    }

    @Override
    protected void initTabs(CodeStyleSettings settings) {
        addIndentOptionsTab(settings);
    }
}
