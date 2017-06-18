package org.dave.ocsensors.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrefixRegistry {
    private static Map<Class, List<String>> supportedPrefixes;

    public static void clearSupportedPrefixes(Class clazz) {
        if(supportedPrefixes == null) {
            return;
        }

        if(!supportedPrefixes.containsKey(clazz)) {
            return;
        }

        supportedPrefixes.remove(clazz);
    }

    public static void addSupportedPrefix(Class clazz, String rawPrefix) {
        if(supportedPrefixes == null) {
            supportedPrefixes = new HashMap<>();
        }

        if(!supportedPrefixes.containsKey(clazz)) {
            supportedPrefixes.put(clazz, new ArrayList<>());
        }

        List<String> supportedPrefixesForClass = supportedPrefixes.get(clazz);

        if(supportedPrefixesForClass.contains(rawPrefix)) {
            return;
        }

        supportedPrefixesForClass.add(rawPrefix);
    }

    public static boolean supportsPrefix(Class clazz, String prefix) {
        return supportedPrefixes != null && supportedPrefixes.get(clazz) != null && supportedPrefixes.get(clazz).contains(prefix);
    }
}
