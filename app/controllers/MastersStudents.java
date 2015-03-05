package controllers;

import com.avaje.ebean.Page;
import models.MastersStudent;
import models.StockItem;
import models.Tag;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.mastersstudents.details;

import java.util.ArrayList;
import java.util.List;

@Security.Authenticated(Secured.class)
public class MastersStudents extends Controller {

    private static final Form<MastersStudent> mastersStudentForm = Form.form(MastersStudent.class);

    public static Result newMastersStudent() {
        return ok(details.render(mastersStudentForm));
    }

    public static Result details(MastersStudent mastersStudent) {
        if (mastersStudent == null) {
            return notFound(String.format("Masters Student does not exist."));
        }
        Form<MastersStudent> filledForm = mastersStudentForm.fill(mastersStudent);
        return ok(details.render(filledForm));
    }

    public static Result save() {
        Form<MastersStudent> boundForm = mastersStudentForm.bindFromRequest();
        if (boundForm.hasErrors()) {
            flash("error", "Please correct the form below.");
            return badRequest(details.render(boundForm));
        }
        MastersStudent mastersStudent = boundForm.get();
        List<Tag> tags = new ArrayList<Tag>();
        for (Tag tag : mastersStudent.tags) {
            if (tag.id != null) {
                tags.add(Tag.findById(tag.id));
            }
        }
        mastersStudent.tags = tags;

        if (mastersStudent.id == null) {
            mastersStudent.save();
        } else {
            mastersStudent.update();
        }

        StockItem stockItem = new StockItem();
        stockItem.mastersStudent = mastersStudent;
        stockItem.quantity = 0L;
        stockItem.save();

        flash("success", String.format("Successfully added product %s", mastersStudent));
        return redirect(routes.MastersStudents.list(0));
    }

    public static Result delete(String ean) {
        final MastersStudent mastersStudent = MastersStudent.findByEan(ean);
        if (mastersStudent == null) {
            return notFound(String.format("Product %s does not exists.", ean));
        }
        for (StockItem stockItem : mastersStudent.stockItems) {
            stockItem.delete();
        }
        mastersStudent.delete();
        return redirect(routes.MastersStudents.list(0));
    }

    public static Result list(Integer page) {
        Page<MastersStudent> mastersStudents = MastersStudent.find(page);
        return ok(views.html.catalog.render(mastersStudents));
    }

}