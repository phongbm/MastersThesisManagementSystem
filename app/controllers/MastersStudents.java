package controllers;

import models.MastersStudent;
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
        return ok(details.render(mastersStudentForm));
    }

    public static Result details(MastersStudent mastersStudent) {
        if (mastersStudent == null) {
            return notFound(String.format("Học viên không tồn tại!"));
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
        UserAccount userAccount = UserAccount.findByEmail(session().get("email"));
        return redirect(routes.MastersStudents.list(0, "id", "asc", ""));
    }

    public static Result delete(String code) {
        final MastersStudent mastersStudent = MastersStudent.findByCode(code);
        if (mastersStudent == null) {
            return notFound(String.format("Học viên %s không tồn tại!", code));
        }
        mastersStudent.delete();
        return redirect(routes.MastersStudents.list(0, "id", "asc", ""));
    }

    public static Result list(Integer page, String sortBy, String order, String filter) {
        return ok(views.html.mastersstudents.listmastersstudents.render(
                MastersStudent.page(page, 5, sortBy, order, filter), sortBy, order, filter
        ));
    }

    public static Result info(MastersStudent mastersStudent){
        return ok(views.html.mastersstudents.info.render(mastersStudent));
    }

}