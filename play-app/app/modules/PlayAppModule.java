package modules;

import com.google.inject.AbstractModule;
import components.LoggerImpl;
import components.PlayApp;
import components.PlayAppImpl;
import test.plugin.Logger;

public class PlayAppModule extends AbstractModule {
    @Override
    public void configure() {
        bind(Logger.class).to(LoggerImpl.class);
        bind(PlayApp.class).to(PlayAppImpl.class);
    }
}
