package ultreon.baseskript.event;

import com.google.common.collect.Lists;
import ultreon.baseskript.Plugin;

import java.util.List;

public class HandlerList {
    private final List<RegisteredListener> handlers = Lists.newCopyOnWriteArrayList();

    public void register(Plugin plugin, Listener listener) {
        this.handlers.add(new RegisteredListener(plugin, listener));
    }

    public Iterable<RegisteredListener> getRegisteredListeners() {
        return this.handlers;
    }

    public void unregister(Listener listener) {
        this.handlers.removeIf(registeredListener -> registeredListener.getListener() == listener);
    }
}
