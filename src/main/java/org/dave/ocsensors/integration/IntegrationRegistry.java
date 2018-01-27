package org.dave.ocsensors.integration;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.dave.ocsensors.utility.AnnotatedInstanceUtil;
import org.dave.ocsensors.utility.Logz;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IntegrationRegistry {
    private static List<AbstractIntegration> integrations = new ArrayList<>();
    private static ASMDataTable asmData;

    public static void registerIntegrations() {
        for(AbstractIntegration integration : AnnotatedInstanceUtil.getIntegrations(asmData)) {
            Logz.info("Registering integration class: %s", integration.getClass());
            integration.init();
            integration.reload();

            integrations.add(integration);
        }
    }

    public static void reloadIntegrations() {
        for(AbstractIntegration integration : integrations) {
            integration.reload();
        }
    }

    public static Map<String, Object> getDataForTileEntity(TileEntity entity, @Nullable EnumFacing side) {
        ScanDataList result = new ScanDataList();
        for(AbstractIntegration integration : integrations) {
            if(!integration.worksWith(entity, side)) {
                continue;
            }

            integration.addScanData(result, entity, side);
        }

        return result.getData();
    }

    public static AbstractIntegration getIntegrationByName(String name) {
        for(AbstractIntegration integration : integrations) {
            if(PrefixRegistry.supportsPrefix(integration.getClass(), name)) {
                return integration;
            }
        }

        return null;
    }

    public static void setAsmData(ASMDataTable asmData) {
        IntegrationRegistry.asmData = asmData;
    }
}
