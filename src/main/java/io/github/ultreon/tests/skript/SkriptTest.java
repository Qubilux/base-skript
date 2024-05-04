package io.github.ultreon.tests.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.log.SkriptLogger;
import io.github.ultreon.skript.BaseSkript;
import io.github.ultreon.skript.plugins.PluginManager;
import org.apache.logging.log4j.Level;

public class SkriptTest {
	public static void main(String[] args) {
		BaseSkript.main(args);
		PluginManager pluginManager = BaseSkript.getPluginManager();

		Skript skript = new Skript();
		pluginManager.registerPlugin(skript);
		skript.onLoad();
		skript.onEnable();

		BaseSkript.init();

		SkriptLogger.LOGGER.log(Level.INFO, "Skript started");

		BaseSkript.getScheduler().runTaskTimerAsynchronously(skript, skript::onTick, 0, 1);

		while (BaseSkript.isRunning()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
