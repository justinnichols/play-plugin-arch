package akka.scheduler.components;

import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.scheduler.exception.TaskNotRegisteredException;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import play.inject.ApplicationLifecycle;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class TaskSchedulerImpl implements TaskScheduler {
    private static final Map<UUID, Task> taskMap = new HashMap<>();
    private static final Map<UUID, Cancellable> runningTasksMap = new HashMap<>();
    private final ActorSystem actorSystem;

    @Inject
    public TaskSchedulerImpl(ApplicationLifecycle lifecycle, ActorSystem actorSystem) {
        // onStart
        this.actorSystem = actorSystem;

        lifecycle.addStopHook( () -> {
            // onStop
            taskMap.clear();
            runningTasksMap.values().forEach(Cancellable::cancel);
            runningTasksMap.clear();

            return CompletableFuture.completedFuture(null);
        });
    }

    public Task registerTask(Task task) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException {
        if (task.id != null && taskMap.containsKey(task.id)) {
            return task;
        }

        if (task.id == null) {
            task.id = UUID.randomUUID();
        }
        taskMap.put(task.id, task);

        if (task.getAutoStart()) {
            startTask(task.id, false);
        }
        return task;
    }

    public void deRegisterTask(String id) throws TaskNotRegisteredException {
        deRegisterTask(UUID.fromString(id));
    }

    public void deRegisterTask(UUID id) throws TaskNotRegisteredException {
        synchronized (this) {
            stopTask(id);
            taskMap.remove(id);
        }
    }

    public void startTask(String id) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException {
        startTask(UUID.fromString(id), false);
    }

    public void startTask(UUID id) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException {
        startTask(id, false);
    }

    public void startTask(String id, boolean withNoDelay) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException {
        startTask(UUID.fromString(id), withNoDelay);
    }

    @SuppressWarnings("unchecked")
    public void startTask(UUID id, boolean withNoDelay) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException {
        synchronized (this) {
            Task task = taskMap.get(id);

            if (task == null) {
                throw new TaskNotRegisteredException(id);
            }

            Cancellable cancellable = runningTasksMap.get(id);

            if (cancellable != null) {
                return;
            }

            task.setCurrentStatus(TaskStatus.STARTING);

            FiniteDuration startDuration = null;
            FiniteDuration intervalDuration = getFiniteDurationFromString(task.getInterval());
            if (withNoDelay) {
                startDuration = Duration.Zero();
            } else if (task.getDelayTime() != null) {
                startDuration = getFiniteDurationFromString(task.getDelayTime());
            } else if (task.getStartTime() != null) {
                startDuration = getFiniteDurationFromStartTimeString(task.getStartTime());
            } else {
                throw new IllegalArgumentException("The delayTime or startTime parameter was not set.");
            }

            Runnable runner;
            if (task.getProcessorConstructorClasses() != null) {
                runner = (Runnable) task.getProcessorClass().getConstructor(task.getProcessorConstructorClasses()).newInstance(task.getProcessorConstructorArguments());
            } else {
                runner = (Runnable) task.getProcessorClass().newInstance();
            }

            runningTasksMap.put(
                    id,
                    actorSystem.scheduler().schedule(
                            startDuration,
                            intervalDuration,
                            runner,
                            actorSystem.dispatcher())
            );

            task.setCurrentStatus(TaskStatus.STARTED);
        }
    }

    public void stopTask(String id) throws TaskNotRegisteredException {
        stopTask(UUID.fromString(id));
    }

    public void stopTask(UUID id) throws TaskNotRegisteredException {
        synchronized (this) {
            Task task = taskMap.get(id);

            if (task == null) {
                throw new TaskNotRegisteredException(id);
            }

            Cancellable cancellable = runningTasksMap.get(id);

            if (cancellable == null) {
                return;
            }

            task.setCurrentStatus(TaskStatus.STOPPING);
            runningTasksMap.remove(id);
            cancellable.cancel();
            task.setCurrentStatus(TaskStatus.STOPPED);
        }
    }

    public void restartTask(String id) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException {
        restartTask(UUID.fromString(id), false);
    }

    public void restartTask(UUID id) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException {
        restartTask(id, false);
    }

    public void restartTask(String id, boolean withNoDelay) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException {
        restartTask(UUID.fromString(id), withNoDelay);
    }

    public void restartTask(UUID id, boolean withNoDelay) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException {
        synchronized (this) {
            stopTask(id);
            startTask(id, withNoDelay);
        }
    }

    public TaskStatus getStatus(String id) throws TaskNotRegisteredException {
        return getStatus(UUID.fromString(id));
    }

    public TaskStatus getStatus(UUID id) throws TaskNotRegisteredException {
        Task task = taskMap.get(id);

        if (task == null) {
            throw new TaskNotRegisteredException(id);
        }

        return task.getCurrentStatus();
    }


    /* Utility Methods */
    private final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("hh:mm a");
    private FiniteDuration getFiniteDurationFromStartTimeString(String input) {
        LocalTime time = LocalTime.parse(input, timeFormatter);
        return FiniteDuration.create(nextExecutionInSeconds(time.getHourOfDay(), time.getMinuteOfHour()), TimeUnit.SECONDS);
    }

    private FiniteDuration getFiniteDurationFromString(String input) {
        String durationVal = input.replaceAll("[^\\d]","").trim();
        String timeUnit = input.replaceAll("\\d","").trim();
        return FiniteDuration.create(Long.parseLong(durationVal), timeUnit);
    }

    private int nextExecutionInSeconds(int hour, int minute) {
        return Seconds.secondsBetween(
                new DateTime(),
                nextExecution(hour, minute)
        ).getSeconds();
    }

    private DateTime nextExecution(int hour, int minute) {
        DateTime next = new DateTime()
                .withHourOfDay(hour)
                .withMinuteOfHour(minute)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);

        return (next.isBeforeNow())
                ? next.plusHours(24)
                : next;
    }
}
