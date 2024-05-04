package io.github.ultreon.skript;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import io.github.ultreon.skript.plugins.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

public class BaseSkript {
    private static final EventBus EVENT_BUS = new EventBus("Skript");
    private static Thread primaryThread;
    private static BaseSkript instance;
    private static final SchedulerImpl scheduler = new SchedulerImpl();
    private static PluginManager pluginManager = new PluginManager();
    private static boolean running;

    public static EventBus getEventBus() {
        return EVENT_BUS;
    }

    public BaseSkript() {
        primaryThread = Thread.currentThread();
    }

    public static void main(String[] args) {
        instance = new BaseSkript();
    }

    public static void shutdown() {
        instance = null;
        scheduler.shutdown();
    }

    public static CommandSender getConsoleSender() {
        return new ConsoleCommandSender();
    }

    public static boolean isPrimaryThread() {
        return Thread.currentThread() == primaryThread;
    }

    public static BaseSkript getInstance() {
        return instance;
    }

    public static List<CommandSender> getOnlinePlayers() {
        return Lists.newArrayList();
    }

    public static Scheduler getScheduler() {
        return scheduler;
    }

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    public static String getBukkitVersion() {
        return "unknown";
    }

    public static Void getServer() {
        return null;
    }

    public static boolean isRunning() {
        return running;
    }

    public static String getVersion() {
        return "0.0.0-unknown";
    }

	public static void init() {
		running = true;
	}

	public static <T> Plugin getProvidingPlugin(Class<? extends T> elementClass) {
		for (Plugin plugin : pluginManager.getPlugins()) {
			File file = plugin.getFile();
			try {
				JarFile jarFile = new JarFile(file);
				if (jarFile.getEntry(elementClass.getName().replace('.', '/') + ".class") != null) {
					return plugin;
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public static void dispatchCommand(CommandSender consoleSender, String s) {

	}
}
