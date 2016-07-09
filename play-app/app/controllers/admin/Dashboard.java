package controllers.admin;

import controllers.CustomController;
import play.mvc.Result;
import views.html.admin.dashboard;

public class Dashboard extends CustomController {
    public Result index() {
        return ok(dashboard.render());
    }
}
