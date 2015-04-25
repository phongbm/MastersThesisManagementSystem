package controllers;

import models.Faculty;
import models.MastersStudent;
import models.UserAccount;
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
        if (session().get("email") == null) {
            return redirect(routes.Application.index());
        }
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
            return redirect(routes.Application.home());
        }
        if (Faculty.findByEmail(session().get("email")) != null &&
                !Faculty.findByEmail(session().get("email")).code.equals(faculty.code)) {
            return redirect(routes.Application.home());
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
        faculty.password = boundForm.field("password").value().split("@")[0];
        List<MastersStudent> mastersStudents = MastersStudent.findAll();
        for (int i = 0; i < mastersStudents.size(); i++) {
            if (faculty.email.equals(mastersStudents.get(i).email)) {
                flash("error", "Địa chỉ email trùng với tài khoản học viên!");
                return badRequest(details.render(boundForm));
            }
        }
        if (faculty.id == null) {
            Faculty faculty1 = Faculty.findByCode(faculty.code);
            if (faculty1 != null && faculty.code.equals(faculty1.code)) {
                flash("error", "Tài khoản với mã số này đã tồn tại!");
                return badRequest(details.render(boundForm));
            }
            Faculty faculty2 = Faculty.findByEmail(faculty.email);
            if (faculty2 != null && faculty.email.equals(faculty.email)) {
                flash("error", "Tài khoản với địa chỉ email này đã tồn tại!");
                return badRequest(details.render(boundForm));
            }
        }
        if (faculty.id != null) {
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
            faculty.save();
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