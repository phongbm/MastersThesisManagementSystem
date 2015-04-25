package controllers;

import com.avaje.ebean.Page;
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
        if (session().get("email") == null) {
            return redirect(routes.Application.index());
        }
        if (MastersStudent.findByEmail(session().get("email")) == null) {
            return redirect(routes.Application.home());
        }
        return ok(details.render(mastersThesisForm));
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
            MastersThesis thesis1 = MastersThesis.findByCode(mastersThesis.code);
            if (thesis1 != null && mastersThesis.code.equals(thesis1.code)) {
                flash("error", "Luận văn với mã số này đã tồn tại!");
                return badRequest(details.render(boundForm));
            }
        }
        if (mastersThesis.id != null) {
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
            return redirect(routes.MastersThesises.list(0));
        }
        MastersStudent mastersStudent = MastersStudent.findByEmail(session().get("email"));
        mastersStudent.mastersThesis = mastersThesis;
        mastersStudent.update();
        flash("success", String.format("Thêm luận văn thành công %s!", mastersThesis));
        return redirect(routes.MastersThesises.list(0));
    }

    public static Result list(Integer page) {
        Page<MastersThesis> mastersThesisPage = MastersThesis.find(page);
        return ok(list.render(mastersThesisPage));
    }

    public static Result details(MastersThesis mastersThesis) {
        if (mastersThesis == null) {
            return notFound(String.format("Luận văn không tồn tại!"));
        }
        Form<MastersThesis> filledForm = mastersThesisForm.fill(mastersThesis);
        return ok(details.render(filledForm));
    }

    public static Result delete(String code) {
        return ok();
    }

}