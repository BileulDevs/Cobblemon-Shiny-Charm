package dev.darcosse.shiny_charm.fabric.utils;

import dev.darcosse.shiny_charm.fabric.ShinyCharm;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class AdvancementUtils {
    public static void grantAdvancement(ServerPlayerEntity player, AdvancementEntry advancement) {
        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancementTracker().getProgress(advancement);

            if (!progress.isDone()) {
                for (String criterion : progress.getUnobtainedCriteria()) {
                    player.getAdvancementTracker().grantCriterion(advancement, criterion);
                }
            }
        }
    }
}
