package components;

import akka.scheduler.components.Task;
import akka.scheduler.components.TaskScheduler;
import akka.scheduler.components.TaskStatus;
import models.Configuration;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import play.inject.ApplicationLifecycle;
import ro.fortsoft.pf4j.*;
import play.Logger;
import test.plugin.TestPluginExtension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class PlayAppImpl implements PlayApp {
    private PluginManager pluginManager;
    private final test.plugin.Logger logger;
    private final TaskScheduler taskScheduler;
    private final Map<UUID, Configuration> configurations;
    private final Map<Configuration, Task> tasks;
    private final Map<Task, Configuration> configurationsByTask;
    private final Map<UUID, Task> tasksById;
    private final Map<PluginWrapper, List<Task>> tasksByPlugin;
    private final Map<Task, PluginWrapper> pluginByTask;

    @Inject
    public PlayAppImpl(ApplicationLifecycle lifecycle,
                       test.plugin.Logger logger,
                       TaskScheduler taskScheduler) throws Exception {
        // on start
        this.logger = logger;
        this.taskScheduler = taskScheduler;
        this.tasks = new HashMap<>();
        this.configurations = new HashMap<>();
        this.configurationsByTask = new HashMap<>();
        this.tasksById = new HashMap<>();
        this.tasksByPlugin = new HashMap<>();
        this.pluginByTask = new HashMap<>();

        init();

        lifecycle.addStopHook(() -> {
            // on stop
            Logger.info("Stopping tasks...");
            for (UUID uuid : tasksById.keySet()) {
                try {
                    stopTask(uuid);
                } catch (Exception ex) {
                    // no op
                }
            }
            Logger.info("Tasks stopped.");
            this.pluginManager.stopPlugins();
            return CompletableFuture.completedFuture(null);
        });
    }

    public void init() throws Exception {
        try {
            this.pluginManager = new DefaultPluginManager();
            this.pluginManager.loadPlugins();
            this.pluginManager.startPlugins();

            this.pluginManager.addPluginStateListener(event -> {
                try {
                    reconcilePluginWithTasks(event.getPlugin());
                } catch (Exception ex) {
                    Logger.error("Exception: " + ex.getMessage(), ex);
                }
            });
        } catch (Exception ex) {
            Logger.error("Exception: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<PluginWrapper> getPlugins() {
        return this.pluginManager.getPlugins();
    }

    public PluginWrapper getPlugin(String id) {
        return this.pluginManager.getPlugin(id);
    }

    public boolean deletePlugin(String id) {
        PluginWrapper pluginWrapper = this.pluginManager.getPlugin(id);
        boolean deleted = this.pluginManager.deletePlugin(id);
        if (deleted) {
            this.tasksByPlugin.remove(pluginWrapper);
        }
        return deleted;
    }

    public PluginState startPlugin(String id) {
        return this.pluginManager.startPlugin(id);
    }

    public PluginState stopPlugin(String id) {
        return this.pluginManager.stopPlugin(id);
    }

    public boolean disablePlugin(String id) {
        return this.pluginManager.disablePlugin(id);
    }

    public boolean enablePlugin(String id) {
        return this.pluginManager.enablePlugin(id);
    }

    public String loadPlugin(File pluginFile) {
        return this.pluginManager.loadPlugin(pluginFile);
    }

    public void loadAllPlugins() {
        this.pluginManager.loadPlugins();
    }

    @SuppressWarnings("unchecked")
    public Map<PluginWrapper, List<Pair<String, String>>> getPluginsWithExtensions(PluginState pluginState) {
        Map<PluginWrapper, List<Pair<String, String>>> pluginMap = new HashMap<>();

        List<PluginWrapper> pluginWrappers = this.pluginManager.getPlugins(pluginState);
        for (PluginWrapper pluginWrapper : pluginWrappers) {
            Set<String> extensions = this.pluginManager.getExtensionClassNames(pluginWrapper.getPluginId());
            List<String> extensionsList = new ArrayList<>(extensions);
            Collections.sort(extensionsList);

            List<Pair<String, String>> extensionPairs = extensionsList.stream().map(extension -> new ImmutablePair<>(extension, TestPluginExtension.class.getName())).collect(Collectors.toList());
            pluginMap.put(pluginWrapper, extensionPairs);
        }

        return pluginMap;
    }

    public String getPluginDirectory() {
        return DefaultPluginManager.DEFAULT_PLUGINS_DIRECTORY;
    }

    public Map<Configuration, Task> getTasks() {
        return this.tasks;
    }

    public List<Configuration> getConfigurations() {
        return new ArrayList<>(this.configurations.values());
    }

    public Configuration getConfiguration(UUID id) {
        return this.configurations.get(id);
    }

    public Task getTaskById(UUID id) {
        return this.tasksById.get(id);
    }

    public void startTask(UUID id) throws Exception {
        Logger.debug("Starting task " + id.toString());
        this.taskScheduler.startTask(id);
        Logger.debug("Task " + id.toString() + " started.");
    }

    public void stopTask(UUID id) throws Exception {
        Logger.debug("Stopping task " + id.toString());
        this.taskScheduler.stopTask(id);
        Logger.debug("Task " + id.toString() + " stopped.");
    }

    public void reconcilePluginWithTasks(PluginWrapper pluginWrapper) throws Exception {
        switch(pluginWrapper.getPluginState().toString()) {
            case "CREATED":
            case "STOPPED":
            case "DISABLED":
                List<Task> tasks = this.tasksByPlugin.get(pluginWrapper);
                if (tasks != null) {
                    List<Task> tasksToStopAndRemove = new ArrayList<>();
                    tasksToStopAndRemove.addAll(tasks);
                    for (Task task : tasksToStopAndRemove) {
                        stopTask(task.id);
                        removeTask(this.configurationsByTask.get(task), task);
                    }
                }
                break;
            case "STARTED":
                for (Configuration configuration : this.configurations.values()) {
                    if (this.pluginManager.getPlugin(configuration.processorPluginName) != null &&
                            this.tasks.get(configuration) == null &&
                            configuration.schedule != null) {
                        addTask(configuration);
                    }
                }
                break;
        }
    }

    public void reconcileConfiguration(Configuration configuration) throws Exception {
        if (configuration.enabled) {
            if (!this.tasks.containsKey(configuration)) {
                if (configuration.schedule != null) {
                    addTask(configuration);
                }
            } else {
                updateTask(configuration, this.tasks.get(configuration));
            }

            this.configurations.put(configuration.id, configuration);
        } else {
            if (this.tasks.containsKey(configuration)) {
                deleteConfiguration(configuration);
            }
        }
    }

    public void addConfiguration(Configuration configuration) {
        if (!this.configurations.containsKey(configuration.id)) {
            this.configurations.put(configuration.id, configuration);
        }
    }

    public void deleteConfiguration(UUID configurationId) throws Exception {
        if (this.configurations.containsKey(configurationId)) {
            deleteConfiguration(this.configurations.get(configurationId));
        }
    }

    public void deleteConfiguration(Configuration configuration) throws Exception {
        Task task = this.tasks.get(configuration);
        if (this.configurations.containsKey(configuration.id)) {
            this.configurations.remove(configuration.id);
        }
        if (task != null) {
            removeTask(configuration, task);
        }
    }

    private void removeTask(Configuration configuration, Task task) throws Exception {
        // plugin
        this.tasksByPlugin.get(this.pluginByTask.get(task)).remove(task);
        this.configurationsByTask.remove(task);
        this.tasks.remove(configuration);
        this.tasksById.remove(task.id);
        Logger.debug("De-registering task " + task.id.toString() + " for configuration " + configuration.name);
        this.taskScheduler.deRegisterTask(task.id);
        Logger.debug("Task " + task.id.toString() + " de-registered for configuration " + configuration.name);
    }

    private void updateTask(Configuration configuration, Task task) throws Exception {
        removeTask(configuration, task);
        addTask(configuration);
    }

    @SuppressWarnings("unchecked")
    private void addTask(Configuration configuration) throws Exception {
        if (configuration.processorPluginExtension != null) {
            PluginWrapper plugin = this.pluginManager.getPlugin(configuration.processorPluginName);
            if (plugin.getPluginState().equals(PluginState.STARTED)) {
                Class clazz = plugin.getPluginClassLoader().loadClass(configuration.processorPluginExtension);

                Task task = new Task(
                        clazz,
                        new Class[]{test.plugin.Logger.class},
                        new Object[]{this.logger},
                        configuration.schedule.startTime,
                        configuration.schedule.delayTime,
                        configuration.schedule.processInterval,
                        configuration.schedule.autoStart
                );

                this.configurationsByTask.put(task, configuration);

                this.tasksByPlugin.putIfAbsent(plugin, new ArrayList<>());
                this.tasksByPlugin.get(plugin).add(task);
                this.pluginByTask.put(task, plugin);

                Logger.debug("Registering task for configuration " + configuration.name);
                taskScheduler.registerTask(task);
                if (task.getCurrentStatus().equals(TaskStatus.STARTING) || task.getCurrentStatus().equals(TaskStatus.STARTED)) {
                    Logger.debug("Task " + task.id.toString() + " started.");
                }
                Logger.debug("Task registered for configuration " + configuration.name);
                this.tasks.put(configuration, task);
                this.tasksById.put(task.id, task);
            }
        }
    }
}
