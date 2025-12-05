package dev.darcosse.shiny_charm.fabric.tick;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class TickManager {
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            HandleAdvancement.checkAdvancements(server);
        });
    }
}