package org.dave.ocsensors.integration.nashorn;


import jdk.nashorn.api.scripting.JSObject;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.dave.ocsensors.integration.AbstractIntegration;
import org.dave.ocsensors.integration.Integrate;
import org.dave.ocsensors.integration.PrefixRegistry;
import org.dave.ocsensors.integration.ScanDataList;
import org.dave.ocsensors.misc.ConfigurationHandler;
import org.dave.ocsensors.utility.Logz;

import javax.annotation.Nullable;
import javax.script.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Integrate(name = "javascript")
public class NashornIntegration extends AbstractIntegration {
    private List<Invocable> scripts;

    @Override
    public void reload() {
        scripts = new ArrayList<>();
        PrefixRegistry.clearSupportedPrefixes(NashornIntegration.class);

        if(!ConfigurationHandler.nashornDataDir.exists()) {
            return;
        }

        for (File file : ConfigurationHandler.nashornDataDir.listFiles()) {
            Logz.info(" > Loading javascript integration from file: '%s'", file.getName());

            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            try {
                engine.eval(new FileReader(file));
                this.scripts.add((Invocable) engine);

                JSObject supportedPrefixes = (JSObject) ((Invocable) engine).invokeFunction("getSupportedPrefixes");
                if(supportedPrefixes != null) {
                    for (Object prefix : supportedPrefixes.values()) {
                        Logz.info("   > Supporting prefix: %s", prefix);
                        PrefixRegistry.addSupportedPrefix(NashornIntegration.class, (String) prefix);
                    }
                }
            } catch (ScriptException e) {
                Logz.warn("Could not compile+eval script: %s", file.getName());
                continue;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Logz.warn("Script %s is missing a method: %s", file.getName(), e);
            }
        }
    }

    @Override
    public boolean worksWith(TileEntity entity, @Nullable EnumFacing side) {
        for(Invocable script : scripts) {
            try {
                if((boolean) script.invokeFunction("worksWith", entity, side)) {
                    return true;
                }
            } catch (ScriptException e) {
                Logz.warn("Could not eval script!");
            } catch (NoSuchMethodException e) {
                Logz.warn("Script is missing 'worksWith' function.");
            }
        }

        return false;
    }

    @Override
    public void addScanData(ScanDataList data, TileEntity entity, @Nullable EnumFacing side) {
        for(Invocable script : scripts) {
            try {
                script.invokeFunction("addScanData", data, entity, side);
            } catch (ScriptException e) {
                Logz.warn("Could not eval script!");
            } catch (NoSuchMethodException e) {
                Logz.warn("Script is missing 'addScanData' function.");
            }
        }
    }
}
