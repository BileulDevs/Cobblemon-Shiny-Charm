package dev.darcosse.shiny_charm.fabric.item;

import dev.darcosse.shiny_charm.fabric.ShinyCharm; // Add this import
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ShinyCharmItemGroup {

    public static ItemGroup SHINY_CHARM_GROUP;

    public static void registerItemGroup() {
        SHINY_CHARM_GROUP = Registry.register(
                Registries.ITEM_GROUP,
                Identifier.of("shiny_charm", "main"),
                ItemGroup.create(ItemGroup.Row.TOP, -1)
                        .icon(() -> new ItemStack(ShinyCharm.SHINY_CHARM))
                        .displayName(Text.translatable("itemgroup.shiny_charm"))
                        .entries((displayContext, entries) -> {
                            entries.add(ShinyCharm.SHINY_CHARM);
                        })
                        .build()
        );
    }
}