package dev.darcosse.shiny_charm.fabric.tick;

import dev.darcosse.shiny_charm.fabric.advancement.ModAdvancement;
import dev.darcosse.shiny_charm.fabric.utils.AdvancementUtils;
import dev.darcosse.shiny_charm.fabric.utils.PokedexRegionUtils;
import dev.darcosse.shiny_charm.fabric.utils.RegionUtils;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Handler générique pour les advancements
 */
public class HandleAdvancement {

    public static void checkAdvancements(MinecraftServer server) {
        server.getPlayerManager().getPlayerList().forEach(player -> {
            grantRootAdvancement(player);
            for (RegionUtils region : RegionUtils.values()) {
                checkDex(player, region.toString());
            }
        });
    }

    private static void grantRootAdvancement(ServerPlayerEntity player) {
        AdvancementUtils.grantAdvancement(player, ModAdvancement.ROOT.getAdvancement(player.server));
    }

    private static void checkDex(ServerPlayerEntity player, String dex) {
        if (PokedexRegionUtils.getRegionProgress(player, dex.toLowerCase()).isCompleted()) {
            AdvancementEntry advancement = ModAdvancement.getAdvancement(player.server, dex);
            if (advancement != null) {
                AdvancementUtils.grantAdvancement(player, advancement);
            }
        }
    }
}