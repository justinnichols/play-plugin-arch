package test.plugin.alpha;

import ro.fortsoft.pf4j.Plugin;
import ro.fortsoft.pf4j.PluginWrapper;

public class TestPluginAlpha extends Plugin {

    public TestPluginAlpha(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("TestPluginAlpha.start()");
    }

    @Override
    public void stop() {
        System.out.println("TestPluginAlpha.stop()");
    }
}
