package models;

import com.avaje.ebean.Page;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.mvc.PathBindable;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Faculty extends Model implements PathBindable<Faculty> {
    @Id
    public Long id;

    @Constraints.Required
    public String code;

    @Constraints.Required
    public String name;

    public String degree;

    public String address;

    @Constraints.Required
    public String email;

    public String phoneNumber;

    public static List<String> options() {
        List<String> options = new ArrayList<String>();
        options.add("Thạc Sĩ");
        options.add("Tiến Sĩ");
        options.add("Tiến Sĩ Khoa Học");
        options.add("Phó Giáo Sư");
        options.add("Giáo Sư");
        return options;
    }


    public static Finder<Long, Faculty> find = new Finder<Long, Faculty>(Long.class, Faculty.class);

    public Faculty() {
    }

    public Faculty(String code, String name, String degree, String address, String email, String phoneNumber) {
        this.code = code;
        this.name = name;
        this.degree = degree;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String toString() {
        return String.format("%s - %s", code, name);
    }

    public static Page<Faculty> page(int page, int pageSize, String sortBy, String order, String filter) {
        if (filter.matches("[\\d]*")) {
            return find.where()
                    .ilike("code", "%" + filter + "%")
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

    public static int getTotalRowCount() {
        return find.where().findPagingList(5).getTotalRowCount();
    }

    public static List<Faculty> findAll() {
        return find.all();
    }

    public static Faculty findByCode(String code) {
        return find.where().eq("code", code).findUnique();
    }

    public static Faculty findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    @Override
    public Faculty bind(String key, String value) {
        return findByCode(value);
    }

    @Override
    public String unbind(String s) {
        return code;
    }

    @Override
    public String javascriptUnbind() {
        return code;
    }
}