package controllers.admin;

import controllers.CustomController;
import play.mvc.Result;
import views.html.admin.pluginsview;

public class Plugins extends CustomController {
    public Result index() {
        String error = flash("PLUGINS_ERROR");
        flash().clear();
        return ok(pluginsview.render(error));
    }
}
