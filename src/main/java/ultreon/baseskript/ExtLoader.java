package ultreon.baseskript;

import ch.njol.skript.Skript;
import ultreon.baseskript.classes.data.AWTClasses;
import ultreon.baseskript.plugins.PluginDescriptionFile;

import java.io.File;
import java.io.InputStream;

public class ExtLoader implements Plugin {
	public ExtLoader() {

	}

	@Override
	public String getName() {
		return "Skript-ExtLoader";
	}

	@Override
	public String getVersion0() {
		return "1.0.0";
	}

	@Override
	public InputStream getResource(String name) {
		return getClass().getResourceAsStream("/" + name);
	}

	@Override
	public File getDataFolder() {
		return new File(".skript/ext");
	}

	@Override
	public void onLoad() {
	}

	@Override
	public void onEnable() {
		try {
			new AWTClasses();
		} catch (Exception e) {
			Skript.exception(e, "An error occurred while loading extension classes.");
		}
	}

	@Override
	public void onDisable() {

	}

	@Override
	public PluginDescriptionFile getDescription() {
		return null;
	}
}
