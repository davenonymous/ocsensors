package org.dave.ocsensors.integration;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public abstract class AbstractCapabilityIntegration extends AbstractIntegration {

    abstract protected Capability getCompatibleCapability();

    @Override
    public boolean worksWith(TileEntity entity, @Nullable EnumFacing side) {
        return entity instanceof ICapabilityProvider && entity.hasCapability(getCompatibleCapability(), side);
    }
}
