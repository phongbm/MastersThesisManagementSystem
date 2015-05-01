package controllers;

import models.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.passwords.changepassword;
import views.html.passwords.forgotpassword;
import views.html.passwords.forgotpasswordsuccess;

import static play.data.Form.form;

public class Passwords extends Controller {

    public static class ChangePassword {
        public String currentPassword;
        public String newPassword;
        public String confirmPassword;
    }

    public static class ForgotPassword {
        public String email;
    }

    public static Result changePassword() {
        if (session().get("email") == null) {
            return redirect(routes.Application.index());
        }
        return ok(changepassword.render(form(ChangePassword.class)));
    }

    public static Result updatePassword() {
        Form<ChangePassword> changePasswordForm = form(ChangePassword.class).bindFromRequest();
        if (changePasswordForm.hasErrors()) {
            flash("error", "Vui lòng hoàn thành đúng mẫu!");
            return badRequest(changepassword.render(changePasswordForm));
        }
        String currentPassword = changePasswordForm.get().currentPassword;
        String newPassword = changePasswordForm.get().newPassword;
        String emailUser = session().get("email");
        if (Faculty.findByEmail(emailUser) != null) {
            Faculty faculty = Faculty.findByEmail(emailUser);
            if (!faculty.password.equals(currentPassword)) {
                flash("error", "Mật khẩu hiện tại không đúng!");
                return badRequest(changepassword.render(changePasswordForm));
            }
            faculty.password = newPassword;
            faculty.update();
            return redirect(routes.Passwords.redirectChanngePassword());
        }
        if (MastersStudent.findByEmail(emailUser) != null) {
            MastersStudent mastersStudent = MastersStudent.findByEmail(emailUser);
            if (!mastersStudent.password.equals(currentPassword)) {
                flash("error", "Mật khẩu hiện tại không đúng!");
                return badRequest(changepassword.render(changePasswordForm));
            }
            mastersStudent.password = newPassword;
            mastersStudent.update();
            return redirect(routes.Passwords.redirectChanngePassword());
        }
        return redirect(routes.Application.admin());
    }

    public static Result redirectChanngePassword() {
        if (session().get("email") == null) {
            return redirect(routes.Application.index());
        }
        return ok(views.html.passwords.changepasswordsuccess.render());
    }

    public static Result forgotPassword() {
        return ok(forgotpassword.render(form(ForgotPassword.class)));
    }

    public static Result resendPassword() {
        Form<ForgotPassword> forgotPasswordForm = form(ForgotPassword.class).bindFromRequest();
        String emailUser = forgotPasswordForm.get().email;
        if (UserAccount.findByEmail(emailUser) != null) {
            flash("error", "Địa chỉ email này không được phép sử dụng!");
            return redirect(routes.Application.index());
        }
        if (Faculty.findByEmail(emailUser) == null &&
                MastersStudent.findByEmail(emailUser) == null) {
            flash("error", "Địa chỉ email này không tồn tại!");
            return redirect(routes.Passwords.forgotPassword());
        }
        String newPassword = (new RandomPassword()).createPassword();
        if (Faculty.findByEmail(emailUser) != null) {
            Faculty faculty = Faculty.findByEmail(emailUser);
            faculty.password = newPassword;
            faculty.update();
            (new MailManager()).sendMail(emailUser, newPassword);
            return redirect(routes.Passwords.forgotPasswordSuccess(emailUser));
        }
        if (MastersStudent.findByEmail(emailUser) != null) {
            MastersStudent mastersStudent = MastersStudent.findByEmail(emailUser);
            mastersStudent.password = newPassword;
            mastersStudent.update();
            (new MailManager()).sendMail(emailUser, newPassword);
            return redirect(routes.Passwords.forgotPasswordSuccess(emailUser));
        }
        return redirect(routes.Application.admin());
    }

    public static Result forgotPasswordSuccess(String email) {
        return ok(forgotpasswordsuccess.render(email));
    }

}