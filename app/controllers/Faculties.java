package controllers;

import models.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.faculties.details;

import java.util.List;

@Security.Authenticated(Secured.class)
public class Faculties extends Controller {

    private static final Form<Faculty> facultyForm = Form.form(Faculty.class);

    public static Result newFaculty() {
        if (UserAccount.findByEmail(session().get("email")) == null) {
            return redirect(routes.Application.home());
        }
        return ok(details.render(facultyForm));
    }

    public static Result delete(String code) {
        final Faculty faculty = Faculty.findByCode(code);
        if (faculty == null) {
            return notFound(String.format("Giảng viên %s không tồn tại!", code));
        }
        faculty.delete();
        return redirect(routes.Faculties.list(0, "id", "asc", ""));
    }

    public static Result details(Faculty faculty) {
        if (faculty == null) {
            return notFound(String.format("Giảng viên không tồn tại!"));
        }
        if (MastersStudent.findByEmail(session().get("email")) != null) {
            return redirect(routes.MastersStudents.info(MastersStudent.findByEmail(session().get("email"))));
        }
        if (Faculty.findByEmail(session().get("email")) != null &&
                !Faculty.findByEmail(session().get("email")).code.equals(faculty.code)) {
            return redirect(routes.Faculties.info(Faculty.findByEmail(session().get("email"))));
        }
        Form<Faculty> filledForm = facultyForm.fill(faculty);
        return ok(details.render(filledForm));
    }

    public static Result save() {
        Form<Faculty> boundForm = facultyForm.bindFromRequest();
        if (boundForm.hasErrors()) {
            flash("error", "Vui lòng hoàn thành đúng mẫu!");
            return badRequest(details.render(boundForm));
        }
        Faculty faculty = boundForm.get();
        faculty.code = faculty.code.toUpperCase();
        if (UserAccount.findByEmail(faculty.email) != null) {
            flash("error", "Địa chỉ email này không được phép sử dụng");
            return badRequest(details.render(boundForm));
        }
        if (MastersStudent.findByEmail(faculty.email) != null) {
            flash("error", "Địa chỉ email trùng với tài khoản học viên!");
            return badRequest(details.render(boundForm));
        }
        if (faculty.id == null) {
            if (Faculty.findByCode(faculty.code) != null) {
                flash("error", "Tài khoản với mã số này đã tồn tại!");
                return badRequest(details.render(boundForm));
            }
            if (Faculty.findByEmail(faculty.email) != null) {
                flash("error", "Tài khoản với địa chỉ email này đã tồn tại!");
                return badRequest(details.render(boundForm));
            }
        } else {
            List<Faculty> facultyList = Faculty.findAll();
            for (int i = 0; i < facultyList.size(); i++) {
                if (facultyList.get(i).code.equals(faculty.code) &&
                        !facultyList.get(i).id.equals(faculty.id)) {
                    flash("error", "Tài khoản với mã số này đã tồn tại!");
                    return badRequest(details.render(boundForm));
                }
                if (facultyList.get(i).email.equals(faculty.email) &&
                        !facultyList.get(i).id.equals(faculty.id)) {
                    flash("error", "Tài khoản với địa chỉ email này đã tồn tại!");
                    return badRequest(details.render(boundForm));
                }
            }
        }
        if (faculty.id == null) {
            faculty.password = (new RandomPassword()).createPassword();
            faculty.save();
            (new MailManager()).sendMail(faculty.email, faculty.password);
        } else {
            faculty.update();
        }
        flash("success", String.format("Thêm tài khoản giảng viên thành công %s!", faculty));
        return redirect(routes.Faculties.list(0, "id", "asc", ""));
    }

    public static Result list(Integer page, String sortBy, String order, String filter) {
        return ok(views.html.faculties.list.render(
                Faculty.page(page, 5, sortBy, order, filter), sortBy, order, filter
        ));
    }

    public static Result info(Faculty faculty) {
        return ok(views.html.faculties.info.render(faculty));
    }

}