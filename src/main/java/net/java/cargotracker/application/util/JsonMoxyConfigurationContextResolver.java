package net.java.cargotracker.application.util;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.moxy.json.MoxyJsonConfiguration;

@Provider
// TODO See if this can be removed.
public class JsonMoxyConfigurationContextResolver
        implements ContextResolver<MoxyJsonConfiguration> {

    @Override
    public MoxyJsonConfiguration getContext(Class<?> objectType) {
        MoxyJsonConfiguration configuration = new MoxyJsonConfiguration();

        Map<String, String> namespacePrefixMapper = new HashMap<>(1);
        namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        configuration.setNamespacePrefixMapper(namespacePrefixMapper);
        configuration.setNamespaceSeparator(':');

        return configuration;
    }
}