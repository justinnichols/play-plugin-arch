package controllers.admin;

import models.Configuration;
import controllers.CustomController;
import play.mvc.Result;
import ro.fortsoft.pf4j.PluginState;
import views.html.admin.configurations.addConfiguration;
import views.html.admin.configurations.editConfiguration;
import views.html.admin.configurationsview;

import java.util.UUID;

public class Configurations extends CustomController {
    public Result index() {
        String error = flash("CONFIGURATIONS_ERROR");
        flash().clear();
        return ok(configurationsview.render(error));
    }

    public Result add() {
        return ok(addConfiguration.render(playApp.getPluginsWithExtensions(PluginState.STARTED)));
    }

    public Result edit(String id) {
        UUID uuid = null;
        try {
            uuid = UUID.fromString(id);
        } catch (Exception exc) {
            flash("CONFIGURATIONS_ERROR", "No configuration found for the given id.");
            return redirect(routes.Configurations.index());
        }

        Configuration configuration = playApp.getConfiguration(uuid);
        if (configuration == null) {
            flash("CONFIGURATIONS_ERROR", "No configuration found for the given id.");
            return redirect(routes.Configurations.index());
        }

        return ok(editConfiguration.render(configuration, playApp.getPluginsWithExtensions(PluginState.STARTED)));
    }
}
