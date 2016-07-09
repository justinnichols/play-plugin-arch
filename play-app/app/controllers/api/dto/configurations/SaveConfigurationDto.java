package controllers.api.dto.configurations;

import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class SaveConfigurationDto {
    public Boolean enabled;
    public String name;
    public String processorPluginName;
    public String processorPluginVersion;
    public String processorPluginExtension;
    public SaveScheduleDto schedule;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessorPluginName() {
        return processorPluginName;
    }

    public void setProcessorPluginName(String processorPluginName) {
        this.processorPluginName = processorPluginName;
    }

    public String getProcessorPluginVersion() {
        return processorPluginVersion;
    }

    public void setProcessorPluginVersion(String processorPluginVersion) {
        this.processorPluginVersion = processorPluginVersion;
    }

    public String getProcessorPluginExtension() {
        return processorPluginExtension;
    }

    public void setProcessorPluginExtension(String processorPluginExtension) {
        this.processorPluginExtension = processorPluginExtension;
    }

    public SaveScheduleDto getSchedule() {
        return schedule;
    }

    public void setSchedule(SaveScheduleDto schedule) {
        this.schedule = schedule;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (enabled == null) {
            errors.add(new ValidationError("enabled", "Enabled is required."));
        }
        if (name == null || name.trim().length() == 0) {
            errors.add(new ValidationError("name", "Name is required."));
        }

        return (errors.size() > 0) ? errors : null;
    }
}
