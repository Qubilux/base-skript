package ultreon.baseskript.plugins;

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
