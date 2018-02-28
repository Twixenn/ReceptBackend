package service;

import beans.EditRecipeBean;
import beans.LoginBean;
import beans.GetRecipeBean;
import beans.ReviewBean;

import javax.ejb.EJB;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class RecipeService {
    @EJB
    GetRecipeBean getRecipeBean;

    @EJB
    EditRecipeBean editRecipeBean;

    @EJB
    ReviewBean reviewBean;

    @EJB
    LoginBean loginBean;

    @GET
    @Path("tags")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategories() {
        JsonObject tags = getRecipeBean.getTags();
        if(tags != null) {
            return Response.ok(tags).build();
        } else {
            return Response.status(400).build();
        }
    }

    @GET
    @Path("recipes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecipes() {
        JsonArray recipe = getRecipeBean.getRecipes();
        if(recipe != null) {
            return Response.ok(recipe).build();
        } else {
            return Response.status(400).build();
        }
    }

    @GET
    @Path("recipe/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecipe(@PathParam("id") int id) {
        JsonObject recipe = getRecipeBean.getRecipe(id);
        if (recipe != null) {
            return Response.ok(recipe).build();
        } else {
            return Response.status(400).build();
        }
    }

    @GET
    @Path("recipe/{id}/reviews")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReviews(@PathParam("id") int id) {
        JsonArray recipe = reviewBean.getReviews(id);
        if(recipe != null) {
            return Response.ok(recipe).build();
        } else {
            return Response.status(400).build();
        }
    }

    @POST
    @Path("recipe/{id}/review")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addReview(String body, @PathParam("id") int id, @Context HttpHeaders header) {
        String basicAuth = header.getHeaderString("Authorization");
        int user = loginBean.login(basicAuth).getInt("id");
        if (user > 0) {
            return Response.status(reviewBean.addReview(body, id, user)).build();
        } else {
            return Response.status(401).build();
        }
    }

    @POST
    @Path("recipe")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRecipe(String body, @Context HttpHeaders header) {
        String basicAuth = header.getHeaderString("Authorization");
        int user = loginBean.login(basicAuth).getInt("id");
        if (user > 0) {
            int status = editRecipeBean.addRecipe(body, user);
            return Response.status(status).build();
        } else {
            return Response.status(401).build();
        }
    }

    @POST
    @Path("recipe/{userId}/favorite")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addFavoriteRecipe(@PathParam("userId") int user, String body) {
        if (user > 0) {
            int status = editRecipeBean.addFavoriteRecipe(body);
            return Response.status(status).build();
        } else {
            return Response.status(401).build();
        }
    }

    @PUT
    @Path("recipe")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRecipe(String body, @Context HttpHeaders header) {
        String basicAuth = header.getHeaderString("Authorization");
        int user = loginBean.login(basicAuth).getInt("id");
        if (user > 0) {
            int status = editRecipeBean.updateRecipe(body);
            return Response.status(status).build();
        } else {
            return Response.status(401).build();
        }
    }

    @DELETE
    @Path("recipe/{id}")
    @Consumes
    public Response deleteRecipe(@PathParam("id") int id, @Context HttpHeaders header) {
        String basicAuth = header.getHeaderString("Authorization");
        int user = loginBean.login(basicAuth).getInt("id");
        if (user > 0) {
            int status = editRecipeBean.deleteRecipe(id);
            return Response.status(status).build();
        } else {
            return Response.status(401).build();
        }
    }

}
