package controllers;

import models.Faculty;
import models.MastersStudent;
import models.UserAccount;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.home;
import views.html.index;
import views.html.listaccount;

import java.io.File;

import static play.data.Form.form;

public class Application extends Controller {

    public static class Login {
        public String email;
        public String password;
    }

    public static Result index() {
        if (session().get("email") != null) {
            return ok(home.render("Hệ Thống Quản Lý Luận Văn Cao Học"));
        } else {
            return ok(index.render(form(Login.class)));
        }
    }

    public static Result home() {
        if (session().get("email") == null) {
            return redirect(routes.Application.index());
        }
        if (UserAccount.findByEmail(session().get("email")).isFaculty()) {
            return redirect(routes.Facultys.info(Faculty.findByEmail(session().get("email"))));
        }
        if (UserAccount.findByEmail(session().get("email")).isMastersStudent()) {
            return redirect(routes.MastersStudents.info(MastersStudent.findByEmail(session().get("email"))));
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
        if (UserAccount.authenticate(email, password) == null) {
            flash("error", "Địa chỉ email hoặc mật khẩu không đúng!");
            return redirect(routes.Application.index());
        }
        session("email", email);
        if (UserAccount.findByEmail(session().get("email")).isAdministrator()) {
            return redirect(routes.Application.admin());
        }
        return redirect(routes.Application.home());
    }

    public static Result listAccount() {
        return ok(listaccount.render());
    }

    public static Result admin() {
        if (session().get("email") == null) {
            return redirect(routes.Application.index());
        }
        if (!UserAccount.findByEmail(session().get("email")).isAdministrator()) {
            return redirect(routes.Application.home());
        }
        return ok(views.html.dashboard.render());
    }

}