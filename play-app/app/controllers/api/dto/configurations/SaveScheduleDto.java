package controllers.api.dto.configurations;

import java.util.ArrayList;
import java.util.List;

public class SaveScheduleDto {
    public String startTimeHour;
    public String startTimeMinute;
    public String startTimeAMPM;
    public String delayTimeVal;
    public String delayTimeUnits;
    public String intervalTimeVal;
    public String intervalTimeUnits;
    public String autoStart;

    public String getStartTimeHour() {
        return startTimeHour;
    }

    public void setStartTimeHour(String startTimeHour) {
        this.startTimeHour = startTimeHour;
    }

    public String getStartTimeMinute() {
        return startTimeMinute;
    }

    public void setStartTimeMinute(String startTimeMinute) {
        this.startTimeMinute = startTimeMinute;
    }

    public String getStartTimeAMPM() {
        return startTimeAMPM;
    }

    public void setStartTimeAMPM(String startTimeAMPM) {
        this.startTimeAMPM = startTimeAMPM;
    }

    public String getDelayTimeVal() {
        return delayTimeVal;
    }

    public void setDelayTimeVal(String delayTimeVal) {
        this.delayTimeVal = delayTimeVal;
    }

    public String getDelayTimeUnits() {
        return delayTimeUnits;
    }

    public void setDelayTimeUnits(String delayTimeUnits) {
        this.delayTimeUnits = delayTimeUnits;
    }

    public String getIntervalTimeVal() {
        return intervalTimeVal;
    }

    public void setIntervalTimeVal(String intervalTimeVal) {
        this.intervalTimeVal = intervalTimeVal;
    }

    public String getIntervalTimeUnits() {
        return intervalTimeUnits;
    }

    public void setIntervalTimeUnits(String intervalTimeUnits) {
        this.intervalTimeUnits = intervalTimeUnits;
    }

    public String getAutoStart() {
        return autoStart;
    }

    public void setAutoStart(String autoStart) {
        this.autoStart = autoStart;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if ((startTimeHour == null || startTimeHour.trim().length() == 0) &&
                (startTimeMinute == null || startTimeMinute.trim().length() == 0) &&
                (delayTimeVal == null || delayTimeVal.trim().length() == 0)) {
            errors.add("Either a schedule start time or a delay time must be set.");
        } else if (delayTimeVal == null || delayTimeVal.trim().length() == 0) {
            if (startTimeHour == null || startTimeHour.trim().length() == 0) {
                errors.add("Schedule Start Time Hour value is required.");
            } else {
                try {
                    int startTimeHourInt = Integer.parseInt(startTimeHour.trim());
                    if (startTimeHourInt < 1 || startTimeHourInt > 12) {
                        errors.add("Schedule Start Time Hour value must be an integer between 1 and 12.");
                    }
                } catch(Exception ex) {
                    errors.add("Schedule Start Time Hour value must be an integer between 1 and 12.");
                }
            }
            if (startTimeMinute == null || startTimeMinute.trim().length() == 0) {
                errors.add("Schedule Start Time Minute value is required.");
            } else {
                try {
                    int startTimeMinuteInt = Integer.parseInt(startTimeMinute.trim());
                    if (startTimeMinuteInt < 0 || startTimeMinuteInt > 59) {
                        errors.add("Schedule Start Time Minute value must be an integer between 0 and 59.");
                    }
                } catch(Exception ex) {
                    errors.add("Schedule Start Time Minute value must be an integer between 0 and 59.");
                }
            }
            if (startTimeAMPM == null ||
                    !(startTimeAMPM.toUpperCase().equals("AM") ||
                            startTimeAMPM.toUpperCase().equals("PM"))) {
                errors.add("Schedule Start Time AM/PM is required and must be 'AM' or 'PM'.");
            }
        } else {
            try {
                int delayTime = Integer.parseInt(delayTimeVal.trim());
                if (delayTime <= 0) {
                    errors.add("Schedule Delay Time value must be an integer greater than 0.");
                }
            } catch(Exception ex) {
                errors.add("Schedule Delay Time value must be an integer greater than 0.");
            }

            if (delayTimeUnits == null ||
                    !(delayTimeUnits.toLowerCase().equals("seconds") ||
                            delayTimeUnits.toLowerCase().equals("minutes") ||
                            delayTimeUnits.toLowerCase().equals("hours"))) {
                errors.add("Schedule Delay Time units is required and must be 'seconds', 'minutes', or 'hours'.");
            }
        }
        if (intervalTimeVal == null || intervalTimeVal.trim().length() == 0) {
            errors.add("Schedule Interval Time Value is required.");
        } else {
            try {
                int intervalTime = Integer.parseInt(intervalTimeVal.trim());
                if (intervalTime <= 0) {
                    errors.add("Schedule Interval Time value must be an integer greater than 0.");
                }
            } catch(Exception ex) {
                errors.add("Schedule Interval Time value must be an integer greater than 0.");
            }
        }
        if (intervalTimeUnits == null ||
                !(intervalTimeUnits.toLowerCase().equals("seconds") ||
                        intervalTimeUnits.toLowerCase().equals("minutes") ||
                        intervalTimeUnits.toLowerCase().equals("hours"))) {
            errors.add("Schedule Interval Time units is required and must be 'seconds', 'minutes', or 'hours'.");
        }
        if (autoStart == null) {
            errors.add("Auto Start is required.");
        } else {
            try {
                Boolean.parseBoolean(autoStart.trim().toLowerCase());
            } catch(Exception ex) {
                errors.add("Schedule Auto Start must be set 'Yes' or 'No'.");
            }
        }
        
        return errors;
    }
}
