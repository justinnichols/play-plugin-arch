package controllers.admin;

import controllers.CustomController;
import play.mvc.Result;

public class Index extends CustomController {
    public Result index() {
        return redirect(controllers.admin.routes.Dashboard.index());
    }
}
