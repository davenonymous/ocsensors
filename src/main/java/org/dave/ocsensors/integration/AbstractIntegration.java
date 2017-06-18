package org.dave.ocsensors.integration;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public abstract class AbstractIntegration {
    public void init() {};

    public void reload() {};

    public abstract boolean worksWith(TileEntity entity, @Nullable EnumFacing side);

    public abstract void addScanData(ScanDataList data, TileEntity entity, @Nullable EnumFacing side);
}
