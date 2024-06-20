package dev.ultreon.baseskript;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class SchedulerImpl implements Scheduler {
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private int id;
    private final Map<Integer, Runnable> tasks = new ConcurrentHashMap<Integer, Runnable>();
    private final Map<Integer, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<Integer, ScheduledFuture<?>>();
    private final Map<Runnable, Integer> taskIds = new ConcurrentHashMap<Runnable, Integer>(); // <task, id>
    private final Map<Plugin, List<Runnable>> pluginTasks = new ConcurrentHashMap<Plugin, List<Runnable>>(); // <plugin, tasks>
    private final List<Runnable> runningTasks = new CopyOnWriteArrayList<Runnable>();

    @Override
    public ScheduledFuture<?> schedule(long delay, Runnable task) {
        return executorService.schedule(() -> {
			while (BaseSkript.isStarting()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
            runningTasks.add(task);
            task.run();
            runningTasks.remove(task);
        }, delay * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(long initialDelay, long period, Runnable task) {
        return executorService.scheduleAtFixedRate(() -> {
			while (BaseSkript.isStarting()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
            runningTasks.add(task);
            task.run();
            runningTasks.remove(task);
        }, initialDelay * 50, period * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(long initialDelay, long delay, Runnable task) {
        return executorService.scheduleWithFixedDelay(() -> {
            runningTasks.add(task);
            task.run();
            runningTasks.remove(task);
        }, initialDelay * 50, delay * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void execute(@NotNull Runnable task) {
        executorService.execute(task);
    }

    public void shutdown() throws InterruptedException {
        executorService.shutdown();
    }

    public void shutdownNow() {
        executorService.shutdownNow();
    }

    @Override
    public BaseSkript getSkript() {
        return BaseSkript.getInstance();
    }

    @Override
    public int runTaskLaterAsynchronously(Plugin plugin, Runnable task, long delay) {
        this.schedule(delay, task);
        int i = id++;
        this.tasks.put(i, task);
        this.pluginTasks.computeIfAbsent(plugin, k -> new CopyOnWriteArrayList<Runnable>()).add(task);
        this.taskIds.put(task, i);
        return i;
    }

    @Override
    public int scheduleSyncDelayedTask(Plugin plugin, Runnable task, long delay) {
        this.schedule(delay, task);
        int i = id++;
        this.tasks.put(i, task);
        this.pluginTasks.computeIfAbsent(plugin, k -> new CopyOnWriteArrayList<Runnable>()).add(task);
        this.taskIds.put(task, i);
        return i;
    }

    @Override
    public int runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay, long period) {
        this.scheduleAtFixedRate(delay, period, task);
        int i = id++;
        this.tasks.put(i, task);
        this.pluginTasks.computeIfAbsent(plugin, k -> new CopyOnWriteArrayList<Runnable>()).add(task);
        this.taskIds.put(task, i);
        return i;
    }

    @Override
    public int scheduleSyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        this.scheduleAtFixedRate(delay, period, task);
        int i = id++;
        this.tasks.put(i, task);
        this.pluginTasks.computeIfAbsent(plugin, k -> new CopyOnWriteArrayList<Runnable>()).add(task);
        this.taskIds.put(task, i);
        return i;
    }

    @Override
    public boolean isQueued(int taskID) {
        return scheduledTasks.containsKey(taskID);
    }

    @Override
    public void cancelTask(int taskID) {
        if (scheduledTasks.containsKey(taskID)) {
            scheduledTasks.get(taskID).cancel(false);
        }
    }

    @Override
    public boolean isCurrentlyRunning(int taskID) {
        return runningTasks.contains(tasks.get(taskID));
    }

    @Override
    public <T> Future<T> callSyncMethod(Plugin p, Callable<T> c) {
        return executorService.submit(c);
    }

    @Override
    public void cancelTasks(Plugin skript) {
        List<Runnable> tasks = this.pluginTasks.get(skript);
        if (tasks != null) {
            for (Runnable task : tasks) {
                this.cancelTask(taskIds.get(task));
            }
        }
    }
}
