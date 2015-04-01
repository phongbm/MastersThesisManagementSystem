package models;

import com.avaje.ebean.Page;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.mvc.PathBindable;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Faculty extends Model implements PathBindable<Faculty> {
    @Id
    public long id;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String degree;

    @Constraints.Required
    public String address;

    @Constraints.Required
    public String email;

    @Constraints.Required
    public String phoneNumber;

    public static Map<String, String> options() {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("1", "Master of Science");
        options.put("2", "Doctor of Philosophy");
        options.put("3", "Doctor of Science");
        options.put("3", "Asscociate Professor");
        options.put("4", "Professor");
        return options;
    }

    public static Finder<Long, Faculty> find = new Finder<Long, Faculty>(Long.class, Faculty.class);

    public Faculty() {
    }

    public Faculty(String name, String degree, String address, String email, String phoneNumber) {
        this.name = name;
        this.degree = degree;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public static Page<Faculty> page(int page, int pageSize, String sortBy, String order, String filter) {
        if (filter.matches("[\\d]*")) {
            return find.where()
                    .ilike("ean", "%" + filter + "%")
                    .orderBy(sortBy + " " + order)
                    .findPagingList(pageSize)
                    .setFetchAhead(false)
                    .getPage(page);
        } else {
            if (filter.matches("[\\D]*")) {
                return find.where()
                        .ilike("name", "%" + filter + "%")
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
            } else {
                return find.where()
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
            }
        }
    }

    public static Faculty findByName(String name) {
        return find.where().eq("name", name).findUnique();
    }

    @Override
    public Faculty bind(String key, String value) {
        return findByName(value);
    }

    @Override
    public String unbind(String s) {
        return name;
    }

    @Override
    public String javascriptUnbind() {
        return name;
    }
}