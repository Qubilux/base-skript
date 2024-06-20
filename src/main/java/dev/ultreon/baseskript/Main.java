package dev.ultreon.baseskript;

import ch.njol.skript.log.SkriptLogger;
import org.apache.logging.log4j.Level;

public class Main {
	public static void main(String[] args) {
		BaseSkript.main(args);

		BaseSkript.load();
		BaseSkript.init();

		SkriptLogger.LOGGER.log(Level.INFO, "Skript started");

		while (BaseSkript.isRunning()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
