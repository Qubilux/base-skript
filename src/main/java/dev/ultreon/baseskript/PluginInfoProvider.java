package dev.ultreon.baseskript;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ServiceLoader;

public interface PluginInfoProvider {
	List<PluginInfoProvider> PROVIDERS = ServiceLoader.load(PluginInfoProvider.class).stream().map(ServiceLoader.Provider::get).toList();

	static @Nullable PluginInfoProvider get(Plugin plugin) {
		return PROVIDERS.stream().filter(provider -> provider.isValid(plugin)).findFirst().orElse(null);
	}

	boolean isValid(Plugin plugin);

	File getDataFolder(Plugin plugin);

	URL getPluginLocation(Plugin plugin);

	InputStream getResource(Plugin plugin, String name);

	File getFile(Plugin plugin);
}
