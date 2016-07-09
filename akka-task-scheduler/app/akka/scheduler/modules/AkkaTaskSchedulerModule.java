package akka.scheduler.modules;

import com.google.inject.AbstractModule;
import akka.scheduler.components.TaskScheduler;
import akka.scheduler.components.TaskSchedulerImpl;

public class AkkaTaskSchedulerModule extends AbstractModule {
    @Override
    public void configure() {
        bind(TaskScheduler.class).to(TaskSchedulerImpl.class);
    }
}
