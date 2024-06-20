package dev.ultreon.baseskript;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;

public interface PluginClassesProvider {
	List<PluginClassesProvider> PROVIDERS = ServiceLoader.load(PluginClassesProvider.class).stream().map(ServiceLoader.Provider::get).toList();

	static @Nullable PluginClassesProvider get(Plugin plugin) {
		return PROVIDERS.stream().filter(provider -> provider.isValid(plugin)).findFirst().orElse(null);
	}

	boolean isValid(Plugin plugin);

	Class<?>[] getClasses(Plugin plugin, String basePackage, String... subPackages) throws IOException;
}
