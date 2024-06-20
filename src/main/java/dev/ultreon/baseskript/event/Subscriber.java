package dev.ultreon.baseskript.event;

import com.ultreon.libs.events.v0.EventPriority;
import com.ultreon.libs.events.v0.ICancellable;

public abstract class Subscriber<T extends Object> {
    public abstract void handle(T e);

    public abstract EventPriority getPriority();

	public abstract Class<? extends T> getType();

	@SuppressWarnings("unchecked")
	void handle0(Object event) {
		if (event instanceof ICancellable && ((ICancellable) event).isCancelled()) return;

		if (!this.getType().isAssignableFrom(event.getClass())) {
			return;
		}

		this.handle((T) event);
	}
}
