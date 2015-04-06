package models;

import com.avaje.ebean.Page;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.mvc.PathBindable;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class MastersStudent extends Model implements PathBindable<MastersStudent> {
    @Id
    public Long id;

    @Constraints.Required
    public String code;

    @Constraints.Required
    public String name;

    @Constraints.Required
    @Formats.DateTime(pattern = "yyyy-MM-dd")
    public Date birthday;

    @Constraints.Required
    public String address;

    @Constraints.Required
    public String phoneNumber;

    @Constraints.Required
    public String faculty;

    @Constraints.Required
    public String course;

    @Constraints.Required
    public String email;

    public byte[] picture;

    public static Finder<Long, MastersStudent> find = new Finder<Long, MastersStudent>(
            Long.class, MastersStudent.class
    );

    public static List<String> options(){
        List<String> options = new ArrayList<String>();
        options.add("Infomation Technology");
        options.add("Electronics & Telecommunications");
        options.add("Engineering Physics & Nanotechnology");
        options.add("Engineering Mechanics & Automation");
        return options;
    }

    public MastersStudent() {
    }

    public MastersStudent(String code, String name, String address, String phoneNumber, String faculty,
                          String course, String email, Date birthday) {
        this.code = code;
        this.name = name;
        this.birthday = birthday;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.faculty = faculty;
        this.course = course;
        this.email = email;
    }

    public String toString() {
        return String.format("%s - %s", code, name);
    }

    public static List<MastersStudent> findAll() {
        return find.all();
    }

    public void delete() {
        super.delete();
    }

    public static Page<MastersStudent> page(int page, int pageSize, String sortBy, String order, String filter) {
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

    public static Page<MastersStudent> find(int page) {
        return find.where()
                .orderBy("id asc")
                .findPagingList(5)
                .setFetchAhead(false)
                .getPage(page);
    }

    public static int getTotalRowCount(){
        return find.where().findPagingList(5).getTotalRowCount();
    }

    public static MastersStudent findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    public static MastersStudent findByEan(String ean) {
        return find.where().eq("code", ean).findUnique();
    }

    public static List<MastersStudent> findByName(String name) {
        return find.where().eq("name", name).findList();
    }

    @Override
    public MastersStudent bind(String key, String value) {
        return findByEan(value);
    }

    @Override
    public String unbind(String key) {
        return code;
    }

    @Override
    public String javascriptUnbind() {
        return code;
    }

}