package controllers;

import models.Faculty;
import models.MastersStudent;
import models.MastersThesis;
import models.UserAccount;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.mastersstudents.details;

import java.util.List;

@Security.Authenticated(Secured.class)
public class MastersStudents extends Controller {

    private static final Form<MastersStudent> mastersStudentForm = Form.form(MastersStudent.class);

    public static Result newMastersStudent() {
        if (session().get("email") == null) {
            return redirect(routes.Application.index());
        }
        if (!UserAccount.findByEmail(session().get("email")).isAdministrator()) {
            return redirect(routes.Application.home());
        }
        return ok(details.render(mastersStudentForm));
    }

    public static Result details(MastersStudent mastersStudent) {
        if (mastersStudent == null) {
            return notFound(String.format("Học viên không tồn tại!"));
        }
        if(UserAccount.findByEmail(session().get("email")).isFaculty()){
            return redirect(routes.Application.home());
        }
        if (UserAccount.findByEmail(session().get("email")).isMastersStudent() &&
                !MastersStudent.findByEmail(session().get("email")).code.equals(mastersStudent.code)) {
            return redirect(routes.Application.home());
        }
        Form<MastersStudent> filledForm = mastersStudentForm.fill(mastersStudent);
        return ok(details.render(filledForm));
    }

    public static Result save() {
        Form<MastersStudent> boundForm = mastersStudentForm.bindFromRequest();
        if (boundForm.hasErrors()) {
            flash("error", "Vui lòng hoàn thành đúng mẫu!");
            return badRequest(details.render(boundForm));
        }
        MastersStudent mastersStudent = boundForm.get();
        mastersStudent.code = mastersStudent.code.toUpperCase();
        List<Faculty> faculties = Faculty.findAll();
        for (int i = 0; i < faculties.size(); i++) {
            if (mastersStudent.email.equals(faculties.get(i).email)) {
                flash("error", "Địa chỉ email trùng với tài khoản giảng viên!");
                return badRequest(details.render(boundForm));
            }
        }
        if (mastersStudent.id == null) {
            MastersStudent student1 = MastersStudent.findByCode(mastersStudent.code);
            if (student1 != null && mastersStudent.code.equals(student1.code)) {
                flash("error", "Tài khoản với mã số này đã tồn tại!");
                return badRequest(details.render(boundForm));
            }
            MastersStudent student2 = MastersStudent.findByEmail(mastersStudent.email);
            if (student2 != null && mastersStudent.email.equals(student2.email)) {
                flash("error", "Tài khoản với địa chỉ email này đã tồn tại!");
                return badRequest(details.render(boundForm));
            }
        }
        if (mastersStudent.id != null) {
            List<MastersStudent> mastersStudents = MastersStudent.findAll();
            for (int i = 0; i < mastersStudents.size(); i++) {
                if (mastersStudents.get(i).code.equals(mastersStudent.code) &&
                        !mastersStudents.get(i).id.equals(mastersStudent.id)) {
                    flash("error", "Tài khoản với mã số này đã tồn tại!");
                    return badRequest(details.render(boundForm));
                }
                if (mastersStudents.get(i).email.equals(mastersStudent.email) &&
                        !mastersStudents.get(i).id.equals(mastersStudent.id)) {
                    flash("error", "Tài khoản với địa chỉ email này đã tồn tại!");
                    return badRequest(details.render(boundForm));
                }
            }
        }
        if (mastersStudent.id == null) {
            mastersStudent.save();
        } else {
            mastersStudent.update();
        }
        flash("success", String.format("Thêm tài khoản học viên thành công %s!", mastersStudent));
        return redirect(routes.MastersStudents.list(0, "id", "asc", ""));
    }

    public static Result delete(String code) {
        final MastersStudent mastersStudent = MastersStudent.findByCode(code);
        if (mastersStudent == null) {
            return notFound(String.format("Học viên %s không tồn tại!", code));
        }
        if (mastersStudent.mastersThesis != null) {
            MastersThesis mastersThesis = MastersThesis.findByCode(mastersStudent.mastersThesis.code);
            mastersStudent.delete();
            mastersThesis.delete();
            return redirect(routes.MastersStudents.list(0, "id", "asc", ""));
        }
        mastersStudent.delete();
        return redirect(routes.MastersStudents.list(0, "id", "asc", ""));
    }

    public static Result list(Integer page, String sortBy, String order, String filter) {
        return ok(views.html.mastersstudents.list.render(
                MastersStudent.page(page, 5, sortBy, order, filter), sortBy, order, filter
        ));
    }

    public static Result info(MastersStudent mastersStudent) {
        return ok(views.html.mastersstudents.info.render(mastersStudent));
    }

}