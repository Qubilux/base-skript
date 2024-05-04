package ultreon.baseskript.plugins;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptEventHandler;
import com.google.common.collect.Lists;
import ultreon.baseskript.BaseSkript;
import ultreon.baseskript.Plugin;
import ultreon.baseskript.event.Event;
import ultreon.baseskript.event.EventPriority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class PluginManager {
    private final List<Plugin> plugins = Lists.newArrayList();
    private final Map<String, Plugin> pluginMap = new HashMap<String, Plugin>();

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

    public void callEvent(Event event) {
        BaseSkript.getEventBus().post(event);
    }

    public void registerEvents(Plugin plugin, Object... listeners) {
        for (Object listener : Lists.newArrayList(listeners)) {
            BaseSkript.getEventBus().register(listener);
        }
    }

    public void registerEvent(Plugin plugin, Object listener) {
        BaseSkript.getEventBus().register(listener);
    }

    public void unregisterEvents(Plugin plugin, Object... listeners) {
        for (Object listener : Lists.newArrayList(listeners)) {
            BaseSkript.getEventBus().unregister(listener);
        }
    }

    public void registerEvent(Class<? extends Event> event, SkriptEventHandler.PriorityListener listener, EventPriority priority, Executor executor, Skript instance) {
        BaseSkript.getEventBus().register(listener);
    }
}
