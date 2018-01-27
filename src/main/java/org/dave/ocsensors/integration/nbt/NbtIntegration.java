package org.dave.ocsensors.integration.nbt;

import com.google.gson.stream.JsonReader;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.dave.ocsensors.integration.AbstractIntegration;
import org.dave.ocsensors.integration.Integrate;
import org.dave.ocsensors.integration.PrefixRegistry;
import org.dave.ocsensors.integration.ScanDataList;
import org.dave.ocsensors.misc.ConfigurationHandler;
import org.dave.ocsensors.utility.Logz;
import org.dave.ocsensors.utility.Serialization;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Integrate(name = "nbt")
public class NbtIntegration extends AbstractIntegration {
    private static Pattern pathRegex = Pattern.compile("(.*?)\\[(.*?)\\]");
    private static Map<Class, Map<String, String>> mappings;

    @Override
    public void reload() {
        this.mappings = new HashMap<>();
        PrefixRegistry.clearSupportedPrefixes(NbtIntegration.class);

        if(!ConfigurationHandler.nbtDataDir.exists()) {
            return;
        }

        for (File file : ConfigurationHandler.nbtDataDir.listFiles()) {
            try {
                Serialization.GSON.fromJson(new JsonReader(new FileReader(file)), NbtConfig.class);
            } catch (FileNotFoundException e) {
            }

            Logz.info(" > Loaded nbt config from file: '%s'", file.getName());
        }
    }

    public static void addMapping(Class clazz, String propertyPath, String fieldPath) {
        if(!mappings.containsKey(clazz)) {
            mappings.put(clazz, new HashMap<>());
        }

        mappings.get(clazz).put(propertyPath, fieldPath);
    }

    @Override
    public boolean worksWith(Entity entity) {
        for(Class clazz : this.mappings.keySet()) {
            if(clazz.isAssignableFrom(entity.getClass())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean worksWith(TileEntity entity, @Nullable EnumFacing side) {
        for(Class clazz : this.mappings.keySet()) {
            if(clazz.isAssignableFrom(entity.getClass())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void addScanData(ScanDataList data, Entity entity) {
        NBTTagCompound tag = new NBTTagCompound();
        entity.writeToNBT(tag);

        for(Map.Entry<Class, Map<String, String>> entry : this.mappings.entrySet()) {
            if(!entry.getKey().isAssignableFrom(entity.getClass())) {
                continue;
            }

            for(Map.Entry<String, String> rule : entry.getValue().entrySet()) {
                String propertyPath = rule.getKey();
                String fieldPath = rule.getValue();

                Object value = recurseNbtTag(tag, fieldPath);
                if(value != null) {
                    data.add(propertyPath, value);
                }
            }
        }
    }

    @Override
    public void addScanData(ScanDataList data, TileEntity entity, @Nullable EnumFacing side) {
        NBTTagCompound tag = new NBTTagCompound();
        entity.writeToNBT(tag);

        for(Map.Entry<Class, Map<String, String>> entry : this.mappings.entrySet()) {
            if(!entry.getKey().isAssignableFrom(entity.getClass())) {
                continue;
            }

            for(Map.Entry<String, String> rule : entry.getValue().entrySet()) {
                String propertyPath = rule.getKey();
                String fieldPath = rule.getValue();

                Object value = recurseNbtTag(tag, fieldPath);
                if(value != null) {
                    data.add(propertyPath, value);
                }
            }
        }
    }

    private static Object recurseNbtTag(NBTTagCompound pointer, String path) {
        List parts = Arrays.asList(path.split("/"));
        String entry = (String) parts.get(0);

        Matcher matcher = pathRegex.matcher(entry);
        if(matcher.matches()) {
            String type = matcher.group(1);
            String key = matcher.group(2);

            if(!pointer.hasKey(key)) {
                return null;
            }

            if(type.equalsIgnoreCase("string")) {
                return pointer.getString(key);
            } else if(type.equalsIgnoreCase("short")) {
                return pointer.getShort(key);
            } else if(type.equalsIgnoreCase("int")) {
                return pointer.getInteger(key);
            }

            Logz.warn("Unsupported nbt type: %s", type);
            return null;
        }

        if(parts.size() > 1) {
            String remaining = String.join("/", parts.subList(1, parts.size()));
            return recurseNbtTag(pointer.getCompoundTag(entry), remaining);
        }

        return null;
    }
}
