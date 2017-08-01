package endpoint.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "itemApi",
        version = "v1",
        resource = "item",
        namespace = @ApiNamespace(
                ownerDomain = "backend.endpoint",
                ownerName = "backend.endpoint",
                packagePath = ""
        )
)
public class ItemEndpoint {

    private static final Logger logger = Logger.getLogger(ItemEndpoint.class.getName());

    /**
     * This method gets the <code>Item</code> object associated with the specified <code>id</code>.
     *
     * @param id The id of the object to be returned.
     * @return The <code>Item</code> associated with <code>id</code>.
     */
    @ApiMethod(name = "getItem")
    public Item getItem(@Named("id") String id) {
        Item item = null;
        try {
            Connection con = CloudSqlServices.getInstance().getConnection();
            String query = "select * from item where id_item=?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            ResultSet resultSet = null;
            preparedStatement.setString(1,id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                query="select * from type where id_item = ? ";
                String name = resultSet.getString("name");
                String desc = resultSet.getString("desc");
                String price = resultSet.getString("price");
                item = new Item();
                item.setName(name);
                item.setDesc(desc);
                item.setPrice(price);
                PreparedStatement preparedStatement1 = con.prepareStatement(query);
                ResultSet resultSet1 = null;
                preparedStatement1.setString(1,id);
                resultSet1 = preparedStatement1.executeQuery();

                List<Type> typeList = new ArrayList<>();
                while(resultSet1.next()){
                    Type type = new Type();
                    type.setId_item(id);
                    type.setId_type(resultSet1.getString("id_type"));
                    type.setSize(resultSet1.getString("size"));
                    type.setQty(resultSet1.getInt("qty"));
                    type.setColor(resultSet1.getString("color"));
                    typeList.add(type);
                }
                item.setType(typeList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.info("Error Calling getItem method " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Error Calling getItem method " + e.getMessage());
        }
        // TODO: Implement this function
        logger.info("Calling getItem method" + item);
        return item;
    }

    @ApiMethod(name = "getAllItem")
    public List<Item> getAllItem(@Nullable @Named("category") String category, @Nullable @Named("offset") Integer offset){
        List<Item> itemList = new ArrayList<>();
        try {
            Connection con = CloudSqlServices.getInstance().getConnection();
            String query = "select * from item i, category c where i.id_category = c.id_category and c.detail like ? limit ?,10";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            ResultSet resultSet = null;
            if(offset == null) offset=0;
            preparedStatement.setString(1,category);
            preparedStatement.setInt(2,offset);

            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String id = resultSet.getString("id_item");
                String name = resultSet.getString("name");
                String desc = resultSet.getString("desc");
                String price = resultSet.getString("price");
                Item item = new Item();
                item.setId(id);
                item.setName(name);
                item.setDesc(desc);
                item.setPrice(price);
                query = "select * from type where id_item = ? ";
                PreparedStatement preparedStatement1 = con.prepareStatement(query);
                ResultSet resultSet1 = null;
                preparedStatement1.setString(1,id);
                resultSet1 = preparedStatement1.executeQuery();

                List<Type> typeList = new ArrayList<>();
                while(resultSet1.next()){
                    Type type = new Type();
                    type.setId_item(id);
                    type.setId_type(resultSet1.getString("id_type"));
                    type.setSize(resultSet1.getString("size"));
                    type.setQty(resultSet1.getInt("qty"));
                    type.setColor(resultSet1.getString("color"));
                    typeList.add(type);
                }
                item.setType(typeList);
                itemList.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.info("Error Calling getItem method " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Error Calling getItem method " + e.getMessage());
        }
        return itemList;
    }


    /**
     * This inserts a new <code>Item</code> object.
     *
     * @param item The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertItem")
    public Item insertItem(Item item) {
        // TODO: Implement this function
        logger.info("Calling insertItem method");
        return item;
    }
}