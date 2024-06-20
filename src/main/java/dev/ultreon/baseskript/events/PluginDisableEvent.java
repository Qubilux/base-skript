package dev.ultreon.baseskript.events;

import dev.ultreon.baseskript.Plugin;

public class PluginDisableEvent {
    private final Plugin plugin;

    public PluginDisableEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
