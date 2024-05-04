package io.github.ultreon.skript.plugins;

import ch.njol.util.StringUtils;
import io.github.ultreon.skript.Plugin;

import java.util.List;

public interface PluginDescriptionFile {
    String getDescription();
    String getName();
    String getVersion();

    List<String> getDepend();

    List<String> getSoftDepend();

    String getMain();

    String getFullName();

    String getWebsite();
}
