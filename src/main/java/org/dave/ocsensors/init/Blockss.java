package org.dave.ocsensors.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dave.ocsensors.sensor.BlockSensor;

public class Blockss {
    @GameRegistry.ObjectHolder("ocsensors:sensor")
    public static BlockSensor sensor;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        sensor.initModel();
    }
}
