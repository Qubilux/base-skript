package ultreon.baseskript;

import ch.njol.skript.Skript;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ultreon.baseskript.event.EventBus;
import ultreon.baseskript.plugins.PluginManager;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.jar.JarFile;

public class BaseSkript {
    private static final EventBus EVENT_BUS = new EventBus();
	public static final Logger LOGGER = LogManager.getLogger("BaseSkript");
	private static Thread primaryThread;
    private static BaseSkript instance;
    private static final SchedulerImpl scheduler = new SchedulerImpl();
    private static final PluginManager pluginManager = new PluginManager();
    private static boolean running = true;
    private static boolean starting = true;
	private static Skript skript;
	private static ExtLoader extLoader;

	public static EventBus getEventBus() {
        return EVENT_BUS;
    }

    public BaseSkript() {
        primaryThread = Thread.currentThread();
    }

    public static void main(String[] args) {
        instance = new BaseSkript();
    }

	public static void load() {
		skript = new Skript();
		extLoader = new ExtLoader();
		pluginManager.registerPlugin(skript);
		pluginManager.registerPlugin(extLoader);
		skript.onLoad();
		skript.onEnable();
		extLoader.onLoad();
		extLoader.onEnable();

		Runtime.getRuntime().addShutdownHook(new Thread(BaseSkript::cleanUp));

		BaseSkript.getScheduler().runTaskTimerAsynchronously(skript, skript::onTick, 0, 1);
	}

	private static void cleanUp() {
		for (Plugin plugin : pluginManager.getPlugins()) {
			try {
				plugin.onDisable();
			} catch (Exception e) {
				LOGGER.fatal("Failed to disable plugin " + plugin.getName(), e);
			}
		}
	}

	public static void shutdown() {
        instance = null;
		try {
			scheduler.shutdown();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
		running = false;

		if (skript != null) {
			skript.onDisable();
			skript = null;
		}
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

	public static boolean isRunning() {
        return running;
    }

    public static String getVersion() {
        return "0.0.0-unknown";
    }

	public static void init() {
		starting = false;
	}

	public static boolean isStarting() {
		return starting;
	}

	public static <T> Plugin getProvidingPlugin(Class<? extends T> elementClass) {
		LOGGER.warn("Deprecated usage of BaseSkript.getProvidingPlugin");

		for (Plugin plugin : pluginManager.getPlugins()) {
			URL pluginLocation = plugin.getPluginLocation();
			if (pluginLocation.getProtocol().equals("file")) {
				File file = null;
				try {
					file = new File(pluginLocation.toURI());
				} catch (URISyntaxException ignored) {

				}
				if (!file.exists() || !file.isFile() || !file.getName().endsWith(".jar"))
					continue;
				try {
					JarFile jarFile = new JarFile(file);
					if (jarFile.getEntry(elementClass.getName().replace('.', '/') + ".class") != null) {
						return plugin;
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

		throw new IllegalStateException("No plugin providing " + elementClass.getName());
	}

	public static void dispatchCommand(CommandSender consoleSender, String s) {

	}

	public static void exit(int code) {
		shutdown();
		System.exit(code);
	}
}
