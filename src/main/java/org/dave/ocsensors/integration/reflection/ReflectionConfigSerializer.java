package org.dave.ocsensors.integration.reflection;

import com.google.gson.*;
import org.dave.ocsensors.integration.PrefixRegistry;
import org.dave.ocsensors.utility.Logz;

import java.lang.reflect.Type;
import java.util.Map;

public class ReflectionConfigSerializer implements JsonDeserializer<ReflectionConfig> {

    @Override
    public ReflectionConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonArray()) {
            Logz.info("Invalid reflection config! Not a json array!");
            return null;
        }

        for (JsonElement entry : json.getAsJsonArray()) {
            if (!entry.isJsonObject()) {
                Logz.info("Invalid reflection config! Skipping entry: not a JSON object!");
                continue;
            }

            JsonObject teObject = entry.getAsJsonObject();
            if(!teObject.has("class")) {
                Logz.info("Invalid reflection config! Skipping TileEntity entry: missing 'class' property!");
                continue;
            }

            String className = teObject.get("class").getAsString();

            Class mappingClass;
            try {
                mappingClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                Logz.info("Class not found: %s", className);
                continue;
            }

            String prefix = "";
            if(teObject.has("prefix")) {
                String rawPrefix = teObject.get("prefix").getAsString();
                PrefixRegistry.addSupportedPrefix(ReflectionIntegration.class, rawPrefix);
                prefix =  rawPrefix + ".";
            }

            if(teObject.has("methods")) {
                for(Map.Entry<String, JsonElement> rule : teObject.get("methods").getAsJsonObject().entrySet()) {
                    if(rule.getValue().isJsonObject() && rule.getValue().getAsJsonObject().has("obf") && rule.getValue().getAsJsonObject().has("deobf")) {
                        String obfName = rule.getValue().getAsJsonObject().get("obf").getAsString();
                        String deobfName = rule.getValue().getAsJsonObject().get("deobf").getAsString();
                        ReflectionIntegration.addMethodMapping(mappingClass, prefix + rule.getKey(), deobfName, obfName);
                    } else if(rule.getValue().isJsonPrimitive()) {
                        String methodName = rule.getValue().getAsString();
                        ReflectionIntegration.addMethodMapping(mappingClass, prefix + rule.getKey(), methodName, methodName);
                    } else {
                        Logz.warn("Invalid method specification for class '%s' in reflection config.", className);
                    }
                }
            }

            if(teObject.has("fields")) {
                for(Map.Entry<String, JsonElement> rule : teObject.get("fields").getAsJsonObject().entrySet()) {
                    ReflectionIntegration.addFieldMapping(mappingClass, prefix + rule.getKey(), rule.getValue().getAsString());
                }
            }
        }

        return null;
    }
}
