package com.securityx.modelfeature.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.UserDao;
import com.securityx.modelfeature.utils.ConcreteMultiValuedMap;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.sun.research.ws.wadl.HTTPMethods;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.jackson.Jackson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;

/**
 * A security provider that will look at each request when received by an endpoint using the auth attribute
 * and check that it has a header value containing a token and will authenticate the token to get the User
 * for the request (otherwise throw an AuthenticationException). That User is the authenticated User associated
 * with the request and the resource method handling the request can use it to check authorisation to perform actions.
 *
 * @param <T> The User class to be returned when a request is authenticated
 */
public class SecurityProvider<T> implements InjectableProvider<Auth, Parameter> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityProvider.class);
    private final Authenticator<UserToken, T> authenticator;
    private FeatureServiceConfiguration featureServiceConfiguration = null;
    //private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    public SecurityProvider(Authenticator<UserToken, T> authenticator, FeatureServiceConfiguration featureServiceConfiguration) {
        this.authenticator = authenticator;
        this.featureServiceConfiguration = featureServiceConfiguration;
    }

    public static class SecurityInjectable<T> extends AbstractHttpContextInjectable<T> {
        private FeatureServiceConfiguration fsConf = null;
        private final Authenticator<UserToken, T> authenticator;
        private final boolean required;

        public SecurityInjectable(Authenticator<UserToken, T> authenticator, boolean required, FeatureServiceConfiguration conf) {
            this.authenticator = authenticator;
            this.required = required;
            this.fsConf = conf;
        }

        @Override
        public T getValue(HttpContext c) {

            // This is where the credentials are extracted from the request
            final String header = c.getRequest().getHeaderValue(HttpHeaders.AUTHORIZATION);
            final String disableAuth = c.getRequest().getHeaderValue("X-E8-Disable-API-Auth");
            try {
                if(disableAuth != null &&  
                    disableAuth.equals("true") &&  
                    fsConf.getEnvironment().equals(FeatureServiceConfiguration.DEV_ENV)){

                    return (T) new User();
                }
                if (header != null && !header.isEmpty()) {
                    // Obtain user credentials passed in authorization header
                    String decodeUserCreds = new String(Base64.decode(header));
                    String[] userCreds = decodeUserCreds.split(":");
                    String userName = userCreds[0];
                    String jwtToken = userCreds[1];
                    // Get user credentials from DB
                    UserDao userDao = new UserDao(fsConf);
                    User fetchedUser = userDao.fetchUserDetailsByUserName(userName);
                    if (fetchedUser.getUsername() != null) {

                        // creiate composite string which contains jwt token and secret
                        String compositeToken = jwtToken.concat("::").concat(fetchedUser.getPassword());
                        UserToken userToken = new UserToken(compositeToken, userName, getPayload(c), fetchedUser);
                        final Optional<T> result = authenticator.authenticate(userToken);
                        if (result.isPresent()) {
                            return result.get();
                        }
                    } else {
                        String err = "User \"" + userName + "\" Not Found..";
                        throw new WebApplicationException(Response
                                .status(Response.Status.UNAUTHORIZED)
                                .entity("{\"error\": \""+err+"\"}")
                                .build());
                    }
                }
            } catch (AuthenticationException e) {
                LOGGER.error("Could not authenticate request", e);
                throw new WebApplicationException(Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \""+e.getMessage()+"\"}")
                        .build());
            } catch (IOException e) {
                LOGGER.error("Could not convert entity to string", e);
                throw new WebApplicationException(Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \""+e.getMessage()+"\"}")
                        .build());
            }

            if (required) {
                LOGGER.error("Did not get the required auth header {}: {}", HttpHeaders.AUTHORIZATION,c.getProperties().toString());
                throw new WebApplicationException(Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"No authorization header found\"}")
                        .build());
            }

            return null;
        }

        @SuppressWarnings({"unchecked"})
        private static MultivaluedMap<String, String> getPayload(HttpContext c) throws IOException {
            MultivaluedMap<String, String> queryParameters = c.getRequest().getQueryParameters();
            ConcreteMultiValuedMap<String, String> payload = new ConcreteMultiValuedMap<>();
            payload.addAll(queryParameters);

            // Request body, if attached
            try {
                HashMap<String, String> requestBody = c.getRequest().getEntity(HashMap.class);
                payload.addAll(requestBody);
            } catch (Exception e) {
                // No Request body present
                if(c.getRequest().getMethod().equalsIgnoreCase(HTTPMethods.POST.toString())
                        || c.getRequest().getMethod().equalsIgnoreCase(HTTPMethods.PUT.toString())){
                    LOGGER.error("Could not get request body ", e);
                    throw new WebApplicationException(Response
                            .status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\": \""+e.getMessage()+"\"}")
                            .build());
                }else{
                    LOGGER.error("Cannot process request {}, Error: {}" , c.getUriInfo().getPath(), e);
                }
            }

            return payload;
        }
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, Auth auth, Parameter parameter) {
        return new SecurityInjectable<>(authenticator, auth.required(), this.featureServiceConfiguration);
    }
}
