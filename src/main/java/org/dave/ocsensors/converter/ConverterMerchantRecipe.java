package org.dave.ocsensors.converter;

import li.cil.oc.api.driver.Converter;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConverterMerchantRecipe implements Converter {
    @Override
    public void convert(Object value, Map<Object, Object> output) {
        if(value instanceof MerchantRecipe) {
            MerchantRecipe recipe = (MerchantRecipe) value;
            List<ItemStack> priceList = new ArrayList<>();
            if(!recipe.getItemToBuy().isEmpty()) {
                priceList.add(recipe.getItemToBuy());
            }
            if(recipe.hasSecondItemToBuy() && !recipe.getSecondItemToBuy().isEmpty()) {
                priceList.add(recipe.getSecondItemToBuy());
            }

            output.put("price", priceList);
            output.put("item", recipe.getItemToSell());
            output.put("disabled", recipe.isRecipeDisabled());
        }
    }
}
