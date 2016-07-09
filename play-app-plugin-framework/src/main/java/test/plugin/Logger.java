package test.plugin;

public interface Logger {
    void debug(String message);
    void debug(String message, Throwable error);
    void error(String message);
    void error(String message, Throwable error);
    void trace(String message);
    void trace(String message, Throwable error);
    void warn(String message);
    void warn(String message, Throwable error);
    void info(String message);
    void info(String message, Throwable error);
}
