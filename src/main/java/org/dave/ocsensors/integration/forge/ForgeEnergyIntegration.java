package org.dave.ocsensors.integration.forge;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.dave.ocsensors.integration.AbstractCapabilityIntegration;
import org.dave.ocsensors.integration.Integrate;

import javax.annotation.Nullable;
import java.util.HashMap;

@Integrate
public class ForgeEnergyIntegration extends AbstractCapabilityIntegration {
    @Override
    public String getSectionName() {
        return "energy";
    }

    @Override
    protected Capability getCompatibleCapability() {
        return CapabilityEnergy.ENERGY;
    }

    @Override
    public Object getScanData(TileEntity entity, @Nullable EnumFacing side) {
        IEnergyStorage storage = entity.getCapability(CapabilityEnergy.ENERGY, side);
        HashMap<String, Object> result = new HashMap<>();
        result.put("canReceive", storage.canReceive());
        result.put("canExtract", storage.canExtract());
        result.put("energyStored", storage.getEnergyStored());
        result.put("maxEnergyStored", storage.getMaxEnergyStored());
        return result;
    }
}

