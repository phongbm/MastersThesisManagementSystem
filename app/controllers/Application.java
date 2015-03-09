package controllers;

import models.MastersStudent;
import models.UserAccount;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.home;
import views.html.index;
import views.html.listaccount;

import static play.data.Form.form;

public class Application extends Controller {

    public static class Login {
        public String email;
        public String password;
    }

    public static Result index() {
        if (session().get("email") != null) {
            return ok(home.render("Master's Thesis Management System"));
        } else {
            return ok(index.render(form(Login.class)));
        }
    }

    public static Result home() {
        return ok(home.render("Master's Thesis Management System"));
    }

    /*
    public static Result login() {
        if (session().get("email") != null) {
            return redirect(routes.Application.index());
        }
        return ok(login.render(form(Login.class)));
    }
    */

    /*
    public static Result testLogin() {
        if (session().get("email") != null) {
            return redirect(routes.Application.index());
        }
        return ok(testlogin.render(form(Login.class)));

    }
    */

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
            return redirect(routes.Application.index());
        }

        session("email", email);
        /*
        if (!email.equals("giaovu@vnu.edu.vn")) {
            MastersStudent mastersStudent = MastersStudent.findByEmail(email);
            return redirect(routes.MastersStudents.detailsReadOnly(mastersStudent));
        } else {
            return redirect(routes.Application.index());
        }
        */
        UserAccount userAccount = UserAccount.findByEmail(email);
        if (userAccount.isAdministrator()) {
            return redirect(routes.Application.home());
        } else {
            if (userAccount.isUser()) {
                MastersStudent mastersStudent = MastersStudent.findByEmail(email);
                return redirect(routes.MastersStudents.detailsReadOnly(mastersStudent));
            } else {
                return ok();
            }
        }
    }

    public static Result listAccount() {
        return ok(listaccount.render());
    }

}