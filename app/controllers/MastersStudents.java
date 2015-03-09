package controllers;

import models.MastersStudent;
import models.StockItem;
import models.Tag;
import models.UserAccount;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.mastersstudents.details;
import views.html.mastersstudents.detailsuser;

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
            return notFound(String.format("Master's Student does not exist."));
        }
        Form<MastersStudent> filledForm = mastersStudentForm.fill(mastersStudent);
        return ok(details.render(filledForm));
    }

    public static Result detailsReadOnly(MastersStudent mastersStudent) {
        if (mastersStudent == null) {
            return notFound(String.format("Master's Student does not exist."));
        }
        Form<MastersStudent> filledForm = mastersStudentForm.fill(mastersStudent);
        return ok(detailsuser.render(filledForm));
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

        flash("success", String.format("Successfully added Master's Student %s", mastersStudent));
        /*
        if (!session().get("email").equals("giaovu@vnu.edu.vn")) {
            return redirect(routes.MastersStudents.detailsReadOnly(MastersStudent.findByEmail(session().get("email"))));
        }
        return redirect(routes.MastersStudents.list(0, "id", "asc", ""));
        */
        UserAccount userAccount = UserAccount.findByEmail(session().get("email"));
        if (userAccount.isAdministrator()) {
            return redirect(routes.MastersStudents.list(0, "id", "asc", ""));
        } else {
            if (userAccount.isUser()) {
                return redirect(routes.MastersStudents.detailsReadOnly(MastersStudent.findByEan(userAccount.email)));
            } else {
                return ok();
            }
        }
    }

    public static Result delete(String ean) {
        final MastersStudent mastersStudent = MastersStudent.findByEan(ean);
        if (mastersStudent == null) {
            return notFound(String.format("Master's Student %s does not exists.", ean));
        }
        for (StockItem stockItem : mastersStudent.stockItems) {
            stockItem.delete();
        }
        mastersStudent.delete();
        return redirect(routes.MastersStudents.list(0, "id", "asc", ""));
    }

    /*
    public static Result list(Integer page) {
        Page<MastersStudent> mastersStudents = MastersStudent.find(page);
        return ok(views.html.listmastersstudent.render(mastersStudents));
    }
    */

    public static Result list(Integer page, String sortBy, String order, String filter) {
        UserAccount userAccount = UserAccount.findByEmail(session().get("email"));
        if (userAccount.isUser()) {
            return redirect(routes.Application.index());
        } else {
            if (userAccount.isAdministrator()) {
                return ok(views.html.listmastersstudent.render(
                                MastersStudent.page(page, 5, sortBy, order, filter),
                                sortBy, order, filter
                        )
                );
            } else {
                return ok();
            }
        }
    }

}