package controllers;

import com.avaje.ebean.Page;
import models.Product;
import models.StockItem;
import models.Tag;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.products.details;

import java.util.ArrayList;
import java.util.List;

@Security.Authenticated(Secured.class)
public class Products extends Controller {

    private static final Form<Product> productForm = Form.form(Product.class);

    public static Result newProduct() {
        return ok(details.render(productForm));
    }

    public static Result details(Product product) {
        if (product == null) {
            return notFound(String.format("Product does not exist."));
        }
        Form<Product> filledForm = productForm.fill(product);
        return ok(details.render(filledForm));
    }

    public static Result save() {
        Form<Product> boundForm = productForm.bindFromRequest();
        if (boundForm.hasErrors()) {
            flash("error", "Please correct the form below.");
            return badRequest(details.render(boundForm));
        }
        Product product = boundForm.get();
        List<Tag> tags = new ArrayList<Tag>();
        for (Tag tag : product.tags) {
            if (tag.id != null) {
                tags.add(Tag.findById(tag.id));
            }
        }
        product.tags = tags;

        if (product.id == null) {
            product.save();
        } else {
            product.update();
        }

        StockItem stockItem = new StockItem();
        stockItem.product = product;
        stockItem.quantity = 0L;
        stockItem.save();

        flash("success", String.format("Successfully added product %s", product));
        return redirect(routes.Products.list(0));
    }

    public static Result delete(String ean) {
        final Product product = Product.findByEan(ean);
        if (product == null) {
            return notFound(String.format("Product %s does not exists.", ean));
        }
        for (StockItem stockItem : product.stockItems) {
            stockItem.delete();
        }
        product.delete();
        return redirect(routes.Products.list(0));
    }

    public static Result list(Integer page) {
        Page<Product> products = Product.find(page);
        return ok(views.html.catalog.render(products));
    }

}