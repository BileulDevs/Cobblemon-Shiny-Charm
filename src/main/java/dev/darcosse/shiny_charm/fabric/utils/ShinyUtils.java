package dev.darcosse.shiny_charm.fabric.utils;

import dev.darcosse.shiny_charm.fabric.config.ConfigManager;
import java.util.concurrent.ThreadLocalRandom;

public class ShinyUtils {
    public static boolean getIsShiny() {
        int odds = ConfigManager.getShinyChance();
        int random = ThreadLocalRandom.current().nextInt(1, odds);
        return random == 1;
    }
}
