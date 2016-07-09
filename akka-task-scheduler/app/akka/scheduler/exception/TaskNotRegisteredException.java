package akka.scheduler.exception;

import java.util.UUID;

public class TaskNotRegisteredException extends Exception {
    private UUID id;

    public TaskNotRegisteredException(UUID id) {
        this(id, "", null);
    }

    public TaskNotRegisteredException(UUID id, String message) {
        this(id, message, null);
    }

    public TaskNotRegisteredException(UUID id, String message, Throwable ex) {
        super(message, ex);
        this.id = id;
    }

    @Override
    public String toString() {
        return "ID: " + id + "\n" + super.toString();
    }
}
