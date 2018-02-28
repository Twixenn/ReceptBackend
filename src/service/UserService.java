package service;

import beans.LoginBean;
import beans.GetRecipeBean;
import beans.ReviewBean;
import beans.UserBean;

import javax.ejb.EJB;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class UserService {
    @EJB
    GetRecipeBean getRecipeBean;

    @EJB
    ReviewBean reviewBean;

    @EJB
    LoginBean loginBean;

    @EJB
    UserBean userBean;

    @GET
    @Path("recipes/favorite/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFavoriteRecipes(@PathParam("userId") int userId) {
        JsonArray recipe = getRecipeBean.getFavoriteRecipes(userId);
        if(recipe != null) {
            return Response.ok(recipe).build();
        } else {
            return Response.status(400).build();
        }
    }

    @GET
    @Path("{id}/reviews")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserReviews(@PathParam("id") String id) {
        JsonArray recipe = reviewBean.getUserReviews(id);
        if (recipe != null) {
            return Response.ok(recipe).build();
        } else {
            return Response.status(400).build();
        }
    }

    @GET
    @Path("{id}/recipes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsersRecipes(@PathParam("id") String id) {
        JsonArray recipe = getRecipeBean.getUserRecipes(id);
        if (recipe != null) {
            return Response.ok(recipe).build();
        } else {
            return Response.status(400).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") String id) {
        JsonObject user = userBean.getUser(id);
        if (user != null) {
            return Response.ok(user).build();
        } else {
            return Response.status(400).build();
        }
    }

    @GET
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context HttpHeaders header) {
        String basicAuth = header.getHeaderString("Authorization");
        JsonObject user = loginBean.checkCredentials(basicAuth);
        if (user.getString("userId") != null) {
            return Response.ok(user).build();
        } else {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("user/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(@Context HttpHeaders header) {
        String basicAuth = header.getHeaderString("Authorization");
        JsonObject user = loginBean.login(basicAuth);
        if (user.getString("userId") != null) {
            return Response.ok(user).build();
        } else {
            return Response.status(401).build();
        }
    }

    @POST
    @Path("user")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(@Context HttpHeaders header) {
        String basicAuth = header.getHeaderString("Authorization");
        return Response.status(loginBean.createUser(basicAuth)).build();
    }
}
