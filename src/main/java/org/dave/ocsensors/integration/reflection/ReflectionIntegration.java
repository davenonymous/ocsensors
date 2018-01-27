package org.dave.ocsensors.integration.reflection;

import com.google.gson.stream.JsonReader;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Integrate(name = "reflection")
public class ReflectionIntegration extends AbstractIntegration {
    private static Map<Class, Map<String, IReflectionMapping>> classMappings;
    private static Set<Class> supportedClasses;

    @Override
    public void reload() {
        this.classMappings = new HashMap<>();
        this.supportedClasses = new HashSet<>();

        PrefixRegistry.clearSupportedPrefixes(ReflectionIntegration.class);

        if(!ConfigurationHandler.reflectionDataDir.exists()) {
            return;
        }

        // TODO: Switch to ResourceLoader class
        for (File file : ConfigurationHandler.reflectionDataDir.listFiles()) {
            try {
                Serialization.GSON.fromJson(new JsonReader(new FileReader(file)), ReflectionConfig.class);
            } catch (FileNotFoundException e) {
            }

            Logz.info(" > Loaded reflection config from file: '%s'", file.getName());
        }
    }

    private static void initClassMapping(Class clazz) {
        if(!supportedClasses.contains(clazz)) {
            supportedClasses.add(clazz);
        }

        if(!classMappings.containsKey(clazz)) {
            classMappings.put(clazz, new HashMap<>());
        }
    }

    public static void addMethodMapping(Class clazz, String propertyPath, String methodName, String obfName) {
        initClassMapping(clazz);

        classMappings.get(clazz).put(propertyPath, new MethodMapping(clazz, methodName, obfName));
    }

    public static void addFieldMapping(Class clazz, String propertyPath, String fieldName) {
        initClassMapping(clazz);

        classMappings.get(clazz).put(propertyPath, new FieldMapping(clazz, fieldName));
    }

    @Override
    public boolean worksWith(TileEntity entity, @Nullable EnumFacing side) {
        return supportedClasses.stream().anyMatch(c -> c.isAssignableFrom(entity.getClass()));
    }

    @Override
    public boolean worksWith(Entity entity) {
        return supportedClasses.stream().anyMatch(c -> c.isAssignableFrom(entity.getClass()));
    }


    @Override
    public void addScanData(ScanDataList data, TileEntity entity, @Nullable EnumFacing side) {
        for(Map.Entry<Class, Map<String, IReflectionMapping>> entry : classMappings.entrySet()) {
            Class clazz = entry.getClass();
            Map<String, IReflectionMapping> entriesForClazz = entry.getValue();

            for (Map.Entry<String, IReflectionMapping> mappingEntry : entriesForClazz.entrySet()) {
                String propertyPath = mappingEntry.getKey();
                IReflectionMapping mapping = mappingEntry.getValue();

                data.add(propertyPath, mapping.getResult(clazz, entity));
            }
        }
    }



    @Override
    public void addScanData(ScanDataList data, Entity entity) {
        for(Map.Entry<Class, Map<String, IReflectionMapping>> entry : classMappings.entrySet()) {
            Class clazz = entry.getClass();
            Map<String, IReflectionMapping> entriesForClazz = entry.getValue();

            for (Map.Entry<String, IReflectionMapping> mappingEntry : entriesForClazz.entrySet()) {
                String propertyPath = mappingEntry.getKey();
                IReflectionMapping mapping = mappingEntry.getValue();

                data.add(propertyPath, mapping.getResult(clazz, entity));
            }
        }

    }


    private interface IReflectionMapping {
        Object getResult(Class clz, Object entity);

        boolean isValid();
    }

    private static class MethodMapping implements IReflectionMapping {
        private String methodName;
        private Method method = null;

        public MethodMapping(Class clz, String methodName, String obfMethodName) {
            this.methodName = methodName;
            try {
                this.method = ReflectionHelper.findMethod(clz, methodName, obfMethodName);
            } catch(ReflectionHelper.UnableToFindMethodException e) {
                Logz.warn("Could not find method '%s' in class '%s'! Exception=%s", this.methodName, clz.getName(), e);
            }
        }

        @Override
        public Object getResult(Class clz, Object entity) {
            try {
                return this.method.invoke(entity);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }

            return null;
        }

        @Override
        public boolean isValid() {
            return this.method != null;
        }
    }

    private static class FieldMapping implements IReflectionMapping {
        private String fieldName;
        private Field field = null;

        public FieldMapping(Class clz, String fieldName) {
            this.fieldName = fieldName;
            String[] obfNames = ObfuscationReflectionHelper.remapFieldNames(clz.getName(), fieldName);
            if(obfNames.length == 0) {
                return;
            }

            try {
                this.field = ReflectionHelper.findField(clz, obfNames);
            } catch (ReflectionHelper.UnableToFindFieldException e) {
                Logz.warn("Could not find field '%s' in class '%s'! Exception=%s", this.fieldName, clz.getName(), e);
                this.field = null;
            }

        }

        @Override
        public Object getResult(Class clz, Object entity) {
            try {
                return this.field.get(entity);
            } catch (IllegalAccessException e) {
                return null;
            }
        }

        @Override
        public boolean isValid() {
            return field != null;
        }
    }
}
