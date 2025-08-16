package dev.darcosse.shiny_charm.fabric;

import dev.darcosse.shiny_charm.fabric.commands.ShinyCharmCommands;
import dev.darcosse.shiny_charm.fabric.config.ConfigManager;
import dev.darcosse.shiny_charm.fabric.handlers.SpawnHandler;
import dev.darcosse.shiny_charm.fabric.item.ShinyCharmItem;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import dev.darcosse.shiny_charm.fabric.item.ShinyCharmItemGroup;
import kotlin.Unit;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ShinyCharm implements ModInitializer {
    public static final String MOD_ID = "shiny_charm";
    public static final Item SHINY_CHARM = new ShinyCharmItem(new Item.Settings().fireproof().rarity(Rarity.EPIC).maxCount(1));

    @Override
    public void onInitialize() {
        ConfigManager.loadConfig();

        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "shiny_charm"), SHINY_CHARM);
        ShinyCharmItemGroup.registerItemGroup();
        ShinyCharmCommands.registerCommands();

        System.out.println("Shiny Charm : 1/" + ConfigManager.getCurrentShinyChance());

        CobblemonEvents.ENTITY_SPAWN.subscribe(Priority.HIGHEST, (event) -> {
            return (Unit) SpawnHandler.handlePokemonSpawn(event);
        });
    }
}
