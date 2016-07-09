package models;

import java.util.UUID;

public class Configuration implements Comparable<Configuration> {
    public UUID id;
    public Boolean enabled = true;
    public String name;
    public String processorPluginName;
    public String processorPluginVersion;
    public String processorPluginExtension;
    public Schedule schedule;

    public static Configuration from(UUID id, Boolean enabled, String name,
                                     String processorPluginName, String processorPluginVersion,
                                     String processorPluginExtension, Schedule schedule) {
        Configuration configuration = new Configuration();
        configuration.id = id;
        configuration.enabled = enabled;
        configuration.name = name;
        configuration.processorPluginName = processorPluginName;
        configuration.processorPluginVersion = processorPluginVersion;
        configuration.processorPluginExtension = processorPluginExtension;
        configuration.schedule = schedule;
        return configuration;
    }

    @Override
    public int compareTo(Configuration configuration) {
        if (configuration.name == null) {
            return 1;
        }

        if (this.name == null) {
            return -1;
        }

        return this.name.compareTo(configuration.name);
    }
}
