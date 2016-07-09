package test.plugin.alpha.extensions;

import ro.fortsoft.pf4j.Extension;
import test.plugin.Logger;
import test.plugin.TestPluginExtension;

@Extension
public class TestExtensionAlpha implements TestPluginExtension {
    private final Logger logger;

    public TestExtensionAlpha(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void run() {
        logger.debug("TestExtensionAlpha.run() called!");
    }
}
