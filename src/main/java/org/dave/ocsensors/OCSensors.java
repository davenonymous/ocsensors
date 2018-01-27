package org.dave.ocsensors;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.dave.ocsensors.command.CommandOCSensors;
import org.dave.ocsensors.converter.ConverterFluid;
import org.dave.ocsensors.integration.IntegrationRegistry;
import org.dave.ocsensors.misc.ConfigurationHandler;
import org.dave.ocsensors.proxy.CommonProxy;

@Mod(modid = OCSensors.MODID, version = OCSensors.VERSION, dependencies = "after:*;required-after:opencomputers", acceptedMinecraftVersions = "[1.12,1.13)")
public class OCSensors {
    public static final String MODID = "ocsensors";
    public static final String VERSION = "1.0.0";

    @SidedProxy(clientSide = "org.dave.ocsensors.proxy.ClientProxy", serverSide = "org.dave.ocsensors.proxy.ServerProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());

        IntegrationRegistry.setAsmData(event.getAsmData());

        MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());

        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        li.cil.oc.api.Driver.add(new ConverterFluid());

        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        IntegrationRegistry.registerIntegrations();

        proxy.postInit(event);
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandOCSensors());
    }
}
