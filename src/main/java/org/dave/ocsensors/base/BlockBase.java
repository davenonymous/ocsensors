package org.dave.ocsensors.base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import org.dave.ocsensors.OCSensors;

public class BlockBase extends Block {
    public BlockBase(Material material) {
        super(material);
    }

    public BlockBase() {
        this(Material.ROCK);
    }

    @Override
    public Block setUnlocalizedName(String name) {
        if(!name.startsWith(OCSensors.MODID + ".")) {
            name = OCSensors.MODID + "." + name;
        }
        return super.setUnlocalizedName(name);
    }
}
