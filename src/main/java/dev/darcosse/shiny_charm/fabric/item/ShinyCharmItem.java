package dev.darcosse.shiny_charm.fabric.item;

import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;

public class ShinyCharmItem extends TrinketItem {

    public ShinyCharmItem(Item.Settings settings) {
        super(settings);
    }

    public static boolean isWearingShinyCharm(LivingEntity entity) {
        return TrinketsApi.getTrinketComponent(entity)
                .map(comp -> comp.isEquipped(stack -> stack.getItem() instanceof ShinyCharmItem))
                .orElse(false);
    }
}