package org.dave.ocsensors.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.dave.ocsensors.init.Blockss;

public class ClientProxy extends CommonProxy {
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        registerItemBlockRenderer();

        Blockss.initModels();
    }

    private void registerItemBlockRenderer() {
        Item item = Item.getItemFromBlock(Blockss.sensor);
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation("ocsensors:sensordishcombined", "inventory"));
    }
}
