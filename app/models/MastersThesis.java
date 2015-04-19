package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.mvc.PathBindable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class MastersThesis extends Model implements PathBindable<MastersThesis> {
    @Id
    public Long id;

    @Constraints.Required
    public String code;

    @Constraints.Required
    public String name;

    public String description;

    @OneToOne(mappedBy = "mastersThesis")
    public MastersStudent mastersStudent;

    public static Finder<Long, MastersThesis> find = new Finder<Long, MastersThesis>(
            Long.class, MastersThesis.class
    );

    public MastersThesis() {
    }

    public MastersThesis(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static MastersThesis findByCode(String code) {
        return find.where().eq("code", code).findUnique();
    }

    @Override
    public MastersThesis bind(String key, String value) {
        return findByCode(value);
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