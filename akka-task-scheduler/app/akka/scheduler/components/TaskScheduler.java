package akka.scheduler.components;

import akka.scheduler.exception.TaskNotRegisteredException;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public interface TaskScheduler {
    Task registerTask(Task task) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException;
    void deRegisterTask(String id) throws TaskNotRegisteredException;
    void deRegisterTask(UUID id) throws TaskNotRegisteredException;
    void startTask(String id) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException;
    void startTask(UUID id) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException;
    void startTask(String id, boolean withNoDelay) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException;
    void startTask(UUID id, boolean withNoDelay) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException;
    void stopTask(String id) throws TaskNotRegisteredException;
    void stopTask(UUID id) throws TaskNotRegisteredException;
    void restartTask(String id) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException;
    void restartTask(UUID id) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException;
    void restartTask(String id, boolean withNoDelay) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException;
    void restartTask(UUID id, boolean withNoDelay) throws NoSuchMethodException, InvocationTargetException, TaskNotRegisteredException, IllegalArgumentException, InstantiationException, IllegalAccessException;
    TaskStatus getStatus(String id) throws TaskNotRegisteredException;
    TaskStatus getStatus(UUID id) throws TaskNotRegisteredException;
}
