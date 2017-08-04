package endpoint.backend;

import java.io.Serializable;
import java.util.List;

public class Item implements Serializable{
    private String name;
    private String price;
    private String desc;
    private String category;
    private List<String> image;
    private List<Type> type;
    private String id;
    private String count;

    public Item() {
    }

    public Item(String name, String price, String desc, String category, List<String> image, List<String> qty, List<String> size, String id, String count) {
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.category = category;
        this.image = image;
//        this.qty = qty;
//        this.size = size;
        this.id = id;
        this.count=count;
    }

    public String getName() {

        return name;
    }

    public List<Type> getType() {
        return type;
    }

    public void setType(List<Type> type) {
        this.type = type;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

//    public List<String> getQty() {
//        return qty;
//    }
//
//    public void setQty(List<String> qty) {
//        this.qty = qty;
//    }
//
//    public List<String> getSize() {
//        return size;
//    }
//
//    public void setSize(List<String> size) {
//        this.size = size;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
