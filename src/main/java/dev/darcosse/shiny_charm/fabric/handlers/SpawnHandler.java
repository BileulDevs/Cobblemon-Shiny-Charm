package dev.darcosse.shiny_charm.fabric.handlers;

import dev.darcosse.shiny_charm.fabric.config.ConfigManager;
import dev.darcosse.shiny_charm.fabric.item.ShinyCharmItem;
import com.cobblemon.mod.common.api.events.entity.SpawnEvent;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.concurrent.ThreadLocalRandom;

public class SpawnHandler {

    public static boolean getIsShiny() {
        int odds = ConfigManager.getShinyChance();
        int random = ThreadLocalRandom.current().nextInt(1, odds);
        return random == 1;
    }

    public static Object handlePokemonSpawn(SpawnEvent event) {

        if (!(event.getEntity() instanceof PokemonEntity)) {
            return null;
        }

        PokemonEntity pokemonEntity = (PokemonEntity) event.getEntity();
        Pokemon pokemon = pokemonEntity.getPokemon();

        if (event.getCtx() != null) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getCtx().getCause().getEntity();
            if (player != null) {
                boolean isWearing = ShinyCharmItem.isWearingShinyCharm(player);
                if (isWearing && getIsShiny()) {
                    pokemon.setShiny(true);
                    player.sendMessage(Text.translatable("message.shiny_charm.success").styled(style -> style.withColor(Formatting.LIGHT_PURPLE)), false);
                }
            }
        }

        return null;
    }

}
