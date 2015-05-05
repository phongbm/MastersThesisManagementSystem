package controllers;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context context) {
        return context.session().get("email");
    }

    @Override
    public Result onUnauthorized(Context context) {
        return redirect(routes.Application.index());
    }

}