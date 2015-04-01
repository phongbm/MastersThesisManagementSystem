package controllers;

import models.Faculty;
import models.MastersStudent;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.facultys.details;

public class Facultys extends Controller {

    private static final Form<Faculty> facultyForm = Form.form(Faculty.class);

    public static Result newFaculty() {
        return ok(details.render(facultyForm));
    }

    public static Result save() {
        Form<Faculty> boundForm = facultyForm.bindFromRequest();
        if (boundForm.hasErrors()) {
            flash("error", "Please correct the form below!");
            return badRequest(details.render(boundForm));
        }
        Faculty faculty = boundForm.get();
        if (faculty.id > 0) {
            faculty.save();
        } else {
            faculty.update();
        }
        flash("success", String.format("Successfully added Master's Student %s!", faculty));
        return redirect(routes.Facultys.list(0, "id", "asc", ""));
    }

    public static Result list(Integer page, String sortBy, String order, String filter) {
        return ok(views.html.listfacultys.render(
                Faculty.page(page, 5, sortBy, order, filter), sortBy, order, filter
        ));
    }

}