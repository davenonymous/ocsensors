package org.dave.ocsensors.integration.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.dave.ocsensors.integration.AbstractIntegration;
import org.dave.ocsensors.integration.Integrate;
import org.dave.ocsensors.integration.ScanDataList;
import org.dave.ocsensors.utility.ObfuscationReflectionHelperEx;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

@Integrate(name = "entity_villager")
public class EntityVillagerIntegration extends AbstractIntegration {
    private static Field villagerCareerId = ObfuscationReflectionHelperEx.findField(EntityVillager.class, "careerId");
    private static Field villagerBuyingList = ObfuscationReflectionHelperEx.findField(EntityVillager.class, "buyingList");

    @Override
    public boolean worksWith(Entity entity) {
        return villagerCareerId != null && villagerBuyingList != null && entity instanceof EntityVillager;
    }

    @Override
    public void addScanData(ScanDataList data, Entity entity) {
        super.addScanData(data, entity);

        // Overwrite type to "villager"
        data.add("type", "villager");

        EntityVillager villager = (EntityVillager) entity;

        HashMap<String, Object> villagerData = new HashMap<>();

        VillagerRegistry.VillagerProfession profession = villager.getProfessionForge();
        villagerData.put("profession", profession.getRegistryName().toString());

        VillagerRegistry.VillagerCareer career = getCareer(villager);
        if(career != null) {
            villagerData.put("career", career.getName());
        }

        List<MerchantRecipe> merchantRecipes = getMerchantRecipes(villager);
        if(merchantRecipes != null) {
            villagerData.put("offers", merchantRecipes);
        }

        data.add("villager", villagerData);
    }

    private List<MerchantRecipe> getMerchantRecipes(EntityVillager villager) {
        try {
            return (List<MerchantRecipe>) villagerBuyingList.get(villager);
        } catch (IllegalAccessException e) {
        }

        return null;
    }

    private VillagerRegistry.VillagerCareer getCareer(EntityVillager villager) {
        try {
            return villager.getProfessionForge().getCareer((int)villagerCareerId.get(villager));
        } catch (IllegalAccessException e) {
        }

        return null;
    }
}
