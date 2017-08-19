package org.dave.ocsensors.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.dave.ocsensors.OCSensors;
import org.dave.ocsensors.init.Blockss;
import org.dave.ocsensors.sensor.BlockSensor;
import org.dave.ocsensors.sensor.ItemBlockSensor;
import org.dave.ocsensors.sensor.TileEntitySensor;

@Mod.EventBusSubscriber
public class CommonProxy {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new BlockSensor(Material.IRON).setUnlocalizedName("sensor").setRegistryName(OCSensors.MODID, "sensor"));
        GameRegistry.registerTileEntity(TileEntitySensor.class, "TileEntitySensor");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlockSensor(Blockss.sensor).setRegistryName(Blockss.sensor.getRegistryName()));
    }

    public void preInit(FMLPreInitializationEvent event) {
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
    }
}