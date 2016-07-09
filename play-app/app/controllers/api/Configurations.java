package controllers.api;

import akka.scheduler.exception.TaskNotRegisteredException;
import controllers.CustomController;
import controllers.api.dto.configurations.*;
import controllers.api.util.ResponseUtil;
import models.Configuration;
import models.Schedule;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.data.format.Formatters;
import play.data.validation.ValidatorProvider;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Configurations extends CustomController {
    private final FormFactory formFactory;

    @Inject
    public Configurations(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result getConfigurations() {
        try {
            List<Configuration> configurations = playApp.getConfigurations();
            List<ConfigurationDto> dtos = new ArrayList<>();
            for (Configuration config : configurations) {
                dtos.add(ConfigurationDto.from(config));
            }
            return ok(Json.toJson(ConfigurationsDto.from(dtos)));
        } catch (Exception ex) {
            Logger.error("An unhandled error occurred while attempting to retrieve configurations.", ex);
            return internalServerError(ResponseUtil.getErrorAsJson("An unhandled error occurred while attempting to retrieve configurations: " + ex.getMessage()));
        }
    }

    private Schedule getScheduleFromDto(SaveScheduleDto dto) {
        String scheduleDelay = null;
        if (dto.delayTimeVal != null && dto.delayTimeVal.trim().length() > 0 &&
                dto.delayTimeUnits != null && dto.delayTimeUnits.trim().length() > 0) {
            scheduleDelay = dto.delayTimeVal + dto.delayTimeUnits;
        }

        String scheduleStartTime = null;
        if (dto.startTimeHour != null && dto.startTimeHour.trim().length() > 0 &&
                dto.startTimeMinute != null && dto.startTimeMinute.trim().length() > 0 &&
                dto.startTimeAMPM != null && dto.startTimeAMPM.trim().length() > 0) {
            scheduleStartTime = dto.startTimeHour + ":" + dto.startTimeMinute + " " + dto.startTimeAMPM;
        }

        String scheduleInterval = null;
        if (dto.intervalTimeVal != null && dto.intervalTimeVal.trim().length() > 0 &&
                dto.intervalTimeUnits != null && dto.intervalTimeUnits.trim().length() > 0) {
            scheduleInterval = dto.intervalTimeVal + dto.intervalTimeUnits;
        }

        return Schedule.from(
                scheduleDelay,
                scheduleStartTime,
                scheduleInterval,
                dto.autoStart.trim().toLowerCase().equals("true"));
    }

    public Result createConfiguration() {
        try {
            Form<SaveConfigurationDto> form = formFactory.form(SaveConfigurationDto.class).bind(request().body().asJson());

            // validate
            if (form.hasErrors()) {
                return badRequest(form.errorsAsJson());
            }

            SaveConfigurationDto dto = form.get();

            Schedule schedule = getScheduleFromDto(dto.schedule);

            Configuration configuration = Configuration.from(
                    UUID.randomUUID(),
                    dto.enabled,
                    dto.name,
                    dto.processorPluginName,
                    dto.processorPluginVersion,
                    dto.processorPluginExtension,
                    schedule);

            playApp.addConfiguration(configuration);

            playApp.reconcileConfiguration(configuration);

            return ok(ResponseUtil.getSuccess(configuration.id));
        } catch (Exception ex) {
            Logger.error("An unhandled error occurred while attempting to create a new configuration.", ex);
            return internalServerError(ResponseUtil.getErrorAsJson("An unhandled error occurred while attempting to create a new configuration: " + ex.getMessage()));
        }
    }

    public Result updateConfiguration(String id) {
        try {
            Form<SaveConfigurationDto> form = formFactory.form(SaveConfigurationDto.class).bind(request().body().asJson());

            // validate
            if (form.hasErrors()) {
                return badRequest(form.errorsAsJson());
            }

            SaveConfigurationDto dto = form.get();

            UUID uuid = null;
            try {
                uuid = UUID.fromString(id);
            } catch (Exception exc) {
                return badRequest(ResponseUtil.getErrorAsJson("No configuration found for the given id."));
            }

            Configuration configuration = playApp.getConfiguration(uuid);
            if (configuration == null) {
                return badRequest(ResponseUtil.getErrorAsJson("No configuration found for the given id."));
            }

            Schedule schedule = getScheduleFromDto(dto.schedule);
            configuration.enabled = dto.enabled;
            configuration.name = dto.name;
            configuration.processorPluginName = dto.processorPluginName;
            configuration.processorPluginVersion = dto.processorPluginVersion;
            configuration.processorPluginExtension = dto.processorPluginExtension;
            configuration.schedule = schedule;

            playApp.reconcileConfiguration(configuration);

            return ok(ResponseUtil.getSuccess());
        } catch(Exception ex) {
            Logger.error("An unhandled error occurred while attempting to delete the configuration [id: " + id + "].", ex);
            return internalServerError(ResponseUtil.getErrorAsJson("An unhandled error occurred while attempting to delete the configuration [id: " + id + "]: " + ex.getMessage()));
        }
    }

    public Result deleteConfiguration(String id) {
        try {
            UUID uuid = null;
            try {
                uuid = UUID.fromString(id);
            } catch (Exception exc) {
                return badRequest(ResponseUtil.getErrorAsJson("No configuration found for the given id."));
            }

            Configuration configuration = playApp.getConfiguration(uuid);
            if (configuration == null) {
                return badRequest(ResponseUtil.getErrorAsJson("No configuration found for the given id."));
            }

            try {
                playApp.deleteConfiguration(configuration);
            } catch (TaskNotRegisteredException exc) {
                // NO OP
            }

            return ok(ResponseUtil.getSuccess());
        } catch(Exception ex) {
            Logger.error("An unhandled error occurred while attempting to delete the configuration [id: " + id + "].", ex);
            return internalServerError(ResponseUtil.getErrorAsJson("An unhandled error occurred while attempting to delete the configuration [id: " + id + "]: " + ex.getMessage()));
        }
    }
}
