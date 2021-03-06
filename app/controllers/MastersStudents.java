package controllers;

import models.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.mastersstudents.details;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static play.data.Form.form;

@Security.Authenticated(Secured.class)
public class MastersStudents extends Controller {

    private static final Form<MastersStudent> mastersStudentForm = Form.form(MastersStudent.class);

    public static Result newMastersStudent() {
        if (UserAccount.findByEmail(session().get("email")) == null) {
            return redirect(routes.Application.home());
        }
        return ok(details.render(mastersStudentForm));
    }

    public static Result delete(String code) {
        final MastersStudent mastersStudent = MastersStudent.findByCode(code);
        if (mastersStudent == null) {
            return notFound(String.format("Học viên %s không tồn tại!", code));
        }
        MastersThesis mastersThesis = null;
        Document document = null;
        if (mastersStudent.mastersThesis != null) {
            mastersThesis = MastersThesis.findByCode(mastersStudent.mastersThesis.code);
        }
        if (mastersStudent.document != null) {
            document = Document.find.byId(mastersStudent.document.id);
        }
        mastersStudent.delete();
        if (mastersThesis != null) {
            mastersThesis.delete();
        }
        if (document != null) {
            document.delete();
        }
        return redirect(routes.MastersStudents.list(0, "id", "asc", ""));
    }

    public static Result details(MastersStudent mastersStudent) {
        if (mastersStudent == null) {
            return notFound(String.format("Học viên không tồn tại!"));
        }
        if (Faculty.findByEmail(session().get("email")) != null) {
            return redirect(routes.Faculties.info(Faculty.findByEmail(session().get("email"))));
        }
        if (MastersStudent.findByEmail(session().get("email")) != null &&
                !MastersStudent.findByEmail(session().get("email")).code.equals(mastersStudent.code)) {
            return redirect(routes.MastersStudents.info(MastersStudent.findByEmail(session().get("email"))));
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
        if (UserAccount.findByEmail(mastersStudent.email) != null) {
            flash("error", "Địa chỉ email này không được phép sử dụng");
            return badRequest(details.render(boundForm));
        }
        if (Faculty.findByEmail(mastersStudent.email) != null) {
            flash("error", "Địa chỉ email trùng với tài khoản giảng viên!");
            return badRequest(details.render(boundForm));
        }
        if (mastersStudent.id == null) {
            if (MastersStudent.findByCode(mastersStudent.code) != null) {
                flash("error", "Tài khoản với mã số này đã tồn tại!");
                return badRequest(details.render(boundForm));
            }
            if (MastersStudent.findByEmail(mastersStudent.email) != null) {
                flash("error", "Tài khoản với địa chỉ email này đã tồn tại!");
                return badRequest(details.render(boundForm));
            }
        } else {
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
            mastersStudent.password = (new RandomPassword()).createPassword();
            mastersStudent.save();
            (new MailManager()).sendMail(mastersStudent.email, mastersStudent.password);
        } else {
            if (MastersStudent.findByEmail(session().get("email")) != null) {
                mastersStudent.faculty = new String(MastersStudent.findByEmail(mastersStudent.email).faculty);
            }
            mastersStudent.update();
        }
        flash("success", String.format("Thêm tài khoản học viên thành công %s!", mastersStudent));
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

    /* ------------------------ */
    public static class UploadAvatarForm {

        public Http.MultipartFormData.FilePart avatar;

        public String validate() {
            Http.MultipartFormData data = request().body().asMultipartFormData();
            avatar = data.getFile("avatar");
            if (avatar == null) {
                return "Tệp tin tài liệu bị thiếu!";
            }
            return null;
        }
    }

    public static Result upload() {
        return ok(views.html.mastersstudents.uploadavatar.render(form(UploadAvatarForm.class)));
    }

    public static Result uploadAvatar() {
        Form<UploadAvatarForm> form = form(UploadAvatarForm.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest(views.html.mastersstudents.uploadavatar.render(form));
        }
        MastersStudent mastersStudent = MastersStudent.findByEmail(session().get("email"));
        mastersStudent.avatar = new byte[(int) form.get().avatar.getFile().length()];
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(form.get().avatar.getFile()));
            inputStream.read(mastersStudent.avatar);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        mastersStudent.update();
        flash("success", "Thay đổi hình ảnh đại diện thành công!");
        return redirect(routes.Application.home());
    }


    public static Result getAvatar(long id) {
        MastersStudent mastersStudent = MastersStudent.find.byId(id);
        if (mastersStudent != null) {
            return ok(mastersStudent.avatar).as("avatar");
        } else {
            flash("error", "Hình đại diện không tồn tại!");
            return redirect(routes.Application.home());
        }
    }

}