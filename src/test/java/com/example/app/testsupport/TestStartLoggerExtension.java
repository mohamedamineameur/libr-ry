package com.example.app.testsupport;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestStartLoggerExtension implements BeforeEachCallback {

    private static final String PURPLE = "\u001B[35m";
    private static final String RESET = "\u001B[0m";

    @Override
    public void beforeEach(ExtensionContext context) {
        String title = context.getDisplayName();
        String functionName = context.getRequiredTestMethod().getName();
        System.out.println(PURPLE + "[TEST] title=" + title + " | function=" + functionName + RESET);
    }
}
