package com.artemis.generator.util;

/**
 * Created by Daan on 10-9-2016.
 */
public class Strings {

    public static String decapitalizeString(String string) {
        return string == null || string.isEmpty() ? "" : Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }
}
