package org.dave.ocsensors.integration;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.dave.ocsensors.utility.Logz;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractIntegration {
    private static Map<Class, List<String>> supportedPrefixes;

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

    public abstract void init();

    public abstract boolean worksWith(TileEntity entity, @Nullable EnumFacing side);

    public abstract void addScanData(ScanDataList data, TileEntity entity, @Nullable EnumFacing side);
}
