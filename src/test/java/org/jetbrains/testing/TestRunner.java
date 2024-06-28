package org.jetbrains.testing;

import java.lang.reflect.Method;
import java.util.Map;

public class TestRunner {
    public static void main(String[] args) throws Exception {
        Map<String, Object[]> parameters = FuzzerConfig.generateTestParameters();
        TestTemplates tests = new TestTemplates();

        for (Map.Entry<String, Object[]> entry : parameters.entrySet()) {
            String testName = entry.getKey();
            Object[] argsForTest = entry.getValue();

            // Using reflection to call the right test method with dynamically generated parameters
            Method method = TestTemplates.class.getMethod(testName, getParameterClasses(argsForTest));
            method.invoke(tests, argsForTest);
        }
    }

    private static Class<?>[] getParameterClasses(Object[] params) {
        Class<?>[] classes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Integer) {
                classes[i] = int.class;
            } else if (params[i] instanceof Double) {
                classes[i] = double.class;
            } else if (params[i] instanceof String) {
                classes[i] = String.class;
            }
        }
        return classes;
    }
}
