package io.github.ultreon.skript;

import ch.njol.skript.Skript;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

@SuppressWarnings("UnusedReturnValue")
public interface Scheduler extends Executor {
    ScheduledFuture<?> schedule(long delay, Runnable task);

    ScheduledFuture<?> scheduleAtFixedRate(long initialDelay, long period, Runnable task);

    ScheduledFuture<?> scheduleWithFixedDelay(long initialDelay, long delay, Runnable task);

    void execute(@NotNull Runnable task);

    /**
     * @return The {@link Bukkit} instance.
     */
    BaseSkript getSkript();

    int runTaskLaterAsynchronously(Plugin plugin, Runnable task, long delay);

    default int runTaskAsynchronously(Plugin plugin, Runnable task) {
        return runTaskLaterAsynchronously(plugin, task, 0);
    }

    int scheduleSyncDelayedTask(Plugin plugin, Runnable task, long delay);

    default int scheduleSyncDelayedTask(Plugin plugin, Runnable task) {
        return scheduleSyncDelayedTask(plugin, task, 0);
    }

    int runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay, long period);

    default int runTaskTimerAsynchronously(Plugin plugin, Runnable task) {
        return runTaskTimerAsynchronously(plugin, task, 0, 1);
    }

    int scheduleSyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period);

    default int scheduleSyncRepeatingTask(Plugin plugin, Runnable task) {
        return scheduleSyncRepeatingTask(plugin, task, 0, 1);
    }

    boolean isQueued(int taskID);

    void cancelTask(int taskID);

    boolean isCurrentlyRunning(int taskID);

    <T> Future<T> callSyncMethod(Plugin p, Callable<T> c);

    void cancelTasks(Plugin skript);

	default void runTaskLater(Skript skript, Runnable task, long delay) {
		runTaskLaterAsynchronously(skript, task, delay);
	}
}
