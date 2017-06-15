package org.dave.ocsensors.integration.nbt;

import com.google.gson.*;
import org.dave.ocsensors.integration.AbstractIntegration;
import org.dave.ocsensors.integration.reflection.ReflectionIntegration;
import org.dave.ocsensors.utility.Logz;

import java.lang.reflect.Type;
import java.util.Map;

public class NbtConfigSerializer implements JsonDeserializer<NbtConfig> {
    @Override
    public NbtConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonArray()) {
            Logz.info("Invalid nbt config! Not a json array!");
            return null;
        }

        for (JsonElement entry : json.getAsJsonArray()) {
            if(!entry.isJsonObject()) {
                Logz.info("Invalid nbt config! Skipping entry: not a JSON object!");
                continue;
            }

            JsonObject teObject = entry.getAsJsonObject();
            if(!teObject.has("class")) {
                Logz.info("Invalid nbt config! Skipping TileEntity entry: missing 'class' property!");
                continue;
            }

            if(!teObject.has("rules")) {
                Logz.info("Invalid nbt config! Skipping TileEntity entry: missing 'rules' property!");
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
                AbstractIntegration.addSupportedPrefix(NbtIntegration.class, rawPrefix);
                prefix =  rawPrefix + ".";
            }

            JsonObject rules = teObject.get("rules").getAsJsonObject();
            for(Map.Entry<String, JsonElement> rule : rules.entrySet()) {
                NbtIntegration.addMapping(mappingClass, prefix + rule.getKey(), rule.getValue().getAsString());
            }
        }

        return null;
    }
}
