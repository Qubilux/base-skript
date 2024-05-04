package io.github.ultreon.skript.event;

public abstract class Event {
    private final HandlerList handlerList = new HandlerList();

    public HandlerList getHandlerList() {
        return handlerList;
    }

	public HandlerList getHandlers() {
		return handlerList;
	}
}
