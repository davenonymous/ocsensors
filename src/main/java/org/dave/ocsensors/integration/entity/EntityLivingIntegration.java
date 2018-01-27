package org.dave.ocsensors.integration.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import org.dave.ocsensors.integration.AbstractIntegration;
import org.dave.ocsensors.integration.Integrate;
import org.dave.ocsensors.integration.ScanDataList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Integrate(name = "entity_living")
public class EntityLivingIntegration extends AbstractIntegration {
    @Override
    public boolean worksWith(Entity entity) {
        return entity instanceof EntityLivingBase;
    }

    @Override
    public void addScanData(ScanDataList data, Entity entity) {
        super.addScanData(data, entity);

        EntityLivingBase living = (EntityLivingBase)entity;

        data.add("name", living.getName());
        data.add("health", living.getHealth());
        data.add("armor", living.getTotalArmorValue());
        if(!living.getActiveItemStack().isEmpty()) {
            data.add("item", living.getActiveItemStack());
        }

        if(living.isBurning()) {
            data.add("burning", true);
        }

        if(living.isAirBorne) {
            data.add("airborne", true);
        }

        List<Map<String, Object>> potionEffects = new ArrayList<>();
        for(PotionEffect potionEffect : living.getActivePotionEffects()) {
            Map<String, Object> potionData = new HashMap<>();
            potionData.put("potion", potionEffect.getPotion().getRegistryName());
            potionData.put("amplifier", potionEffect.getAmplifier());
            potionData.put("duration", potionEffect.getDuration());

            potionEffects.add(potionData);
        }

        if(potionEffects.size() > 0) {
            data.add("effect", potionEffects);
        }
    }
}
