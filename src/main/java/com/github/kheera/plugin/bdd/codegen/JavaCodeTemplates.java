package com.github.kheera.plugin.bdd.codegen;

import com.intellij.psi.PsiFile;

public class JavaCodeTemplates {
    public static String createNewTestModule(final String name, final PsiFile featureFile) {
        final StringBuilder sbl = new StringBuilder();
        sbl.append("@TestModule(featureFile = \"");
        sbl.append(featureFile.getName());
        sbl.append("\")\n");
        sbl.append("public class ");
        sbl.append(name);
        sbl.append("implements StepDefinition");
        sbl.append("{");
        sbl.append("}");
        return sbl.toString();
    }
}
