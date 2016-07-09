package controllers.api.dto.configurations;

import models.Configuration;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ConfigurationDto {
    public UUID id;
    public Boolean enabled;
    public String name;
    public String processorPluginName;
    public String processorPluginVersion;
    public String processorPluginExtension;
    public ScheduleDto schedule;

    public static ConfigurationDto from(@NotNull Configuration configuration) throws Exception {
        ConfigurationDto dto = new ConfigurationDto();
        dto.enabled = configuration.enabled;
        dto.id = configuration.id;
        dto.name = configuration.name;
        dto.processorPluginName = configuration.processorPluginName;
        dto.processorPluginVersion = configuration.processorPluginVersion;
        dto.processorPluginExtension = configuration.processorPluginExtension;
        if (configuration.schedule != null) {
            dto.schedule = ScheduleDto.from(configuration.schedule);
        } else {
            dto.schedule = null;
        }
        return dto;
    }
}
