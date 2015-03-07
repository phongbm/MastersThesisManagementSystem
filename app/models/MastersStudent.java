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
public class MastersStudent extends Model implements PathBindable<MastersStudent> {

    @Id
    public Long id;

    @Constraints.Required
    public String ean;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String address;

    @Constraints.Required
    public String phonenumber;

    @Constraints.Required
    public String faculty;

    @Constraints.Required
    public String course;

    @Constraints.Required
    public String email;

    @Constraints.Required
    public Date birthday;

    @OneToMany(mappedBy = "mastersStudent")
    public List<StockItem> stockItems;

    public byte[] picture;

    @ManyToMany
    public List<Tag> tags;

    public static Finder<Long, MastersStudent> find = new Finder<Long, MastersStudent>(
            Long.class, MastersStudent.class
    );

    public MastersStudent() {
    }

    public MastersStudent(String ean, String name, String address, String phonenumber, String faculty,
                          String course, String email, Date birthday) {
        this.ean = ean;
        this.name = name;
        this.birthday = birthday;
        this.address = address;
        this.phonenumber = phonenumber;
        this.faculty = faculty;
        this.course = course;
        this.email = email;
    }

    public String toString() {
        return String.format("%s - %s", ean, name);
    }

    public static List<MastersStudent> findAll() {
        return find.all();
    }

    public void delete() {
        for (Tag tag : tags) {
            tag.mastersStudents.remove(this);
            tag.save();
        }
        super.delete();
    }

    public static Page<MastersStudent> page(int page, int pageSize, String sortBy, String order, String filter) {
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

    public static Page<MastersStudent> find(int page) {
        return find.where()
                .orderBy("id asc")
                .findPagingList(5)
                .setFetchAhead(false)
                .getPage(page);
    }

    public static MastersStudent findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    public static MastersStudent findByEan(String ean) {
        return find.where().eq("ean", ean).findUnique();
    }

    public static List<MastersStudent> findByName(String term) {
        return find.where().eq("name", term).findList();
    }

    @Override
    public MastersStudent bind(String key, String value) {
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