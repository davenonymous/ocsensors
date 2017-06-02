package org.dave.ocsensors.integration;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.dave.ocsensors.utility.AnnotatedInstanceUtil;
import org.dave.ocsensors.utility.Logz;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IntegrationRegistry {
    private static List<AbstractIntegration> integrations = new ArrayList<>();

    public static void registerIntegrations(ASMDataTable asmData) {
        for(AbstractIntegration integration : AnnotatedInstanceUtil.getIntegrations(asmData)) {
            Logz.info("Registered integration class: %s", integration.getClass());
            integrations.add(integration);
        }
    }

    public static HashMap<String, Object> getDataForTileEntity(TileEntity entity, @Nullable EnumFacing side) {
        HashMap<String, Object> result = new HashMap<>();

        for(AbstractIntegration integration : integrations) {
            if(!integration.worksWith(entity, side)) {
                continue;
            }

            result.put(integration.getSectionName(), integration.getScanData(entity, side));
        }

        return result;
    }

    public static AbstractIntegration getIntegrationByName(String name) {
        for(AbstractIntegration integration : integrations) {
            if(integration.getSectionName().equalsIgnoreCase(name)) {
                return integration;
            }
        }

        return null;
    }
}
