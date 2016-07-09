package controllers.api.dto.tasks;

import java.util.List;

public class TasksDto {
    public List<ConfigurationTaskDto> tasks;

    public static TasksDto from(List<ConfigurationTaskDto> tasks) {
        TasksDto dto = new TasksDto();
        dto.tasks = tasks;
        return dto;
    }
}
