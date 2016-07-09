package controllers.api.dto.plugins;

import ro.fortsoft.pf4j.PluginWrapper;

import javax.validation.constraints.NotNull;

public class PluginDto {
    public String pluginId;
    public String pluginVersion;
    public String pluginDescription;
    public String pluginState;

    public static PluginDto from(@NotNull PluginWrapper pluginWrapper) throws Exception {
        PluginDto dto = new PluginDto();
        dto.pluginId = pluginWrapper.getPluginId();
        dto.pluginVersion = pluginWrapper.getDescriptor().getVersion().toString();
        dto.pluginDescription = pluginWrapper.getDescriptor().getPluginDescription();
        dto.pluginState = pluginWrapper.getPluginState().toString();
        return dto;
    }
}
