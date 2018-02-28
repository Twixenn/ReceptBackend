package beans;

import utilities.ConnectionFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.*;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Stateless
public class ReviewBean {

    public JsonArray getReviews(int id) {
        try {
            String sql = String.format("SELECT recipeId, Users.username as user, Reviews.userId, reviewValue, description FROM Reviews INNER JOIN Users on id = Reviews.userId WHERE recipeId = %d", id);
            ConnectionBean connectionBean = new ConnectionBean(sql);
            ResultSet data = connectionBean.getData();

            JsonArrayBuilder reviews = Json.createArrayBuilder();

            while (data.next()) {
                JsonObjectBuilder review = Json.createObjectBuilder()
                        .add("user", data.getString("user"))
                        .add("recipeId", data.getInt("recipeId"))
                        .add("value", data.getString("reviewValue"))
                        .add("description", data.getString("description"));
                reviews.add(review.build());
            }
            connectionBean.closeConnection();
            return reviews.build();

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return null;
    }

    public JsonArray getUserReviews(String id) {
        try {
            String sql = String.format("SELECT recipeId, Recipes.name, Recipes.userId, Recipes.image, Reviews.userId, reviewValue, Reviews.description, u1.username as user, u2.username as recipeUser FROM Reviews INNER JOIN Users u1 on u1.id = Reviews.userId INNER JOIN Recipes On Recipes.id = recipeId INNER JOIN Users u2 ON Recipes.userId = u2.id WHERE u1.id = %s", id);
            ConnectionBean connectionBean = new ConnectionBean(sql);
            ResultSet data = connectionBean.getData();
            JsonArrayBuilder reviews = Json.createArrayBuilder();

            while (data.next()) {
                JsonObjectBuilder review = Json.createObjectBuilder()
                        .add("user", data.getString("user"))
                        .add("recipeUser", data.getString("recipeUser"))
                        .add("recipeId", data.getInt("recipeId"))
                        .add("value", data.getString("reviewValue"))
                        .add("description", data.getString("description"))
                        .add("image", data.getString("image"))
                        .add("recipeName", data.getString("name"));
                reviews.add(review.build());
            }
            connectionBean.closeConnection();
            return reviews.build();

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return null;
    }

    public int addReview(String body, int recipeId, int userId) {
        try {
            JsonObject review = getInputValues(body);
            int reviewValue = review.getInt("reviewValue");
            if(reviewValue > 5 || reviewValue < 0) {
                return 400;
            }

            Connection connection = ConnectionFactory.getConnection();
            Statement stmt = connection.createStatement();
            String sql = String.format("INSERT INTO Reviews VALUES(%d, %d, %d, '%s' ); ",
                    recipeId,
                    userId,
                    review.getInt("reviewValue"),
                    review.getString("description"));
            stmt.executeUpdate(sql);

            connection.close();

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return 400;
        }

        return 201;
    }

    public JsonObject getInputValues(String body) {
        JsonReader jsonReader = Json.createReader(new StringReader(body));
        JsonObject recipe = jsonReader.readObject();
        jsonReader.close();
        return recipe;
    }
}
