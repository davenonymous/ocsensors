package org.dave.ocsensors.init;

import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dave.ocsensors.OCSensors;
import org.dave.ocsensors.sensor.BlockSensor;
import org.dave.ocsensors.sensor.ItemBlockSensor;
import org.dave.ocsensors.sensor.TileEntitySensor;

public class Blockss {
    public static BlockSensor sensor;

    public static void init() {
        sensor = (BlockSensor) new BlockSensor(Material.IRON).setUnlocalizedName("sensor").setRegistryName(OCSensors.MODID, "sensor");

        GameRegistry.register(sensor);
        GameRegistry.register(new ItemBlockSensor(sensor).setRegistryName(sensor.getRegistryName()));
        GameRegistry.registerTileEntity(TileEntitySensor.class, "TileEntitySensor");
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        sensor.initModel();
    }
}
