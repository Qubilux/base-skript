package dev.ultreon.baseskript.event;

import dev.ultreon.baseskript.Plugin;

public class RegisteredListener extends Listener {
    private final Plugin plugin;
    private final Listener listener;

    public RegisteredListener(Plugin plugin, Listener listener) {
        super();
        this.plugin = plugin;
        this.listener = listener;
    }

    public Listener getListener() {
        return listener;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
