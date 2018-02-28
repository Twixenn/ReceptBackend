package utilities;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
@Provider
public class CORSFilter implements ContainerResponseFilter {
    @Override
    public void filter(final ContainerRequestContext requestContext,
                       final ContainerResponseContext cres) throws IOException {
        cres.getHeaders().putSingle("Access-Control-Allow-Origin", "*");
        cres.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
        cres.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS, HEAD");
        cres.getHeaders().putSingle("Access-Control-Allow-Headers", "Origin, Content-Type, Authorization, Accept, X-Requested-With");
        cres.getHeaders().add("Access-Control-Max-Age", "1209600");
    }
}

