package endpoint.backend;

import java.io.Serializable;

/**
 * Created by Lukaskris on 31/07/2017.
 */

public class Type implements Serializable {
    private String id_type;
    private String id_item;
    private String size;
    private int qty;
    private String color;

    public Type() {}

    public Type(String id_type, String id_item, String size, int qty, String color) {
        this.id_type = id_type;
        this.id_item = id_item;
        this.size = size;
        this.qty = qty;
        this.color = color;
    }

    public String getId_type() {
        return id_type;
    }

    public void setId_type(String id_type) {
        this.id_type = id_type;
    }

    public String getId_item() {
        return id_item;
    }

    public void setId_item(String id_item) {
        this.id_item = id_item;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
