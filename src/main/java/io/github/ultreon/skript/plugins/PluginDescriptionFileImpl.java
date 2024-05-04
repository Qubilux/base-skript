package io.github.ultreon.skript.plugins;

import org.intellij.lang.annotations.Language;

import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class PluginDescriptionFileImpl implements PluginDescriptionFile {
    private final String name;
    private final String version;

    public PluginDescriptionFileImpl(String name, String version) {
        this.name = name;
        this.version = version;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public List<String> getDepend() {
        return List.of();
    }

    @Override
    public List<String> getSoftDepend() {
        return List.of();
    }

    @Override
    public @Language("jvm-class-name") String getMain() {
        return "ch.njol.skript.Skript";
    }

    @Override
    public String getFullName() {
        return "Skript";
    }

    @Override
    public String getWebsite() {
        return "https://github.com/SkriptLang/Skript";
    }
}
