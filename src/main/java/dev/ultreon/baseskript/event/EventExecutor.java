package dev.ultreon.baseskript.event;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public interface EventExecutor extends Executor {
	void execute(Listener listener, Object event);

	@Override
	default void execute(@NotNull Runnable command) {

	}
}
