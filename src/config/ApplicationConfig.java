package config;

import javax.ws.rs.core.Application;
import java.util.Set;

/**
 *
 * @author Fia
 */
@javax.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends Application {


    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;

    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(service.RecipeService.class);
        resources.add(service.UserService.class);
        resources.add(utilities.CORSFilter.class);
    }

}
