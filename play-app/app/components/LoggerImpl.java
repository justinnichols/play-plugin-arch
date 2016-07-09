package components;

import play.Logger;

import javax.inject.Singleton;

@Singleton
public class LoggerImpl implements test.plugin.Logger {
    public void debug(String message) {
        Logger.debug(message);
    }
    public void debug(String message, Throwable error) {
        Logger.debug(message, error);
    }
    public void error(String message) {
        Logger.error(message);
    }
    public void error(String message, Throwable error) {
        Logger.error(message, error);
    }
    public void trace(String message) {
        Logger.trace(message);
    }
    public void trace(String message, Throwable error) {
        Logger.trace(message, error);
    }
    public void warn(String message) {
        Logger.warn(message);
    }
    public void warn(String message, Throwable error) {
        Logger.warn(message, error);
    }
    public void info(String message) {
        Logger.info(message);
    }
    public void info(String message, Throwable error) {
        Logger.info(message, error);
    }
}
