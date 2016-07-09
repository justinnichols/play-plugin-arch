package controllers.api.dto.configurations;

import models.Schedule;

import javax.validation.constraints.NotNull;

public class ScheduleDto {
    public String delayTime;
    public String startTime;
    public String processInterval;
    public Boolean autoStart;

    public static ScheduleDto from(@NotNull Schedule schedule) throws Exception {
        ScheduleDto dto = new ScheduleDto();
        dto.delayTime = schedule.delayTime;
        dto.startTime = schedule.startTime;
        dto.processInterval = schedule.processInterval;
        dto.autoStart = schedule.autoStart;
        return dto;
    }
}
