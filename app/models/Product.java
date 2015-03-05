package models;

import com.avaje.ebean.Page;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.mvc.PathBindable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

@Entity
public class Product extends Model implements PathBindable<Product> {
    @Id
    public Long id;

    @Constraints.Required
    public String ean;

    @Constraints.Required
    public String name;

    public String description;

    public Date date;

    @OneToMany(mappedBy = "product")
    public List<StockItem> stockItems;

    public byte[] picture;

    @ManyToMany
    public List<Tag> tags;

    public static Finder<Long, Product> find = new Finder<Long, Product>(
            Long.class, Product.class
    );

    public Product() {
    }

    public Product(String ean, String name, String description) {
        this.ean = ean;
        this.name = name;
        this.description = description;
    }

    public String toString() {
        return String.format("%s - %s", ean, name);
    }

    public static List<Product> findAll() {
        return find.all();
    }

    public void delete() {
        for (Tag tag : tags) {
            tag.products.remove(this);
            tag.save();
        }
        super.delete();
    }

    public static Page<Product> find(int page) {
        return find.where()
                .orderBy("id asc")
                .findPagingList(10)
                .setFetchAhead(false)
                .getPage(page);
    }

    public static Product findByEan(String ean) {
        return find.where().eq("ean", ean).findUnique();
    }

    public static List<Product> findByName(String term) {
        return find.where().eq("name", term).findList();
    }

    @Override
    public Product bind(String key, String value) {
        return findByEan(value);
    }

    @Override
    public String unbind(String key) {
        return ean;
    }

    @Override
    public String javascriptUnbind() {
        return ean;
    }
}