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
import org.dave.ocsensors.utility.ObfuscationReflectionHelperEx;
import org.dave.ocsensors.utility.ResourceLoader;
import org.dave.ocsensors.utility.Serialization;

import javax.annotation.Nullable;
import java.io.*;
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

        ResourceLoader loader = new ResourceLoader(ConfigurationHandler.reflectionDataDir, "assets/ocsensors/config/reflection/");
        for(Map.Entry<String, InputStream> entry : loader.getResources().entrySet()) {
            String filename = entry.getKey();
            InputStream is = entry.getValue();

            Logz.info(" > Loading reflection config from file: '%s'", filename);
            Serialization.GSON.fromJson(new JsonReader(new InputStreamReader(is)), ReflectionConfig.class);
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

    private void addUniversalScanData(ScanDataList data, Object entity) {
        for(Map.Entry<Class, Map<String, IReflectionMapping>> entry : classMappings.entrySet()) {
            Class clazz = entry.getKey();
            Map<String, IReflectionMapping> entriesForClazz = entry.getValue();

            // Logz.info("Is %s assignable from %s = %s", clazz.getName(), entity.getClass().getName(), clazz.isAssignableFrom(entity.getClass()));
            if(!clazz.isAssignableFrom(entity.getClass())) {
                continue;
            }

            for (Map.Entry<String, IReflectionMapping> mappingEntry : entriesForClazz.entrySet()) {
                String propertyPath = mappingEntry.getKey();
                IReflectionMapping mapping = mappingEntry.getValue();
                if(!mapping.isValid()) {
                    continue;
                }

                data.add(propertyPath, mapping.getResult(clazz, entity));
            }
        }
    }

    @Override
    public void addScanData(ScanDataList data, TileEntity entity, @Nullable EnumFacing side) {
        addUniversalScanData(data, entity);
    }

    @Override
    public void addScanData(ScanDataList data, Entity entity) {
        addUniversalScanData(data, entity);
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
            this.field = ObfuscationReflectionHelperEx.findField(clz, fieldName);
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
