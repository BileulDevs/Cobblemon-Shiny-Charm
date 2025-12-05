package dev.darcosse.shiny_charm.fabric.utils;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokedex.PokedexManager;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PokedexRegionUtils {
    public static boolean isRegionCompleted(ServerPlayerEntity player, String region) {
        PokedexManager pokedexData = Cobblemon.INSTANCE.getPlayerDataManager()
                .getPokedexData(player.getUuid());

        int[] range = getRegionRange(region);
        if (range == null) return false;

        int startNumber = range[0];
        int endNumber = range[1];

        int implementedCount = 0;
        int caughtCount = 0;

        for (int i = startNumber; i <= endNumber; i++) {
            Species species = PokemonSpecies.INSTANCE.getByPokedexNumber(i, "cobblemon");;

            if (species != null && species.getImplemented()) {
                implementedCount++;

                if (pokedexData.getSpeciesRecords().containsKey(species)) {
                    caughtCount++;
                }
            }
        }

        return implementedCount > 0 && caughtCount == implementedCount;
    }

    // Fonction pour obtenir les Pokémon implémentés d'une région
    public static String getImplementedRegion(String region) {
        int[] range = getRegionRange(region);
        if (range == null) return "Région non reconnue";

        int startNumber = range[0];
        int endNumber = range[1];

        StringBuilder result = new StringBuilder();
        result.append("Pokémon implémentés pour ").append(region).append(":\n");

        int implementedCount = 0;
        for (int i = startNumber; i <= endNumber; i++) {
            Species species = PokemonSpecies.INSTANCE.getByPokedexNumber(i, "cobblemon");

            if (species != null && species.getImplemented()) {
                result.append("- #").append(i).append(" ").append(species.getName()).append("\n");
                implementedCount++;
            }
        }

        result.append("\nTotal implémentés: ").append(implementedCount)
                .append("/").append(endNumber - startNumber + 1);

        return result.toString();
    }

    private static int[] getRegionRange(String region) {
        return switch (region.toLowerCase()) {
            case "kanto" -> new int[]{1, 151};
            case "johto" -> new int[]{152, 251};
            case "hoenn" -> new int[]{252, 386};
            case "sinnoh" -> new int[]{387, 493};
            case "unova", "unys" -> new int[]{494, 649};
            case "kalos" -> new int[]{650, 721};
            case "alola" -> new int[]{722, 809};
            case "galar" -> new int[]{810, 898};
            case "paldea" -> new int[]{899, 1025};
            case "national" -> new int[]{1, 1025};
            default -> null;
        };
    }

    public static RegionProgress getRegionProgress(ServerPlayerEntity player, String region) {
        PokedexManager pokedexData = Cobblemon.INSTANCE.getPlayerDataManager()
                .getPokedexData(player.getUuid());

        int[] range = getRegionRange(region);
        if (range == null) return null;

        int startNumber = range[0];
        int endNumber = range[1];

        int implementedCount = 0;
        int caughtCount = 0;
        int seenCount = 0;

        for (int i = startNumber; i <= endNumber; i++) {
            Species species = PokemonSpecies.INSTANCE.getByPokedexNumber(i, "cobblemon");;

            if (species != null && species.getImplemented()) {
                implementedCount++;

                if (hasSeenSpecies(pokedexData, species)) {
                    seenCount++;
                }

                if (hasCaughtSpecies(pokedexData, species)) {
                    caughtCount++;
                }
            }
        }

        return new RegionProgress(region, implementedCount, seenCount, caughtCount);
    }

    public record RegionProgress(
            String region,
            int totalImplemented,
            int totalSeen,
            int totalCaught,
            double completionPercentage,
            boolean isCompleted
    ) {
        public RegionProgress(String region, int totalImplemented, int totalSeen, int totalCaught) {
            this(
                    region,
                    totalImplemented,
                    totalSeen,
                    totalCaught,
                    totalImplemented > 0 ? (double) totalCaught / totalImplemented * 100.0 : 0.0,
                    totalImplemented > 0 && totalCaught == totalImplemented
            );
        }

        @Override
        public String toString() {
            return String.format("%s: %d/%d capturés (%.1f%%) - %s",
                    region, totalCaught, totalImplemented, completionPercentage,
                    isCompleted ? "COMPLÉTÉ" : "EN COURS");
        }
    }

    public static void checkPlayerRegionProgress(ServerPlayerEntity player, String region) {
        RegionProgress progress = getRegionProgress(player, region);

        if (progress != null) {
            if (progress.isCompleted()) {
                player.sendMessage(Text.translatable(
                        "command.shinycharm.check.completed", region
                ));
            } else {
                player.sendMessage(Text.translatable(
                        "command.shinycharm.check.progress",
                        region,
                        progress.totalCaught(),
                        progress.totalImplemented(),
                        String.format("%.1f%%", progress.completionPercentage())
                ));
            }
        } else {
            player.sendMessage(Text.translatable("command.shinycharm.error.invalid_region", region));
        }
    }

    public static void checkAllRegionsProgress(ServerPlayerEntity player) {
        String[] regions = {"kanto", "johto", "hoenn", "sinnoh", "unova", "kalos", "alola", "galar", "paldea", "national"};

        player.sendMessage(Text.literal("§6=== Progression Pokédex ==="));

        for (String region : regions) {
            RegionProgress progress = getRegionProgress(player, region);
            if (progress != null && progress.totalImplemented() > 0) {
                String status = progress.isCompleted() ? "§a✓" : "§e⚬";
                player.sendMessage(Text.literal(
                        String.format("%s §f%s: §7%d/%d (%.1f%%)",
                                status,
                                region.substring(0, 1).toUpperCase() + region.substring(1),
                                progress.totalCaught(),
                                progress.totalImplemented(),
                                progress.completionPercentage())
                ));
            }
        }
    }

    public static java.util.List<Species> getMissingPokemon(ServerPlayerEntity player, String region) {
        PokedexManager pokedexData = Cobblemon.INSTANCE.getPlayerDataManager()
                .getPokedexData(player.getUuid());

        int[] range = getRegionRange(region);
        if (range == null) return java.util.Collections.emptyList();

        java.util.List<Species> missing = new java.util.ArrayList<>();

        for (int i = range[0]; i <= range[1]; i++) {
            Species species = PokemonSpecies.INSTANCE.getByPokedexNumber(i, "cobblemon");;

            if (species != null && species.getImplemented()) {
                if (!hasCaughtSpecies(pokedexData, species)) {
                    missing.add(species);
                }
            }
        }

        return missing;
    }

    private static boolean hasCaughtSpecies(PokedexManager pokedexData, Species species) {
        return pokedexData.getKnowledgeForSpecies(species.resourceIdentifier) == PokedexEntryProgress.CAUGHT;
    }

    private static boolean hasSeenSpecies(PokedexManager pokedexData, Species species) {
        return pokedexData.getKnowledgeForSpecies(species.resourceIdentifier) == PokedexEntryProgress.ENCOUNTERED;
    }
}