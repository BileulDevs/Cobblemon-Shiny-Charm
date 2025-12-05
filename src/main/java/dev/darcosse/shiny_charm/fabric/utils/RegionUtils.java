package dev.darcosse.shiny_charm.fabric.utils;

public enum RegionUtils {
    JOHTO,
    KANTO,
    HOENN,
    SINNOH,
    UNOVA,
    KALOS,
    ALOLA,
    GALAR,
    PALDEA,
    NATIONAL;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}