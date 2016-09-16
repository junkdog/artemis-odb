package com.artemis.generator.util;

import org.apache.commons.lang3.StringUtils;

/**
 * String utility methods.
 *
 * @author Daan van Yperen
 */
public class Strings {
    public static String decapitalizeString(String string) {
        return string == null || string.isEmpty() ? "" : Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }

    public static String capitalizeString(String string) {
        return string == null || string.isEmpty() ? "" : Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }


    /**
     * Returns method name.
     * <p>
     * For alphanumerical or empty prefixes lowercase, otherwise uppercase.
     */
    public static String assembleMethodName(String prefix, String suffix) {

        if (StringUtils.isEmpty(prefix)) {
            return decapitalizeString(suffix);
        }

        if (!StringUtils.isAlphanumeric(prefix)) {
            return prefix + decapitalizeString(suffix);
        }

        return prefix + capitalizeString(suffix);
    }
}
