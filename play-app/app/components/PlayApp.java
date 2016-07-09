package components;

import akka.scheduler.components.Task;
import models.Configuration;
import org.apache.commons.lang3.tuple.Pair;
import ro.fortsoft.pf4j.PluginException;
import ro.fortsoft.pf4j.PluginState;
import ro.fortsoft.pf4j.PluginWrapper;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PlayApp {
    void init() throws Exception;
    Map<Configuration, Task> getTasks();
    List<Configuration> getConfigurations();
    Configuration getConfiguration(UUID id);
    Task getTaskById(UUID id);
    void startTask(UUID id) throws Exception;
    void stopTask(UUID id) throws Exception;
    void reconcileConfiguration(Configuration configuration) throws Exception;
    void addConfiguration(Configuration configuration);
    void deleteConfiguration(UUID configurationId) throws Exception;
    void deleteConfiguration(Configuration configuration) throws Exception;
    List<PluginWrapper> getPlugins();
    PluginWrapper getPlugin(String id);
    boolean deletePlugin(String id) throws PluginException;
    PluginState startPlugin(String id);
    PluginState stopPlugin(String id);
    boolean disablePlugin(String id);
    boolean enablePlugin(String id);
    String loadPlugin(File pluginFile);
    void loadAllPlugins();
    String getPluginDirectory();
    Map<PluginWrapper, List<Pair<String, String>>> getPluginsWithExtensions(PluginState pluginState);
}
