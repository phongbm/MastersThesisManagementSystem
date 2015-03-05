package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Warehouse extends Model {
    @Id
    public Long id;

    public String name;

    @OneToOne
    public Address address;

    @OneToMany(mappedBy = "warehouse")
    public List<StockItem> stock = new ArrayList();

    public String toString() {
        return name;
    }

    public static Warehouse findById(Long id) {
        return Warehouse.find.byId(id);
    }

    public static Finder<Long, Warehouse> find = new Finder<Long, Warehouse>(
            Long.class, Warehouse.class
    );
}