package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class StockItem extends Model {
    @Id
    public Long id;

    @ManyToOne
    public Warehouse warehouse;

    @ManyToOne
    public MastersStudent mastersStudent;

    public Long quantity;

    public String toString() {
        return String.format("StockItem %d - %d x Master's Student %s",
                id, quantity, mastersStudent == null ? null : mastersStudent.id);
    }

    public static StockItem findById(Long id) {
        return StockItem.find.byId(id);
    }

    public static Finder<Long, StockItem> find = new Finder<Long, StockItem>(
            Long.class, StockItem.class
    );
}