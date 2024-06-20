package dev.ultreon.baseskript;

public enum ServerPlatform {
    BUKKIT,
    BUKKIT_GLOWSTONE,
    BUKKIT_PAPER,
    BUKKIT_SPIGOT,
    BUKKIT_CRAFTBUKKIT,
    BUKKIT_SPONGE,
    BUKKIT_UNKNOWN,
    SPONGE;
    public boolean works = true;
    public boolean supported = true;
    public String name = "unknown";
}
