package io.github.ultreon.skript;

import ch.njol.skript.lang.SyntaxElement;
import io.github.ultreon.skript.plugins.PluginDescriptionFile;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;

public interface Plugin {

	static <T> Plugin getProvidingPlugin(Class<? extends T> elementClass) {
		return BaseSkript.getProvidingPlugin(elementClass);
	}

    String getName();

    String getVersion0();

    InputStream getResource(String name);

    File getDataFolder();

    void onLoad();

    void onEnable();

    void onDisable();

    default boolean isEnabled() {
        return true;
    }

    default void registerEvents(Plugin plugin) {
        BaseSkript.getEventBus().register(plugin);
    }

    PluginDescriptionFile getDescription();

    default ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }

	default File getFile() {
		try {
			return new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
