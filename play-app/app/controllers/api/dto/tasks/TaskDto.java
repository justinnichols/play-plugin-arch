package controllers.api.dto.tasks;

import akka.scheduler.components.Task;
import akka.scheduler.components.TaskStatus;

import java.util.UUID;

public class TaskDto {
    public UUID id;
    public String processor;
    public TaskStatus currentStatus;

    public static TaskDto from(Task task) {
        TaskDto dto = new TaskDto();
        dto.id = task.id;
        if (task.getProcessorClass() != null) {
            dto.processor = task.getProcessorClass().getName();
        } else {
            dto.processor = null;
        }
        dto.currentStatus = task.getCurrentStatus();
        return dto;
    }
}
