package models;

public class Schedule {
    public String delayTime;
    public String startTime;
    public String processInterval;
    public Boolean autoStart;

    public static Schedule from(String delayTime, String startTime, String processInterval, Boolean autoStart) {
        Schedule schedule = new Schedule();
        schedule.delayTime = delayTime;
        schedule.startTime = startTime;
        schedule.processInterval = processInterval;
        schedule.autoStart = autoStart;
        return schedule;
    }
}
