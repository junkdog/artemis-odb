package com.artemis.generator.util;

/**
 * @author Daan van Yperen
 */
public class Strings {

    public static String decapitalizeString(String string) {
        return string == null || string.isEmpty() ? "" : Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }
}
