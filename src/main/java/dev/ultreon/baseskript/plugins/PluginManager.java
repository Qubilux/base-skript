package dev.ultreon.baseskript.plugins;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptEventHandler;
import com.google.common.collect.Lists;
import com.ultreon.libs.events.v0.EventPriority;
import dev.ultreon.baseskript.BaseSkript;
import dev.ultreon.baseskript.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class PluginManager {
    private final List<Plugin> plugins = Lists.newArrayList();
    private final Map<String, Plugin> pluginMap = new HashMap<>();

    public void registerPlugin(Plugin plugin) {
        plugins.add(plugin);
        pluginMap.put(plugin.getName(), plugin);
    }

    public Plugin[] getPlugins() {
        return plugins.toArray(Plugin[]::new);
    }

    public Plugin getPlugin(String name) {
        return pluginMap.get(name);
    }

    public void callEvent(Object event) {
        BaseSkript.getEventBus().publish(event);
    }

    public void registerEvents(Plugin plugin, Object... listeners) {
        for (Object listener : Lists.newArrayList(listeners)) {
            BaseSkript.getEventBus().subscribe(listener);
        }
    }

    public void registerEvent(Plugin plugin, Object listener) {
        BaseSkript.getEventBus().subscribe(listener);
    }

    public void unregisterEvents(Plugin plugin, Object... listeners) {
        for (Object listener : Lists.newArrayList(listeners)) {
            BaseSkript.getEventBus().unsubscribe(listener);
        }
    }

    public void registerEvent(Class<? extends Object> event, SkriptEventHandler.PriorityListener listener, EventPriority priority, Executor executor, Skript instance) {
        BaseSkript.getEventBus().subscribe(listener);
    }
}
