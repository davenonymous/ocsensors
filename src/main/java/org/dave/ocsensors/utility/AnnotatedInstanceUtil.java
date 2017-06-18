package org.dave.ocsensors.utility;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.dave.ocsensors.integration.AbstractIntegration;
import org.dave.ocsensors.integration.Integrate;
import org.dave.ocsensors.misc.ConfigurationHandler;

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

                if(info.containsKey("name")) {
                    String integrationName = (String) info.get("name");

                    if(ConfigurationHandler.IntegrationSettings.disabledIntegrations.contains(integrationName)) {
                        Logz.info("Skipping '%s', integration '%s' is disabled in the config", asmData.getClassName(), integrationName);
                        continue;
                    } else {
                        Logz.info("Loading '%s', integration='%s'.", asmData.getClassName(), integrationName);
                    }
                } else {
                    Logz.info("Skipping '%s', missing 'name' parameter in annotation", asmData.getClassName());
                    continue;
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
