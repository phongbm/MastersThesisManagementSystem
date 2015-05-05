package models;

import com.avaje.ebean.Page;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import java.io.*;

@Entity
public class Document extends Model {

    @Id
    public long id;

    @Constraints.Required
    public String name;

    @Lob
    public byte[] data;

    @OneToOne(mappedBy = "document")
    public MastersStudent mastersStudent;

    public static Finder<Long, Document> find = new Finder<Long, Document>(Long.class, Document.class);

    public Document(){
    }

    public static Page<Document> find(int page) {
        return find.where()
                .orderBy("id asc")
                .findPagingList(5)
                .setFetchAhead(false)
                .getPage(page);
    }

    public static int getTotalRowCount() {
        return find.where().findPagingList(5).getTotalRowCount();
    }

}