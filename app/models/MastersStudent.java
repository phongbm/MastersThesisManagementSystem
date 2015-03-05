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

    public MastersStudent(String ean, String name, String address, Date birthday) {
        this.ean = ean;
        this.name = name;
        this.address = address;
        this.birthday = birthday;
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

    public static Page<MastersStudent> find(int page) {
        return find.where()
                .orderBy("id asc")
                .findPagingList(10)
                .setFetchAhead(false)
                .getPage(page);
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