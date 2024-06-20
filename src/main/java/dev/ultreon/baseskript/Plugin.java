package dev.ultreon.baseskript;

import dev.ultreon.baseskript.plugins.PluginDescriptionFile;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public interface Plugin {

	static <T> Plugin getProvidingPlugin(Class<? extends T> elementClass) {
		return BaseSkript.getProvidingPlugin(elementClass);
	}

    String getName();

    String getVersion0();

	default InputStream getResource(String name) {
		PluginInfoProvider pluginInfoProvider = PluginInfoProvider.get(this);
		if (pluginInfoProvider == null)
			return getClass().getResourceAsStream("/" + name);
		return pluginInfoProvider.getResource(this, name);
	}

	default File getDataFolder() {
		PluginInfoProvider pluginInfoProvider = PluginInfoProvider.get(this);
		if (pluginInfoProvider == null)
			return new File("plugins/" + getName());
		return pluginInfoProvider.getDataFolder(this);
	}

	void onLoad();

    void onEnable();

    void onDisable();

    default boolean isEnabled() {
        return true;
    }

    default void registerEvents(Plugin plugin) {
        BaseSkript.getEventBus().subscribe(plugin);
    }

    PluginDescriptionFile getDescription();

    default ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }

	@Deprecated
	default File getFile() {
		PluginInfoProvider pluginInfoProvider = PluginInfoProvider.get(this);
		if (pluginInfoProvider == null) {
			try {
				return new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
			} catch (URISyntaxException e) {
				return null;
			}
		}

		return pluginInfoProvider.getFile(this);
	}

	default URL getPluginLocation() {
		PluginInfoProvider pluginInfoProvider = PluginInfoProvider.get(this);
		if (pluginInfoProvider == null)
			return getClass().getProtectionDomain().getCodeSource().getLocation();
		return pluginInfoProvider.getPluginLocation(this);
	}
}
