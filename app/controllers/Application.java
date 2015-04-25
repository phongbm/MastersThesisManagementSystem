package controllers;

import models.Faculty;
import models.MastersStudent;
import models.UserAccount;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
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
            return redirect(routes.Application.home());
        } else {
            return ok(index.render(form(Login.class)));
        }
    }

    public static Result home() {
        String email = session().get("email");
        if (Faculty.findByEmail(email) != null) {
            return redirect(routes.Faculties.info(Faculty.findByEmail(email)));
        }
        if (MastersStudent.findByEmail(email) != null) {
            return redirect(routes.MastersStudents.info(MastersStudent.findByEmail(email)));
        }
        return redirect(routes.Application.admin());
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
        if (UserAccount.authenticate(email, password) != null ||
                MastersStudent.authenticate(email, password) != null ||
                Faculty.authenticate(email, password) != null) {
            session("email", email);
            return redirect(routes.Application.home());
        }
        flash("error", "Địa chỉ email hoặc mật khẩu không đúng!");
        return redirect(routes.Application.index());
    }

    public static Result listAccount() {
        return ok(listaccount.render());
    }

    public static Result admin() {
        if (session().get("email") == null) {
            return redirect(routes.Application.index());
        }
        if (UserAccount.findByEmail(session().get("email")) == null) {
            return redirect(routes.Application.home());
        }
        return ok(views.html.dashboard.render());
    }

}