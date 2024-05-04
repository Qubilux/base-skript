package io.github.ultreon.skript.events;

import io.github.ultreon.skript.Plugin;

public class PluginDisableEvent {
    private final Plugin plugin;

    public PluginDisableEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
