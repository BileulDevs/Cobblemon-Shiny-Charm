package dev.darcosse.shiny_charm.fabric.commands;

import dev.darcosse.shiny_charm.fabric.config.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ShinyCharmCommands {

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerShinyCommands(dispatcher);
        });
    }

    private static void registerShinyCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("shinycharm")
                .then(CommandManager.literal("reload")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(ShinyCharmCommands::reloadConfig))
                .then(CommandManager.literal("info")
                        .executes(ShinyCharmCommands::showInfo))
        );
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        try {
            ConfigManager.reloadConfig();

            context.getSource().sendFeedback(
                    () -> Text.translatable("command.shiny.reload.success"),
                    true
            );

            context.getSource().sendFeedback(
                    () -> Text.translatable("command.shiny.reload.rate", ConfigManager.getCurrentShinyChance()),
                    false
            );

            return 1;
        } catch (Exception e) {
            context.getSource().sendError(
                    Text.translatable("command.shiny.reload.error", e.getMessage())
            );
            return 0;
        }
    }

    private static int showInfo(CommandContext<ServerCommandSource> context) {
        int chance = ConfigManager.getCurrentShinyChance();
        double percentage = (1.0 / chance) * 100.0;

        context.getSource().sendFeedback(
                () -> Text.translatable("command.shiny.info.header"),
                false
        );
        context.getSource().sendFeedback(
                () -> Text.translatable("command.shiny.info.rate", chance,
                        String.format("%.3f", percentage)),
                false
        );

        return 1;
    }
}