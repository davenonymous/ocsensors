package org.dave.ocsensors.sensor;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockSensor extends ItemBlock {
    public ItemBlockSensor(Block block) {
        super(block);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);

        if(GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("tile.ocsensors.sensor.tooltip"));
        }
    }
}
