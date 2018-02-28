package beans;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;

@Stateless
public class EditRecipeBean {
    private int recipeId = 0;

    public int addRecipe(String body, int user) {
        try {
            JsonObject recipe = getInputValues(body);
            String sql = "SELECT MAX(id) as id FROM Recipes";
            ConnectionBean connectionBean = new ConnectionBean(sql);
            ResultSet data = connectionBean.getData();
            data.next();
            recipeId = data.getInt("id");
            connectionBean.closeConnection();

            System.out.println("före");
            sql = String.format("INSERT INTO Recipes VALUES(%d, '%s', '%s', %d, %d, '%s', '%s', '%s' ); ",
                    ++recipeId,
                    recipe.getString("name"),
                    recipe.getString("description"),
                    user,
                    recipe.getInt("portions"),
                    recipe.getString("time"),
                    recipe.getString("instructions"),
                    recipe.getString("image"));
            connectionBean = new ConnectionBean(sql, 200);
            System.out.println("efter");

            JsonArray categories = recipe.getJsonArray("categories");
            sql = "INSERT INTO Recepies_Categories (recipeId, categoryId) VALUES ";
            insertCategories(categories, sql, connectionBean);

            JsonArray ingredients = recipe.getJsonArray("ingredients");
            sql = "INSERT INTO Recepies_Ingrediens (recipeId, ingredientId, amount, unitId) VALUES";
            insertIngredients(ingredients, sql, connectionBean);

            connectionBean.closeConnection();

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return 400;
        }

        return 201;
    }

    public int addFavoriteRecipe(String body) {
        try {
            JsonObject recipe = getInputValues(body);
            String sql = "SELECT MAX(id) as id FROM Recipes";
            ConnectionBean connectionBean = new ConnectionBean(sql);
            ResultSet data = connectionBean.getData();
            data.next();
            recipeId = data.getInt("id");

            sql = String.format("INSERT INTO SavedRecipes VALUES(%s, %s); ",
                    recipe.getString("recipeId"),
                    recipe.getString("userId"));
            connectionBean.executeUpdate(sql);
            connectionBean.closeConnection();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return 418;
        }
        return 201;
    }

    public int updateRecipe(String body) {
        try {
            JsonObject recipe = getInputValues(body);
            recipeId = recipe.getInt("id");

            String sql = String.format("UPDATE Recipes SET " +
                            "name = '%s', " +
                            "description = '%s', " +
                            "portions = %d, " +
                            "time = '%s', " +
                            "instructions = '%s', " +
                            "image = '%s' WHERE Recipes.id = %d; ",
                    recipe.getString("name"),
                    recipe.getString("description"),
                    recipe.getInt("portions"),
                    recipe.getString("time"),
                    recipe.getString("instructions"),
                    recipe.getString("image"),
                    recipeId);
            ConnectionBean connectionBean = new ConnectionBean(sql, 200);

            JsonArray categories = recipe.getJsonArray("categories");
            delete("DELETE FROM Recepies_Categories WHERE recipeId = " + recipeId, connectionBean);
            sql = "INSERT INTO Recepies_Categories (recipeId, categoryId) VALUES ";
            insertCategories(categories, sql, connectionBean);

            delete("DELETE FROM Recepies_Ingrediens WHERE recipeId = " + recipeId, connectionBean);
            JsonArray ingredients = recipe.getJsonArray("ingredients");
            sql = "INSERT INTO Recepies_Ingrediens (recipeId, ingredientId, amount, unitId) VALUES";
            insertIngredients(ingredients, sql, connectionBean);

            connectionBean.closeConnection();

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return 400;
        }

        return 201;
    }

    public int deleteRecipe(int id) {
        String sql = String.format("DELETE FROM Recipes WHERE id = %d", id);
        try {
            ConnectionBean connectionBean = new ConnectionBean(sql, 200);
            connectionBean.closeConnection();
            return 200;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 418;
    }

    public void insertCategories(JsonArray list, String sql, ConnectionBean connectionBean) throws SQLException {
        for(int i = 0; i < list.size(); i++) {
            sql += String.format("(%d, (SELECT id FROM Categories WHERE name = '%s')),",
                    recipeId, list.getJsonObject(i).getString("category"));
        }
        if(list.size() > 0) {
            sql = sql.substring(0, sql.length() - 1);
            connectionBean.executeUpdate(sql);
        }
    }

    public void insertIngredients(JsonArray list, String sql, ConnectionBean connectionBean) throws SQLException, ClassNotFoundException {
        for(int i = 0; i < list.size(); i++) {
            String sql2 = String.format("SELECT Ingrediens.name FROM Ingrediens WHERE Ingrediens.name = '%s'",
                    list.getJsonObject(i).getString("name"));
            ConnectionBean connectionBean2 = new ConnectionBean(sql2);
            ResultSet result = connectionBean2.getData();

            if(!result.next()) {
                sql2 = String.format("INSERT INTO Ingrediens (name) VALUES ('%s')", list.getJsonObject(i).getString("name"));
                connectionBean.executeUpdate(sql2);
            }
            System.out.println("hejsan");
            sql += String.format(" (%d, (SELECT id FROM Ingrediens WHERE name = '%s'), %s, (SELECT id FROM Units WHERE name = '%s')),",
                    recipeId,
                    list.getJsonObject(i).getString("name"),
                    list.getJsonObject(i).getString("amount"),
                    list.getJsonObject(i).getString("unit")
            );
            System.out.println("tjosan");
        }
        if(list.size() > 0) {
            sql = sql.substring(0, sql.length() - 1);
            connectionBean.executeUpdate(sql);
        }
    }

    public JsonObject getInputValues(String body) {
        JsonReader jsonReader = Json.createReader(new StringReader(body));
        JsonObject recipe = jsonReader.readObject();
        jsonReader.close();
        return recipe;
    }

    public void delete(String sql, ConnectionBean connectionBean) throws SQLException {
        connectionBean.executeUpdate(sql);
    }
}
