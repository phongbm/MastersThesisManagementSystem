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
    public Product product;

    public Long quantity;

    public String toString() {
        return String.format("StockItem %d - %d x product %s",
                id, quantity, product == null ? null : product.id);
    }

    public static StockItem findById(Long id) {
        return StockItem.find.byId(id);
    }

    public static Finder<Long, StockItem> find = new Finder<Long, StockItem>(
            Long.class, StockItem.class
    );
}