package org.dave.ocsensors.init;

import li.cil.oc.api.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Recipes {
    public static void init() {
        registerRecipes();
    }

    private static void registerRecipes() {
        ItemStack sensor = new ItemStack(Blockss.sensor, 1, 0);
        ItemStack adapter = Items.get("adapter").createItemStack(1);
        ItemStack cable = Items.get("cable").createItemStack(1);
        ItemStack pearl = new ItemStack(net.minecraft.init.Items.ENDER_PEARL, 1, 0);
        ItemStack iron = new ItemStack(net.minecraft.init.Items.IRON_INGOT, 1, 0);

        GameRegistry.addRecipe(new ShapedRecipes(3, 3, new ItemStack[] {null, pearl, null, null, cable, null, iron, adapter, iron}, sensor));
    }
}
