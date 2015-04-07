package controllers;

import com.avaje.ebean.Ebean;
import models.Faculty;
import models.UserAccount;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.facultys.details;

import java.util.List;

public class Facultys extends Controller {

    private static final Form<Faculty> facultyForm = Form.form(Faculty.class);

    public static Result newFaculty() {
        if (session().get("email") == null) {
            return redirect(routes.Application.index());
        }
        if (!UserAccount.findByEmail(session().get("email")).isAdministrator()) {
            return redirect(routes.Application.index());
        }
        return ok(details.render(facultyForm));
    }

    public static Result delete(String code) {
        final Faculty faculty = Faculty.findByCode(code);
        if (faculty == null) {
            return notFound(String.format("Faculty %s does not exists!", code));
        }
        faculty.delete();
        return redirect(routes.Facultys.list(0, "id", "asc", ""));
    }

    public static Result details(Faculty faculty) {
        if (faculty == null) {
            return notFound(String.format("Faculty does not exist!"));
        }
        Form<Faculty> filledForm = facultyForm.fill(faculty);
        return ok(details.render(filledForm));
    }

    public static Result save() {
        Form<Faculty> boundForm = facultyForm.bindFromRequest();
        if (boundForm.hasErrors()) {
            flash("error", "Please correct the form below!");
            return badRequest(details.render(boundForm));
        }
        Faculty faculty = boundForm.get();
        if (faculty.id == null) {
            Faculty faculty1 = Faculty.findByCode(faculty.code);
            if (faculty1 != null && faculty.code.equals(faculty1.code)) {
                flash("error", "That ID already exists!");
                return badRequest(details.render(boundForm));
            }
            Faculty faculty2 = Faculty.findByEmail(faculty.email);
            if (faculty2 != null && faculty.email.equals(faculty.email)) {
                flash("error", "That Email already exists!");
                return badRequest(details.render(boundForm));
            }
        }
        if (faculty.id != null) {
            List<Faculty> facultyList = Faculty.findAll();
            for (int i = 0; i < facultyList.size(); i++) {
                if (facultyList.get(i).code.equals(faculty.code) &&
                        !facultyList.get(i).id.equals(faculty.id)) {
                    flash("error", "That ID already exists!");
                    return badRequest(details.render(boundForm));
                }
                if (facultyList.get(i).email.equals(faculty.email) &&
                        !facultyList.get(i).id.equals(faculty.id)) {
                    flash("error", "That Email already exists!");
                    return badRequest(details.render(boundForm));
                }
            }
        }
        if (faculty.id == null) {
            faculty.save();
        } else {
            faculty.update();
        }
        flash("success", String.format("Successfully added Master's Student %s!", faculty));
        return redirect(routes.Facultys.list(0, "id", "asc", ""));
    }

    public static Result list(Integer page, String sortBy, String order, String filter) {
        return ok(views.html.facultys.listfacultys.render(
                Faculty.page(page, 5, sortBy, order, filter), sortBy, order, filter
        ));
    }

    public static Result info(Faculty faculty) {
        return ok(views.html.facultys.info.render(faculty));
    }

}