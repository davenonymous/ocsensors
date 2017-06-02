package org.dave.ocsensors.integration;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public abstract class AbstractIntegration {
    public abstract String getSectionName();

    public abstract boolean worksWith(TileEntity entity, @Nullable EnumFacing side);

    public abstract Object getScanData(TileEntity entity, @Nullable EnumFacing side);
}
