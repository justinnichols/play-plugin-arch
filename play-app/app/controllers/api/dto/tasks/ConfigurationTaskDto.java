package controllers.api.dto.tasks;

import akka.scheduler.components.Task;
import controllers.api.dto.configurations.ConfigurationDto;
import models.Configuration;

public class ConfigurationTaskDto {
    public ConfigurationDto configuration;
    public TaskDto task;

    public static ConfigurationTaskDto from(Configuration configuration, Task task)  throws Exception {
        ConfigurationTaskDto dto = new ConfigurationTaskDto();
        dto.configuration = ConfigurationDto.from(configuration);
        dto.task = TaskDto.from(task);
        return dto;
    }
}
