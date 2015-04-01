package models;

import com.avaje.ebean.Page;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Faculty extends Model {

    @Id
    public long id;

    @Constraints.Required
    public String name;

    public String address;
    public String email;
    public String phoneNumber;

    public static Finder<Long, Faculty> find = new Finder<Long, Faculty>(Long.class, Faculty.class);

    public Faculty() {
    }

    public Faculty(String name) {
        this.name = name;
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

}