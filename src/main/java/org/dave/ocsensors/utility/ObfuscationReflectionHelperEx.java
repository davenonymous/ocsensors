package org.dave.ocsensors.utility;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public class ObfuscationReflectionHelperEx {
    public static Field findField(Class clz, String fieldName) {
        String[] obfNames = ObfuscationReflectionHelper.remapFieldNames(clz.getName(), fieldName);
        if(obfNames.length == 0) {
            return null;
        }

        try {
            return ReflectionHelper.findField(clz, obfNames);
        } catch (ReflectionHelper.UnableToFindFieldException e) {
            Logz.warn("Could not find field '%s' in class '%s'! Exception=%s", fieldName, clz.getName(), e);
        }

        return null;
    }
}
