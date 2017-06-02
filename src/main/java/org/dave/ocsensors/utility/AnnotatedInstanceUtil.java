package org.dave.ocsensors.utility;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.dave.ocsensors.integration.AbstractIntegration;
import org.dave.ocsensors.integration.Integrate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnnotatedInstanceUtil {
    private AnnotatedInstanceUtil() {
    }

    public static List<AbstractIntegration> getIntegrations(ASMDataTable asmDataTable) {
        return getInstances(asmDataTable, Integrate.class, AbstractIntegration.class);
    }

    private static <T> List<T> getInstances(ASMDataTable asmDataTable, Class annotationClass, Class<T> instanceClass) {
        String annotationClassName = annotationClass.getCanonicalName();
        Set<ASMDataTable.ASMData> asmDatas = asmDataTable.getAll(annotationClassName);
        List<T> instances = new ArrayList<T>();
        for (ASMDataTable.ASMData asmData : asmDatas) {
            try {
                Map<String, Object> info = asmData.getAnnotationInfo();
                if(info.containsKey("mod")) {
                    String mod = (String) info.get("mod");
                    if(!Loader.isModLoaded(mod)) {
                        Logz.info("Skipping '%s', mod '%s' is not loaded", asmData.getClassName(), mod);
                        continue;
                    }
                }

                if(info.containsKey("minecraft_version")) {
                    String version = (String) info.get("minecraft_version");
                    if(!version.equals(Loader.MC_VERSION)) {
                        Logz.info("Found version: %s", Loader.MC_VERSION);
                        Logz.info("Skipping '%s', integration is for version '%s'", asmData.getClassName(), version);
                        continue;
                    }
                }

                Class<?> asmClass = Class.forName(asmData.getClassName());
                Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
                T instance = asmInstanceClass.newInstance();
                instances.add(instance);
            } catch (ClassNotFoundException e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            } catch (IllegalAccessException e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            } catch (InstantiationException e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            } catch (ExceptionInInitializerError e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            }
        }
        return instances;
    }
}
