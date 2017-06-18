package org.dave.ocsensors.integration.forge;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.dave.ocsensors.integration.*;

import javax.annotation.Nullable;
import java.util.HashMap;

@Integrate
public class ForgeEnergyIntegration extends AbstractCapabilityIntegration {
    @Override
    protected Capability getCompatibleCapability() {
        return CapabilityEnergy.ENERGY;
    }

    @Override
    public void init() {
        PrefixRegistry.addSupportedPrefix(ForgeEnergyIntegration.class, "energy");
    }

    @Override
    public void addScanData(ScanDataList data, TileEntity entity, @Nullable EnumFacing side) {
        IEnergyStorage storage = entity.getCapability(CapabilityEnergy.ENERGY, side);
        HashMap<String, Object> result = new HashMap<>();
        result.put("canReceive", storage.canReceive());
        result.put("canExtract", storage.canExtract());
        result.put("energyStored", storage.getEnergyStored());
        result.put("maxEnergyStored", storage.getMaxEnergyStored());

        data.add("energy", result);
    }
}

