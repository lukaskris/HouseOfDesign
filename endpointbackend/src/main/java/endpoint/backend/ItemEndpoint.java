package endpoint.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
//import com.googlecode.objectify.ObjectifyService;
//import com.googlecode.objectify.cmd.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

//import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p>
 * DO NOT deploy this code unchanged as part of a real application to real users.
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

    private static final int DEFAULT_LIST_LIMIT = 20;

//    static {
//        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
//        ObjectifyService.register(Item.class);
//    }

    /**
     * Returns the {@link Item} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Item} with the provided ID.
     */
//    @ApiMethod(
//            name = "get",
//            path = "item/{id}",
//            httpMethod = ApiMethod.HttpMethod.GET)
//    public Item get(@Named("id") String id) throws NotFoundException {
//        logger.info("Getting Item with ID: " + id);
//        Item item = ofy().load().type(Item.class).id(id).now();
//        if (item == null) {
//            throw new NotFoundException("Could not find Item with ID: " + id);
//        }
//        return item;
//    }

    @ApiMethod(name = "get")
    public Item getItem(@Named("id") String id) {
        Item item = null;
        try {
            Connection con = CloudSqlServices.getInstance().getConnection();
            String query = "select * from item where id_item=?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            ResultSet resultSet = null;
            preparedStatement.setString(1,id);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                String name = resultSet.getString("name");
                String desc = resultSet.getString("desc");
                String price = resultSet.getString("price");
                item = new Item();
                item.setName(name);
                item.setDesc(desc);
                item.setPrice(price);
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
    public List<Item> getAllItem(@com.google.api.server.spi.config.Nullable @Named("category") String category, @com.google.api.server.spi.config.Nullable @Named("offset") Integer offset){
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
     * Inserts a new {@code Item}.
     */
//    @ApiMethod(
//            name = "insert",
//            path = "item",
//            httpMethod = ApiMethod.HttpMethod.POST)
//    public Item insert(Item item) {
//        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
//        // You should validate that item.id has not been set. If the ID type is not supported by the
//        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
//        //
//        // If your client provides the ID then you should probably use PUT instead.
//        ofy().save().entity(item).now();
//        logger.info("Created Item with ID: " + item.getId());
//
//        return ofy().load().entity(item).now();
//    }

    /**
     * Updates an existing {@code Item}.
     *
     * @param id   the ID of the entity to be updated
     * @param item the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Item}
     */
//    @ApiMethod(
//            name = "update",
//            path = "item/{id}",
//            httpMethod = ApiMethod.HttpMethod.PUT)
//    public Item update(@Named("id") String id, Item item) throws NotFoundException {
//        // TODO: You should validate your ID parameter against your resource's ID here.
//        checkExists(id);
//        ofy().save().entity(item).now();
//        logger.info("Updated Item: " + item);
//        return ofy().load().entity(item).now();
//    }

    /**
     * Deletes the specified {@code Item}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Item}
     */
//    @ApiMethod(
//            name = "remove",
//            path = "item/{id}",
//            httpMethod = ApiMethod.HttpMethod.DELETE)
//    public void remove(@Named("id") String id) throws NotFoundException {
//        checkExists(id);
//        ofy().delete().type(Item.class).id(id).now();
//        logger.info("Deleted Item with ID: " + id);
//    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
//    @ApiMethod(
//            name = "list",
//            path = "item",
//            httpMethod = ApiMethod.HttpMethod.GET)
//    public CollectionResponse<Item> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
//        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
//        Query<Item> query = ofy().load().type(Item.class).limit(limit);
//        if (cursor != null) {
//            query = query.startAt(Cursor.fromWebSafeString(cursor));
//        }
//        QueryResultIterator<Item> queryIterator = query.iterator();
//        List<Item> itemList = new ArrayList<Item>(limit);
//        while (queryIterator.hasNext()) {
//            itemList.add(queryIterator.next());
//        }
//        return CollectionResponse.<Item>builder().setItems(itemList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
//    }
//
//    private void checkExists(String id) throws NotFoundException {
//        try {
//            ofy().load().type(Item.class).id(id).safe();
//        } catch (com.googlecode.objectify.NotFoundException e) {
//            throw new NotFoundException("Could not find Item with ID: " + id);
//        }
//    }
}