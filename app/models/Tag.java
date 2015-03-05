package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Tag extends Model {
    @Id
    public Long id;
    @Constraints.Required
    public String name;
    @ManyToMany(mappedBy = "tags")
    public List<MastersStudent> mastersStudents;

    public Tag() {
        // Left empty
    }

    public Tag(Long id, String name, Collection<MastersStudent> mastersStudents) {
        this.id = id;
        this.name = name;
        this.mastersStudents = new LinkedList<MastersStudent>(mastersStudents);
        for (MastersStudent mastersStudent : mastersStudents) {
            mastersStudent.tags.add(this);
        }
    }

    public static Tag findById(Long id) {
        return Tag.find.byId(id);
    }

    public static Finder<Long, Tag> find = new Finder<Long, Tag>(
            Long.class, Tag.class
    );

}