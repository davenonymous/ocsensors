package org.dave.ocsensors.integration;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public abstract class AbstractIntegration {
    public void init() {}

    public void reload() {}

    public boolean worksWith(TileEntity entity, @Nullable EnumFacing side) {
        return false;
    }

    public void addScanData(ScanDataList data, TileEntity entity, @Nullable EnumFacing side) {}

    public boolean worksWith(Entity entity) {
        return false;
    }

    public void addScanData(ScanDataList data, Entity entity) {}
}
