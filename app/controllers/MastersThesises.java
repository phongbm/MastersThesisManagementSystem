package controllers;

import com.avaje.ebean.Page;
import models.Faculty;
import models.MastersStudent;
import models.MastersThesis;
import models.UserAccount;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.mastersthesises.details;
import views.html.mastersthesises.list;

import java.util.List;

@Security.Authenticated(Secured.class)
public class MastersThesises extends Controller {

    private static final Form<MastersThesis> mastersThesisForm = Form.form(MastersThesis.class);

    public static Result newMastersThesis() {
        String emailUser = session().get("email");
        if (Faculty.findByEmail(emailUser) != null) {
            return redirect(routes.Faculties.info(Faculty.findByEmail(emailUser)));
        }
        if (MastersStudent.findByEmail(emailUser).mastersThesis != null) {
            return redirect(routes.MastersStudents.info(MastersStudent.findByEmail(emailUser)));
        }
        return ok(details.render(mastersThesisForm));
    }

    public static Result delete(String code) {
        final MastersThesis mastersThesis = MastersThesis.findByCode(code);
        if (mastersThesis == null) {
            return notFound(String.format("Luận văn %s không tồn tại!", code));
        }
        MastersStudent mastersStudent = mastersThesis.mastersStudent;
        MastersThesis mastersThesis1 = mastersStudent.mastersThesis;
        mastersStudent.mastersThesis = null;
        mastersStudent.update();
        mastersThesis1.delete();
        return redirect(routes.MastersThesises.list(0));
    }

    public static Result details(MastersThesis mastersThesis) {
        if (Faculty.findByEmail(session().get("email")) != null) {
            return redirect(routes.Faculties.info(Faculty.findByEmail(session().get("email"))));
        }
        if (mastersThesis == null) {
            return notFound(String.format("Luận văn không tồn tại!"));
        }
        Form<MastersThesis> filledForm = mastersThesisForm.fill(mastersThesis);
        return ok(details.render(filledForm));
    }

    public static Result save() {
        Form<MastersThesis> boundForm = mastersThesisForm.bindFromRequest();
        if (boundForm.hasErrors()) {
            flash("error", "Vui lòng hoàn thành đúng mẫu!");
            return badRequest(details.render(boundForm));
        }
        MastersThesis mastersThesis = boundForm.get();
        mastersThesis.code = mastersThesis.code.toUpperCase();
        if (mastersThesis.id == null) {
            if (MastersThesis.findByCode(mastersThesis.code) != null) {
                flash("error", "Luận văn với mã số này đã tồn tại!");
                return badRequest(details.render(boundForm));
            }
        } else {
            List<MastersThesis> mastersThesises = MastersThesis.findAll();
            for (int i = 0; i < mastersThesises.size(); i++) {
                if (mastersThesises.get(i).code.equals(mastersThesis.code) &&
                        !mastersThesises.get(i).id.equals(mastersThesis.id)) {
                    flash("error", "Luận văn với mã số này đã tồn tại!");
                    return badRequest(details.render(boundForm));
                }
            }
        }
        if (mastersThesis.id == null) {
            mastersThesis.save();
        } else {
            mastersThesis.update();
        }
        if (UserAccount.findByEmail(session().get("email")) != null) {
            List<MastersStudent> mastersStudents = MastersStudent.findAll();
            for (MastersStudent mastersStudent : mastersStudents) {
                mastersStudent.update();
            }
        } else {
            MastersStudent mastersStudent = MastersStudent.findByEmail(session().get("email"));
            mastersStudent.mastersThesis = mastersThesis;
            mastersStudent.update();
            flash("success", String.format("Thêm luận văn thành công %s!", mastersThesis));
        }
        return redirect(routes.MastersThesises.list(0));
    }

    public static Result list(Integer page) {
        Page<MastersThesis> mastersThesisPage = MastersThesis.find(page);
        return ok(list.render(mastersThesisPage));
    }

}