package controllers;

import models.MastersStudent;
import models.UserAccount;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.mastersstudents.details;
import views.html.mastersstudents.detailsuser;

import java.util.List;

@Security.Authenticated(Secured.class)
public class MastersStudents extends Controller {

    private static final Form<MastersStudent> mastersStudentForm = Form.form(MastersStudent.class);

    public static Result newMastersStudent() {
        return ok(details.render(mastersStudentForm));
    }

    public static Result details(MastersStudent mastersStudent) {
        if (mastersStudent == null) {
            return notFound(String.format("Master's Student does not exist!"));
        }
        Form<MastersStudent> filledForm = mastersStudentForm.fill(mastersStudent);
        return ok(details.render(filledForm));
    }

    public static Result detailsReadOnly(MastersStudent mastersStudent) {
        if (mastersStudent == null) {
            return notFound(String.format("Master's Student does not exist!"));
        }
        Form<MastersStudent> filledForm = mastersStudentForm.fill(mastersStudent);
        return ok(detailsuser.render(filledForm));
    }

    public static Result save() {
        Form<MastersStudent> boundForm = mastersStudentForm.bindFromRequest();
        if (boundForm.hasErrors()) {
            flash("error", "Please correct the form below!");
            return badRequest(details.render(boundForm));
        }

        MastersStudent mastersStudent = boundForm.get();


        if (mastersStudent.id == null) {
            MastersStudent student1 = MastersStudent.findByEan(mastersStudent.ean);
            if (student1 != null && mastersStudent.ean.equals(student1.ean)) {
                flash("error", "That ID already exists!");
                return badRequest(details.render(boundForm));
            }
            MastersStudent student2 = MastersStudent.findByEmail(mastersStudent.email);
            if (student2 != null && mastersStudent.email.equals(student2.email)) {
                flash("error", "That Email already exists!");
                return badRequest(details.render(boundForm));
            }
        }

        if (mastersStudent.id != null) {
            List<MastersStudent> mastersStudents = MastersStudent.findAll();
            for (int i = 0; i < mastersStudents.size(); i++) {
                if (mastersStudents.get(i).ean.equals(mastersStudent.ean) &&
                        !mastersStudents.get(i).id.equals(mastersStudent.id)) {
                    flash("error", "That ID already exists!");
                    return badRequest(details.render(boundForm));
                }
                if (mastersStudents.get(i).email.equals(mastersStudent.email) &&
                        !mastersStudents.get(i).id.equals(mastersStudent.id)) {
                    flash("error", "That Email already exists!");
                    return badRequest(details.render(boundForm));
                }
            }
        }
        if (mastersStudent.id == null) {
            mastersStudent.save();
        } else {
            mastersStudent.update();
        }
        flash("success", String.format("Successfully added Master's Student %s!", mastersStudent));
        UserAccount userAccount = UserAccount.findByEmail(session().get("email"));
        if (userAccount.isAdministrator()) {
            return redirect(routes.MastersStudents.list(0, "id", "asc", ""));
        } else {
            if (userAccount.isUser()) {
                return redirect(routes.MastersStudents.detailsReadOnly(MastersStudent.findByEmail(userAccount.email)));
            } else {
                return ok();
            }
        }
    }

    public static Result delete(String ean) {
        final MastersStudent mastersStudent = MastersStudent.findByEan(ean);
        if (mastersStudent == null) {
            return notFound(String.format("Master's Student %s does not exists!", ean));
        }
        mastersStudent.delete();
        return redirect(routes.MastersStudents.list(0, "id", "asc", ""));
    }

    public static Result list(Integer page, String sortBy, String order, String filter) {
        UserAccount userAccount = UserAccount.findByEmail(session().get("email"));
        if (userAccount.isUser()) {
            return redirect(routes.Application.home());
        } else {
            if (userAccount.isAdministrator()) {
                return ok(views.html.listmastersstudents.render(
                        MastersStudent.page(page, 5, sortBy, order, filter), sortBy, order, filter
                ));
            } else {
                return ok();
            }
        }
    }

}