package controllers;

import com.avaje.ebean.Page;
import models.MastersStudent;
import models.MastersThesis;
import models.UserAccount;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.mastersthesiss.details;
import views.html.mastersthesiss.list;

import java.util.List;

@Security.Authenticated(Secured.class)
public class MastersThesiss extends Controller {
    private static final Form<MastersThesis> mastersThesisForm = Form.form(MastersThesis.class);

    public static Result newMastersThesis() {
        if (session().get("email") == null) {
            return redirect(routes.Application.index());
        }
        if (!UserAccount.findByEmail(session().get("email")).isMastersStudent()) {
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
        if (mastersThesis.id == null) {
            mastersThesis.save();
        } else {
            mastersThesis.update();
        }
        if(UserAccount.findByEmail(session().get("email")).isAdministrator()) {
            List<MastersStudent> mastersStudents = MastersStudent.findAll();
            for (MastersStudent mastersStudent : mastersStudents) {
                mastersStudent.update();
            }
            return redirect(routes.MastersThesiss.list(0));
        }
        MastersStudent mastersStudent = MastersStudent.findByEmail(session().get("email"));
        mastersStudent.mastersThesis = mastersThesis;
        mastersStudent.update();
        flash("success", String.format("Thêm luận văn thành công %s!", mastersThesis));
        return redirect(routes.MastersThesiss.list(0));
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