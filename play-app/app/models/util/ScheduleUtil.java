package models.util;

import models.Schedule;

public class ScheduleUtil {
    public static String getValFromDelayTime(Schedule schedule) {
        if (schedule != null && schedule.delayTime != null && schedule.delayTime.trim().length() > 0) {
            return schedule.delayTime.replaceAll("[^\\d]","").trim();
        }

        return "";
    }

    public static String getUnitsFromDelayTime(Schedule schedule) {
        if (schedule != null && schedule.delayTime != null && schedule.delayTime.trim().length() > 0) {
            return schedule.delayTime.replaceAll("\\d","").trim();
        }

        return "";
    }

    public static String getValFromProcessInterval(Schedule schedule) {
        if (schedule != null && schedule.processInterval != null && schedule.processInterval.trim().length() > 0) {
            return schedule.processInterval.replaceAll("[^\\d]","").trim();
        }

        return "";
    }

    public static String getUnitsFromProcessInterval(Schedule schedule) {
        if (schedule != null && schedule.processInterval != null && schedule.processInterval.trim().length() > 0) {
            return schedule.processInterval.replaceAll("\\d","").trim();
        }

        return "";
    }

    public static String getHourFromStartTime(Schedule schedule) {
        if (schedule != null && schedule.startTime != null && schedule.startTime.trim().length() > 0) {
            return schedule.startTime.substring(0, schedule.startTime.indexOf(":"));
        }

        return "";
    }

    public static String getMinuteFromStartTime(Schedule schedule) {
        if (schedule != null && schedule.startTime != null && schedule.startTime.trim().length() > 0) {
            return schedule.startTime.substring(schedule.startTime.indexOf(":") + 1, schedule.startTime.lastIndexOf(" "));
        }

        return "";
    }

    public static String getAMPMFromStartTime(Schedule schedule) {
        if (schedule != null && schedule.startTime != null && schedule.startTime.trim().length() > 0) {
            return schedule.startTime.substring(schedule.startTime.lastIndexOf(" ") + 1);
        }

        return "";
    }
}
