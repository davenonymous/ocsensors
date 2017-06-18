package org.dave.ocsensors.integration.reflection;

import com.google.gson.stream.JsonReader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
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
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Integrate
public class ReflectionIntegration extends AbstractIntegration {
    private static Map<Class, Map<String, String>> methodMappings;
    private static Map<Class, Map<String, String>> fieldMappings;
    private static Map<Class, Map<String, String>> privateFieldMappings;

    @Override
    public void reload() {
        this.methodMappings = new HashMap<>();
        this.fieldMappings = new HashMap<>();
        this.privateFieldMappings = new HashMap<>();
        PrefixRegistry.clearSupportedPrefixes(ReflectionIntegration.class);

        if(!ConfigurationHandler.reflectionDataDir.exists()) {
            return;
        }

        for (File file : ConfigurationHandler.reflectionDataDir.listFiles()) {
            try {
                Serialization.GSON.fromJson(new JsonReader(new FileReader(file)), ReflectionConfig.class);
            } catch (FileNotFoundException e) {
            }

            Logz.info(" > Loaded reflection config from file: '%s'", file.getName());
        }
    }

    public static void addMethodMapping(Class clazz, String propertyPath, String methodName) {
        if(!methodMappings.containsKey(clazz)) {
            methodMappings.put(clazz, new HashMap<>());
        }

        methodMappings.get(clazz).put(propertyPath, methodName);
    }

    public static void addFieldMapping(Class clazz, String propertyPath, String fieldName) {
        if(!fieldMappings.containsKey(clazz)) {
            fieldMappings.put(clazz, new HashMap<>());
        }

        fieldMappings.get(clazz).put(propertyPath, fieldName);
    }

    public static void addPrivateFieldMapping(Class clazz, String propertyPath, String methodName) {
        if(!privateFieldMappings.containsKey(clazz)) {
            privateFieldMappings.put(clazz, new HashMap<>());
        }

        privateFieldMappings.get(clazz).put(propertyPath, methodName);
    }



    @Override
    public boolean worksWith(TileEntity entity, @Nullable EnumFacing side) {
        if(methodMappings.keySet().stream().anyMatch(c -> c.isAssignableFrom(entity.getClass()))) {
            return true;
        }

        if(fieldMappings.keySet().stream().anyMatch(c -> c.isAssignableFrom(entity.getClass()))) {
            return true;
        }

        if(privateFieldMappings.keySet().stream().anyMatch(c -> c.isAssignableFrom(entity.getClass()))) {
            return true;
        }

        return false;
    }



    @FunctionalInterface
    interface Function2 <C, A, B> {
        public void apply (C c, A a, B b);
    }

    public static void processMapping(TileEntity entity, Map<Class, Map<String, String>> mapping, Function2<Class, String, String> f) {
        for(Map.Entry<Class, Map<String, String>> entry : mapping.entrySet()) {
            Class clazz = entry.getKey();
            if (!clazz.isAssignableFrom(entity.getClass())) {
                continue;
            }

            for(Map.Entry<String, String> rule : entry.getValue().entrySet()) {
                String propertyPath = rule.getKey();
                String fieldPath = rule.getValue();

                f.apply(clazz, propertyPath, fieldPath);
            }
        }
    }


    @Override
    public void addScanData(ScanDataList data, TileEntity entity, @Nullable EnumFacing side) {
        processMapping(entity, this.methodMappings, (clazz, propertyPath, methodName) -> {
            try {
                data.add(propertyPath, clazz.getDeclaredMethod(methodName).invoke(entity));
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            } catch (NoSuchMethodException e) {
            }
        });

        processMapping(entity, this.fieldMappings, (clazz, propertyPath, fieldName) -> {
            try {
                data.add(propertyPath, clazz.getField(fieldName).get(entity));
            } catch (IllegalAccessException e) {
            } catch (NoSuchFieldException e) {
            }
        });

        processMapping(entity, this.privateFieldMappings, (clazz, propertyPath, privateFieldName) -> {
            data.add(propertyPath, ObfuscationReflectionHelper.getPrivateValue(clazz, entity, privateFieldName));
        });
    }
}
