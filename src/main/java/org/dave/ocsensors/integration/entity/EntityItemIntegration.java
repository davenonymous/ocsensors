package org.dave.ocsensors.integration.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import org.dave.ocsensors.integration.AbstractIntegration;
import org.dave.ocsensors.integration.Integrate;
import org.dave.ocsensors.integration.ScanDataList;

@Integrate(name = "entity_item")
public class EntityItemIntegration extends AbstractIntegration {
    @Override
    public boolean worksWith(Entity entity) {
        return entity instanceof EntityItem;
    }

    @Override
    public void addScanData(ScanDataList data, Entity entity) {
        super.addScanData(data, entity);

        EntityItem entityItem = (EntityItem)entity;
        ItemStack itemStack = entityItem.getItem();

        data.add("item", itemStack);
    }
}