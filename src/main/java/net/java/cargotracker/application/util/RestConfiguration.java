package net.java.cargotracker.application.util;

import javax.ws.rs.ApplicationPath;
import net.java.cargotracker.interfaces.handling.rest.HandlingReportService;
import net.java.pathfinder.api.GraphTraversalService;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * JAX-RS configuration.
 */
@ApplicationPath("rest")
public class RestConfiguration extends ResourceConfig {

    public RestConfiguration() {
        // Resources
        packages(new String[]{
                    HandlingReportService.class.getPackage().getName(),
                    GraphTraversalService.class.getPackage().getName()});
        // Providers - JSON.        
        register(new MoxyJsonFeature());
        register(new JsonMoxyConfigurationContextResolver()); // TODO See if this can be removed.
    }
}