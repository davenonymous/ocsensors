package org.dave.ocsensors.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dave.ocsensors.integration.nbt.NbtConfig;
import org.dave.ocsensors.integration.nbt.NbtConfigSerializer;
import org.dave.ocsensors.integration.reflection.ReflectionConfig;
import org.dave.ocsensors.integration.reflection.ReflectionConfigSerializer;

public class Serialization {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(NbtConfig.class, new NbtConfigSerializer())
            .registerTypeAdapter(ReflectionConfig.class, new ReflectionConfigSerializer())
            .create();
}
