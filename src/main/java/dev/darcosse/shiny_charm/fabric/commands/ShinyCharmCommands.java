package dev.darcosse.shiny_charm.fabric.commands;

import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.darcosse.shiny_charm.fabric.config.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.darcosse.shiny_charm.fabric.utils.PokedexRegionUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

public class ShinyCharmCommands {

    private static final List<String> AVAILABLE_REGIONS = Arrays.asList(
            "kanto", "johto", "hoenn", "sinnoh", "unova", "kalos", "alola", "galar", "paldea", "national"
    );

    private static final int POKEMON_PER_PAGE = 30;

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerShinyCommands(dispatcher);
        });
    }

    private static void registerShinyCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("shinydex")
                .then(CommandManager.literal("reload")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(ShinyCharmCommands::reloadConfig))
                .then(CommandManager.literal("info")
                        .executes(ShinyCharmCommands::showInfo))
                .then(CommandManager.literal("check")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            PokedexRegionUtils.checkAllRegionsProgress(player);
                            return 1;
                        })
                        .then(CommandManager.argument("region", StringArgumentType.string())
                                .suggests((context, builder) -> CommandSource.suggestMatching(AVAILABLE_REGIONS, builder))
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                    String region = StringArgumentType.getString(context, "region");

                                    if (!isValidRegion(region)) {
                                        player.sendMessage(Text.translatable("command.shinycharm.error.invalid_region", region));
                                        return 0;
                                    }

                                    PokedexRegionUtils.checkPlayerRegionProgress(player, region.toUpperCase());
                                    return 1;
                                })
                        )
                )
                .then(CommandManager.literal("missing")
                        .then(CommandManager.argument("region", StringArgumentType.string())
                                .suggests((context, builder) -> CommandSource.suggestMatching(AVAILABLE_REGIONS, builder))
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                    String region = StringArgumentType.getString(context, "region");

                                    if (!isValidRegion(region)) {
                                        player.sendMessage(Text.translatable("command.shinycharm.error.invalid_region", region));
                                        return 0;
                                    }

                                    List<Species> missing = PokedexRegionUtils.getMissingPokemon(player, region);
                                    if (missing.isEmpty()) {
                                        player.sendMessage(Text.translatable("command.shinycharm.missing.complete", region));
                                    } else {
                                        showPaginatedMissingPokemon(player, missing, region, 1);
                                    }
                                    return 1;
                                })
                                .then(CommandManager.argument("page", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                            String region = StringArgumentType.getString(context, "region");
                                            int page = IntegerArgumentType.getInteger(context, "page");

                                            if (!isValidRegion(region)) {
                                                player.sendMessage(Text.translatable("command.shinycharm.error.invalid_region", region));
                                                return 0;
                                            }

                                            List<Species> missing = PokedexRegionUtils.getMissingPokemon(player, region);
                                            if (missing.isEmpty()) {
                                                player.sendMessage(Text.translatable("command.shinycharm.missing.complete", region));
                                            } else {
                                                showPaginatedMissingPokemon(player, missing, region, page);
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .then(CommandManager.literal("completion")
                        .then(CommandManager.argument("region", StringArgumentType.string())
                                .suggests((context, builder) -> CommandSource.suggestMatching(AVAILABLE_REGIONS, builder))
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                    String region = StringArgumentType.getString(context, "region");

                                    if (!isValidRegion(region)) {
                                        player.sendMessage(Text.translatable("command.shinycharm.error.invalid_region", region));
                                        return 0;
                                    }

                                    PokedexRegionUtils.RegionProgress progress = PokedexRegionUtils.getRegionProgress(player, region);
                                    if (progress != null) {
                                        player.sendMessage(Text.translatable("command.shinycharm.completion.header", region.toUpperCase()));
                                        player.sendMessage(Text.translatable("command.shinycharm.completion.implemented", progress.totalImplemented()));
                                        player.sendMessage(Text.translatable("command.shinycharm.completion.seen", progress.totalSeen()));
                                        player.sendMessage(Text.translatable("command.shinycharm.completion.caught", progress.totalCaught()));
                                        player.sendMessage(Text.translatable("command.shinycharm.completion.percentage",
                                                String.format("%.1f%%", progress.completionPercentage())));
                                        player.sendMessage(Text.translatable("command.shinycharm.completion.status." +
                                                (progress.isCompleted() ? "complete" : "incomplete")));
                                    } else {
                                        player.sendMessage(Text.translatable("command.shinycharm.error.invalid_region", region));
                                    }
                                    return 1;
                                })
                        )
                )
        );
    }

    private static boolean isValidRegion(String region) {
        return AVAILABLE_REGIONS.contains(region.toLowerCase());
    }

    private static void showPaginatedMissingPokemon(ServerPlayerEntity player, List<Species> missing, String region, int page) {
        int totalPages = (int) Math.ceil((double) missing.size() / POKEMON_PER_PAGE);

        if (page < 1 || page > totalPages) {
            player.sendMessage(Text.translatable("command.shinycharm.missing.invalid_page", totalPages));
            return;
        }

        int startIndex = (page - 1) * POKEMON_PER_PAGE;
        int endIndex = Math.min(startIndex + POKEMON_PER_PAGE, missing.size());

        player.sendMessage(Text.translatable("command.shinycharm.missing.header", region.toUpperCase(), missing.size()));

        for (int i = startIndex; i < endIndex; i++) {
            Species species = missing.get(i);
            String speciesTranslationKey = "pokemon.species." + species.showdownId().toLowerCase();
            player.sendMessage(Text.translatable(
                    "command.shinycharm.missing.entry",
                    species.getNationalPokedexNumber(),
                    Text.translatable(speciesTranslationKey)
            ));
        }

        if (totalPages > 1) {
            player.sendMessage(Text.translatable("command.shinycharm.missing.pagination", page, totalPages));
        }
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