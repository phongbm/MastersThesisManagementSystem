package controllers;

import models.Faculty;
import play.mvc.Result;
import play.mvc.Controller;

public class Facultys extends Controller {
    public static Result list(Integer page, String sortBy, String order, String filter) {
        return ok(views.html.listfacultys.render(
                Faculty.page(page, 5, sortBy, order, filter), sortBy, order, filter
        ));

    }
}