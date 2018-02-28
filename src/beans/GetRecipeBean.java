package beans;

import java.lang.String;
import utilities.ConnectionFactory;

import javax.ejb.Stateless;
import javax.json.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Stateless
public class GetRecipeBean {

    public JsonArray getRecipes() {
        String sql = "SELECT Recipes.id, Users.username AS user, Users.image as userImage, Recipes.userId, name, Recipes.image, AVG(Reviews.reviewValue) AS reviews, Recipes.description, portions, time, instructions FROM Recipes LEFT JOIN Users ON Recipes.userId = Users.id LEFT JOIN Reviews ON Recipes.id = Reviews.recipeId GROUP BY Recipes.id, user, userImage, Recipes.userId, name, Recipes.image, Recipes.description, portions, time, instructions";
        return getRecipes(sql);
    }

    public JsonArray getFavoriteRecipes(int userId) {
        String sql = String.format("SELECT SavedRecipes.recipeId as id, SavedRecipes.userId, Recipes.name, Recipes.image, AVG(Reviews.reviewValue) AS reviews, Recipes.description, Users.username as user, Users.image AS userImage from SavedRecipes INNER JOIN Recipes ON Recipes.id = SavedRecipes.recipeId INNER JOIN Users ON Users.id = Recipes.userId LEFT JOIN Reviews ON Recipes.id = Reviews.recipeId WHERE SavedRecipes.userId = %d GROUP BY SavedRecipes.recipeId, SavedRecipes.userId, Recipes.name, Recipes.image, Recipes.description, Users.username, Users.image", userId);
        try {
            ConnectionBean connectionBean = new ConnectionBean(sql);
            ResultSet data = connectionBean.getData();
            JsonArrayBuilder recipes = Json.createArrayBuilder();

            while (data.next()) {
                JsonObjectBuilder recipe = Json.createObjectBuilder()
                        .add("id", data.getInt("id"))
                        .add("user", data.getString("user"))
                        .add("userId", data.getString("userId"))
                        .add("userImage", data.getString("userImage"))
                        .add("name", data.getString("name"))
                        .add("image", data.getString("image"))
                        .add("avgReview", data.getDouble("reviews"))
                        .add("description", data.getString("description"));

                recipes.add(recipe.build());
            }
            connectionBean.closeConnection();
            return recipes.build();

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return null;

    }

    public JsonObject getRecipe(int id) {
        String sql = String.format("SELECT Recipes.id, Users.username AS user, Users.image as userImage, Recipes.userId, name, Recipes.image, AVG(Reviews.reviewValue) AS reviews, Recipes.description, portions, time, instructions FROM Recipes LEFT JOIN Users ON Recipes.userId = Users.id LEFT JOIN Reviews ON Recipes.id = Reviews.recipeId WHERE Recipes.id = %d GROUP BY username, Users.image, Recipes.userId, name, Recipes.image, Recipes.description, Recipes.portions, Recipes.time, Recipes.instructions", id);
        JsonArray recipes = getRecipes(sql);
        JsonObject recipe = recipes.getJsonObject(0);

        return recipe;
    }

    public JsonArray getUserRecipes(String id) {
        String sql = String.format("SELECT Recipes.id, Users.username AS user, Users.image as userImage, Recipes.userId, name, Recipes.image, AVG(Reviews.reviewValue) AS reviews, Recipes.description, portions, time, instructions FROM Recipes LEFT JOIN Users ON Recipes.userId = Users.id LEFT JOIN Reviews ON Recipes.id = Reviews.recipeId WHERE Recipes.userId = '%s' GROUP BY Recipes.id, Users.username, Users.image, Recipes.name, Recipes.image, Recipes.description, Recipes.portions, Recipes.time, Recipes.instructions", id);
        try {
            Connection connection = ConnectionFactory.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet data = stmt.executeQuery(sql);
            JsonArrayBuilder recipes = Json.createArrayBuilder();

            while (data.next()) {
                JsonObjectBuilder recipe = Json.createObjectBuilder()
                        .add("id", data.getInt("id"))
                        .add("user", data.getString("user"))
                        .add("userId", data.getString("userId"))
                        .add("userImage", data.getString("userImage"))
                        .add("name", data.getString("name"))
                        .add("image", data.getString("image"))
                        .add("avgReview", data.getDouble("reviews"))
                        .add("description", data.getString("description"))
                        .add("instructions", data.getString("instructions"));

                recipes.add(recipe.build());
            }
            connection.close();
            return recipes.build();

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return null;
    }

    public JsonArray getRecipes(String sql) {
        try {
            ConnectionBean connectionBean = new ConnectionBean(sql);
            ResultSet data = connectionBean.getData();
            JsonArrayBuilder recipes = Json.createArrayBuilder();

            while (data.next()) {
                int id = data.getInt("id");
                JsonObjectBuilder recipe = Json.createObjectBuilder()
                        .add("id", data.getInt("id"))
                        .add("user", data.getString("user"))
                        .add("userId", data.getString("userId"))
                        .add("userImage", data.getString("userImage"))
                        .add("name", data.getString("name"))
                        .add("image", data.getString("image"))
                        .add("avgReview", data.getDouble("reviews"))
                        .add("description", data.getString("description"))
                        .add("portions", data.getInt("portions"))
                        .add("time", data.getString("time"))
                        .add("instructions", data.getString("instructions"))
                        .add("ingredients", getIngredients(id))
                        .add("categories", getCategories(id))
                        .add("reviews", getReviews(id));

                recipes.add(recipe.build());
            }
            connectionBean.closeConnection();
            return recipes.build();

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return null;
    }

    public JsonArray getIngredients(int id) throws SQLException, ClassNotFoundException {
        JsonArrayBuilder ingredients = Json.createArrayBuilder();
        String sql = String.format("SELECT Ingrediens.name as 'ingredient', amount, Units.name as 'unit' FROM Recepies_Ingrediens INNER JOIN Ingrediens ON ingredientId = Ingrediens.id INNER JOIN Units ON unitId = Units.id WHERE recipeId = %d"
                , id);
        ConnectionBean connectionBean = new ConnectionBean(sql);
        ResultSet result = connectionBean.getData();
        while (result.next()) {
            JsonObject ingredient = Json.createObjectBuilder()
                    .add("name", result.getString("ingredient"))
                    .add("amount", result.getDouble("amount"))
                    .add("unit", result.getString("unit"))
                    .build();
            ingredients.add(ingredient);
        }
        connectionBean.closeConnection();
        return ingredients.build();
    }

    public JsonObject getTags() {
        try {
            String sql = "SELECT name FROM Categories";
            JsonArray categories = getNames(sql);

            sql = "SELECT name FROM `Units`";
            JsonArray units = getNames(sql);

            JsonObjectBuilder tags = Json.createObjectBuilder()
                    .add("categories", categories)
                    .add("units", units);
            return tags.build();

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return null;
    }

    public JsonArray getNames(String sql) throws SQLException, ClassNotFoundException {
        ConnectionBean connectionBean = new ConnectionBean(sql);
        ResultSet data = connectionBean.getData();
        JsonArrayBuilder names = Json.createArrayBuilder();

        while (data.next()) {
            JsonObjectBuilder name = Json.createObjectBuilder()
                    .add("name", data.getString("name"));
            names.add(name.build());
        }
        connectionBean.closeConnection();
        return names.build();

    }

    public JsonArray getCategories(int id) throws SQLException, ClassNotFoundException {
        String sql = String.format("SELECT Categories.name as 'category', categoryId FROM Recepies_Categories INNER JOIN Categories ON categoryId = Categories.id WHERE recipeId = %d"
                , id);
        ConnectionBean connectionBean = new ConnectionBean(sql);
        ResultSet result = connectionBean.getData();
        JsonArrayBuilder ingredients = Json.createArrayBuilder();
        while (result.next()) {
            JsonObject ingredient = Json.createObjectBuilder()
                    .add("category", result.getString("category"))
                    .build();
            ingredients.add(ingredient);
        }
        connectionBean.closeConnection();
        return ingredients.build();
    }

    public JsonArray getReviews(int id) throws SQLException, ClassNotFoundException {
        JsonArrayBuilder ingredients = Json.createArrayBuilder();
        String sql = String.format("SELECT reviewValue, description, Users.username AS user, Users.id as userId FROM Reviews INNER JOIN Users ON Reviews.userId = Users.id WHERE recipeId = %d"
                , id);
        ConnectionBean connectionBean = new ConnectionBean(sql);
        ResultSet result = connectionBean.getData();
        while (result.next()) {
            JsonObject ingredient = Json.createObjectBuilder()
                    .add("reviewValue", result.getString("reviewValue"))
                    .add("user", result.getString("user"))
                    .add("userId", result.getInt("userId"))
                    .add("review", result.getString("description"))
                    .build();
            ingredients.add(ingredient);
        }
        return ingredients.build();
    }
}
