package controllers.api.dto.configurations;

import java.util.List;

public class ConfigurationsDto {
    public List<ConfigurationDto> configurations;

    public static ConfigurationsDto from(List<ConfigurationDto> configurations) {
        ConfigurationsDto dto = new ConfigurationsDto();
        dto.configurations = configurations;
        return dto;
    }
}
