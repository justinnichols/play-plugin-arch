package controllers.api;

import akka.scheduler.components.Task;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.CustomController;
import controllers.api.dto.tasks.ConfigurationTaskDto;
import controllers.api.dto.tasks.TasksDto;
import controllers.api.util.ResponseUtil;
import models.Configuration;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Tasks extends CustomController {
    public Result getTasks() {
        try {
            String taskType = request().getQueryString("type");

            List<ConfigurationTaskDto> taskDtos = new ArrayList<>();

            Map<Configuration, Task> tasks = playApp.getTasks();
            for (Configuration config : tasks.keySet()) {
                taskDtos.add(ConfigurationTaskDto.from(config, tasks.get(config)));
            }

            return ok(Json.toJson(TasksDto.from(taskDtos)));
        } catch (Exception ex) {
            Logger.error("An unhandled error occurred while attempting to retrieve tasks.", ex);
            return internalServerError(ResponseUtil.getErrorAsJson("An unhandled error occurred while attempting to retrieve tasks: " + ex.getMessage()));
        }
    }

    public Result updateTask(String id) {
        try {
            Task task = playApp.getTaskById(UUID.fromString(id));

            if (task == null) {
                return notFound(ResponseUtil.getErrorAsJson("No task found with the given id."));
            }

            JsonNode json = request().body().asJson();

            if (json.has("status")) {
                String status = json.get("status").asText();
                if (status != null) {
                    switch (status.toUpperCase()) {
                        case "START":
                            playApp.startTask(UUID.fromString(id));
                            break;
                        case "STOP":
                            playApp.stopTask(UUID.fromString(id));
                            break;
                        default:
                            break;
                    }
                }
            }

            return ok(ResponseUtil.getSuccess());
        } catch (Exception ex) {
            return internalServerError(ResponseUtil.getErrorAsJson("An unexpected error occurred. " + ex.getMessage()));
        }
    }
}
