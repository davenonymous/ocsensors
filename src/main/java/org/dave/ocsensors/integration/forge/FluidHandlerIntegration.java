package org.dave.ocsensors.integration.forge;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.dave.ocsensors.integration.AbstractCapabilityIntegration;
import org.dave.ocsensors.integration.Integrate;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Integrate
public class FluidHandlerIntegration extends AbstractCapabilityIntegration {
    @Override
    protected Capability getCompatibleCapability() {
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    public String getSectionName() {
        return "fluid";
    }

    @Override
    public Object getScanData(TileEntity entity, @Nullable EnumFacing side) {
        IFluidHandler fluidHandler = entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);

        ArrayList<Map> result = new ArrayList<>();
        for (IFluidTankProperties tank : fluidHandler.getTankProperties()) {
            HashMap<String, Object> tankMap = new HashMap<>();
            tankMap.put("capacity", tank.getCapacity());
            tankMap.put("contents", tank.getContents());

            result.add(tankMap);
        }

        return result;
    }
}
