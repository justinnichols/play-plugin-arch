package controllers.api.dto.plugins;

import java.util.List;

public class PluginsDto {
    public List<PluginDto> plugins;

    public static PluginsDto from(List<PluginDto> plugins) {
        PluginsDto dto = new PluginsDto();
        dto.plugins = plugins;
        return dto;
    }
}
