package org.dave.ocsensors.integration;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanDataList {
    private Map<String, Object> data;

    public ScanDataList() {
        this.data = new HashMap<>();
    }

    public void add(String propertyPath, Object value) {
        recurseAdd(this.data, propertyPath, value);
    }

    private void recurseAdd(Map<String, Object> data, String propertyPath, Object value) {
        List<String> parts = Arrays.asList(propertyPath.split("\\."));
        if(parts.size() <= 0) {
            return;
        }

        String key = parts.get(0);
        if(parts.size() == 1) {
            data.put(key, value);
        } else {
            if(!data.containsKey(key)) {
                data.put(key, new HashMap<String, Object>());
            }

            String remaining = String.join("/", parts.subList(1, parts.size()));
            recurseAdd((Map<String, Object>) data.get(key), remaining, value);
        }
    }

    public Map<String, Object> getData() {
        return data;
    }
}
