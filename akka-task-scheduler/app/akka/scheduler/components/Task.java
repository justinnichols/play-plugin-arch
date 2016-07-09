package akka.scheduler.components;

import java.util.Arrays;
import java.util.UUID;

public class Task<T extends Runnable> {
    public UUID id;
    private final Boolean autoStart;

    private final String delayTime;
    private final String startTime;
    private final String interval;
    private final Class<T> processorClass;
    private final Class[] processorConstructorClasses;
    private final Object[] processorConstructorArguments;
    private TaskStatus currentStatus;

    public Task(Class<T> processorClass, Class[] processorConstructorClasses, Object[] processorConstructorArguments,
                String startTime, String delayTime, String interval, Boolean autoStart) {
        this.processorClass = processorClass;
        this.processorConstructorClasses = processorConstructorClasses;
        this.processorConstructorArguments = processorConstructorArguments;
        this.currentStatus = TaskStatus.STOPPED;
        this.startTime = startTime;
        this.delayTime = delayTime;
        this.interval = interval;
        this.autoStart = autoStart;
    }

    public TaskStatus getCurrentStatus() {
        return this.currentStatus;
    }

    public void setCurrentStatus(TaskStatus status) {
        this.currentStatus = status;
    }

    public Boolean getAutoStart() {
        return autoStart;
    }

    public String getDelayTime() {
        return delayTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getInterval() {
        return interval;
    }

    public Class<T> getProcessorClass() {
        return processorClass;
    }

    public Class[] getProcessorConstructorClasses() {
        return processorConstructorClasses;
    }

    public Object[] getProcessorConstructorArguments() {
        return processorConstructorArguments;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", autoStart=" + autoStart +
                ", delayTime='" + delayTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", interval='" + interval + '\'' +
                ", processorClass=" + processorClass +
                ", processorConstructorClasses=" + Arrays.toString(processorConstructorClasses) +
                ", processorConstructorArguments=" + Arrays.toString(processorConstructorArguments) +
                ", currentStatus=" + currentStatus +
                '}';
    }
}
