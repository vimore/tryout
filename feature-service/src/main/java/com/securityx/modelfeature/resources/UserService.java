package com.securityx.modelfeature.resources;

import com.securityx.modelfeature.auth.User;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.UserDao;
import com.securityx.modelfeature.utils.BCrypt;
import com.securityx.modelfeature.utils.ConcreteMultiValuedMap;
import com.securityx.modelfeature.utils.TokenUtils;
import com.sun.jersey.core.util.Base64;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Path("/user")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class UserService {
    private FeatureServiceConfiguration configuration = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private static final String SECURITY_TOKEN = "securityToken";

    public UserService(FeatureServiceConfiguration conf) {
        super();
        this.configuration = conf;
    }

    @GET
    public Response getAll(@Auth User user) {
        String role = user.getUserRole();
        if (role==null || !role.equals(User.ROLE_ADMIN)) {
            //throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            ConcreteMultiValuedMap<String, String> map = new ConcreteMultiValuedMap<String, String>();
            map.putSingle("error", "User does not have permission to access this data...");
            return Response.status(Response.Status.UNAUTHORIZED).entity(map.toJsonString()).build();

        } else {
            // list the available user
            UserDao userDao = new UserDao(this.configuration);
            List<User> fetchedUsers = userDao.fetchUsers(User.USER_TYPE.ALL);
            return Response.ok(fetchedUsers).build();
        }
    }

    @GET
    @Path("/{username}")
    public Response get(@Auth User user, @PathParam("username") String userName) {
        if (!user.getUserRole().equals(User.ROLE_ADMIN)) {
            //throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            ConcreteMultiValuedMap<String, String> map = new ConcreteMultiValuedMap<String, String>();
            map.putSingle("error", "User does not have permission to access this data...");
            return Response.status(Response.Status.UNAUTHORIZED).entity(map.toJsonString()).build();

        } else {

            // list the available user
            UserDao userDao = new UserDao(this.configuration);
            User fetchedUser = userDao.fetchUserDetailsByUserName(userName);
            // hide user password in response
            fetchedUser.setPassword(null);
            return Response.ok(fetchedUser).build();
        }
    }

    @POST
    //@Path("/add")
    public Response add(@Auth User user, @Valid User newUser) {

        ConcreteMultiValuedMap<String, String> map = new ConcreteMultiValuedMap<String, String>();
        if (!user.getUserRole().equals(User.ROLE_ADMIN)) {
            //throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            map.putSingle("error", "User does not have permission to add user...");
            return Response.status(Response.Status.UNAUTHORIZED).entity(map.toJsonString()).build();

        } else {
            // Add the user
            UserDao userDao = new UserDao(this.configuration);
            Boolean status = userDao.addUser(newUser);
            if (status) {
                map.putSingle("message", "User added successfully..");
            } else {
                map.putSingle("error", "Failed to add user..");
            }
            return Response.ok(map.toJsonString()).build();
        }
    }

    @GET
    @Path("/generate/user")
    public HashMap<String, String> generateNewUser(@Auth User user, @QueryParam("appName") String forAppName, @QueryParam("role") String role) {
        if (!user.getUserRole().equals(User.ROLE_ADMIN)) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        } else {

            // Generate User Name and Password
            String uniqueCode = UUID.randomUUID().toString().replace("-", "").toUpperCase();
            String secret = UUID.randomUUID().toString().replace("-", "*&^");

            User newUser = new User();
            newUser.setUsername(uniqueCode);
            newUser.setPassword(secret);
            newUser.setDisplayName(uniqueCode);
            if(role.equalsIgnoreCase(User.ROLE_ADMIN)) {
                newUser.setUserRole(User.ROLE_ADMIN);
            }
            if(role.equalsIgnoreCase(User.ROLE_VIEWER)) {
                newUser.setUserRole(User.ROLE_VIEWER);
            }
            newUser.setActiveStatus(true);
            newUser.setCreatedForApp(forAppName);
            newUser.setIsAutoGenerated(true);

            // Save Generated user details
            UserDao userDao = new UserDao(this.configuration);
            Boolean isUserSaved = userDao.addUser(newUser);

            if (isUserSaved) {
                User fetchUser = userDao.fetchUserDetailsByUserName(uniqueCode);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("accessKey", fetchUser.getUsername());
                map.put("accessSecret", fetchUser.getPassword());
                map.put("appName", fetchUser.getCreatedForApp());

                return map;
            }
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("/generatedUsers")
    public Response getAllGeneratedUsers(@Auth User user) {
        if (user.getUserRole() != null && !user.getUserRole().equals(User.ROLE_ADMIN)) {
            ConcreteMultiValuedMap<String, String> map = new ConcreteMultiValuedMap<String, String>();
            //throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            map.putSingle("error", "User does not have permission to access this data...");
            return Response.status(Response.Status.UNAUTHORIZED).entity(map.toJsonString()).build();
        } else {

            // list the available user
            UserDao userDao = new UserDao(this.configuration);
            List<User> fetchedUsers = userDao.fetchUsers(User.USER_TYPE.GENERATED);
            return Response.ok(fetchedUsers).build();
        }
    }

    /*@PUT
    @Path("/{username}")
    public User update(@Auth User user, @PathParam("username") String userName, @Valid User userDetais) {
        return userDetais;
    }*/

    @DELETE
    @Path("/{username}")
    public Response delete(@Auth User user, @PathParam("username") String userName) {

        ConcreteMultiValuedMap<String, String> map = new ConcreteMultiValuedMap<String, String>();
        if (!user.getUserRole().equals(User.ROLE_ADMIN)) {
            //throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            map.putSingle("error", "User does not have permission to add user...");
            return Response.status(Response.Status.UNAUTHORIZED).entity(map.toJsonString()).build();

        } else {
            // Add the user
            UserDao userDao = new UserDao(this.configuration);
            Boolean status = userDao.removeUserByUserName(userName);
            if (status) {
                map.putSingle("message", "User removed successfully..");
            } else {
                map.putSingle("error", "Failed to remove user..");
            }
            return Response.ok(map.toJsonString()).build();
        }
    }


    @GET
    @Path("/"+SECURITY_TOKEN)
    public Response userLogin(@Context HttpHeaders headers) {

        String authHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
        String decodeUserCreds = new String(Base64.decode(authHeader.getBytes(Charset.forName("UTF-8"))));
        String[] userCreds = decodeUserCreds.split(":");
        String userName = userCreds[0];
        String password = userCreds[1];

        ConcreteMultiValuedMap<String, String> map = new ConcreteMultiValuedMap<String, String>();
        UserDao userDao = new UserDao(this.configuration);
        User fetchedUser = userDao.fetchUserDetailsByUserName(userName);

        Boolean isPwdValid = false;
        if (fetchedUser.getPassword() != null) {
            // Check if password is valid or not
            isPwdValid = BCrypt.checkpw(password, fetchedUser.getPassword());
        }

        if (isPwdValid) {
            // User is valid, therefore generate token
            String role = fetchedUser.getUserRole().equals(User.ROLE_ADMIN) ? User.ROLE_ADMIN : User.ROLE_VIEWER;
            String secToken = TokenUtils.generateSecurityToken(userName, role, fetchedUser.getPassword(), this.configuration);
            map.putSingle(SECURITY_TOKEN, secToken);
            return Response.ok(map.toJsonString()).build();
        } else {
            map.putSingle("error", "invalid user credentials...");
            return Response.status(Response.Status.UNAUTHORIZED).entity(map.toJsonString()).build();
        }
    }

    @GET
    @Path("/regenerateToken")
    public HashMap<String, String> regenerateToken(@Auth User user, @Context HttpHeaders headers) {
        String secToken = TokenUtils.generateSecurityToken(user.getUsername(), user.getUserRole(), user.getPassword(), this.configuration);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(SECURITY_TOKEN, secToken);
        return map;
    }
}
