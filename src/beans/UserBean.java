package beans;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.sql.ResultSet;

@Stateless
public class UserBean {

    public JsonObject getUser(String id) {
        String sql = String.format("SELECT Users.image as userImage, Users.username as user, Users.id FROM Users WHERE Users.id = %s", id);

        try {
            ConnectionBean connectionBean = new ConnectionBean(sql);
            ResultSet data = connectionBean.getData();
            JsonObjectBuilder user = Json.createObjectBuilder();

            while (data.next()) {
                user
                        .add("id", data.getInt("id"))
                        .add("user", data.getString("user"))
                        .add("userImage", data.getString("userImage"));

            }
            connectionBean.closeConnection();
            return user.build();

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return null;
    }
}
