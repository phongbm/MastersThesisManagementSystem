package controllers;

import models.UserAccount;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

import static play.data.Form.form;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready"));
    }

    public static class Login {
        public String email;
        public String password;
    }

    public static Result login() {
        if(session().get("email") != null){
            return redirect(routes.Application.index());
        }
        return ok(login.render(form(Login.class)));
    }

    public static Result logout() {
        session().clear();
        return redirect(routes.Application.index());
    }

    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        String email = loginForm.get().email;
        String password = loginForm.get().password;

        session().clear();
        if (UserAccount.authenticate(email, password) == null) {
            flash("error", "Invalid email and/or password");
            return redirect(routes.Application.login());
        }
        session("email", email);
        return redirect(routes.MastersStudents.list(0));
    }

}