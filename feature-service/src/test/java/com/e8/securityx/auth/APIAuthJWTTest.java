package com.e8.securityx.auth;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.e8.test.HBaseTestBase;
import com.securityx.modelfeature.auth.SecurityProvider;
import com.securityx.modelfeature.auth.User;
import com.securityx.modelfeature.auth.UserAuthenticator;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.config.SecurityTokenConfiguration;
import com.securityx.modelfeature.dao.PhoenixUtils;
import com.securityx.modelfeature.resources.UserService;
import com.securityx.modelfeature.utils.ConcreteMultiValuedMap;
import com.securityx.modelfeature.utils.TokenUtils;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.core.util.Base64;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class APIAuthJWTTest extends HBaseTestBase{
    private static FeatureServiceConfiguration configuration = new FeatureServiceConfiguration();
    private static final ObjectMapper mapper = Jackson.newObjectMapper();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private UserService userService;

    @BeforeClass
    public static void startServers() throws Exception{
        setupHBase();
        String confFile = System.getProperty("user.dir")+"/src/main/config/test_cfg.yml";

        configuration = new ConfigurationFactory<>(FeatureServiceConfiguration.class,
                validator,
                mapper, "dw")
                .build(new File(confFile));
        configuration.setZkQuorum("127.0.0.1:"+utility.getZkCluster().getClientPort());
        configuration.setSolrQuorum("127.0.0.1:"+utility.getZkCluster().getClientPort()+"/solr");
        try {
            Connection conn = PhoenixUtils.getPhoenixConnection(configuration);
            String sql = "CREATE TABLE IF NOT EXISTS APP_USERS (\n" +
                    "    USER_NAME VARCHAR NOT NULL,\n" +
                    "    DISPLAY_NAME VARCHAR,\n" +
                    "    PASSWORD VARCHAR,\n" +
                    "    USER_ROLE VARCHAR,\n" +
                    "    CREATED_DATE_TIME VARCHAR,\n" +
                    "    ACTIVE_STATUS BOOLEAN, \n" +
                    "    CREATED_FOR_APP VARCHAR,\n " +
                    "    IS_AUTO_GENERATED BOOLEAN \n" +
                    "CONSTRAINT PK PRIMARY KEY ( USER_NAME )\n" +
                    ") SALT_BUCKETS=10";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.execute();
        }catch(Exception ex){
            throw ex;
        }
    }
    @AfterClass
    public static void stopServers() throws Exception{
       try {
            String sql = "DROP TABLE IF EXISTS APP_USERS";
            Connection conn = PhoenixUtils.getPhoenixConnection(configuration);
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.execute();
       }catch(Exception ex){
            ex.printStackTrace();
            //throw ex;
       }
       teardownHBase();
    }
    @Before
    public void setup() throws Exception{
        userService = new UserService(configuration);
    }
    @After
    public void tearDown() throws Exception{
        // nothing to do
    }
    @Test
    public void testGeneration() throws Exception{
        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin");
        user.setUserRole(User.ROLE_ADMIN);
        HashMap<String, String> keys =userService.generateNewUser(user, "QRadar", "admin");
        assertNotNull(keys);
        assertEquals(3, keys.size());
        assertTrue("Keys do not contain accessKey" , keys.containsKey("accessKey"));
        assertTrue("Keys do not contain accessSecret" , keys.containsKey("accessSecret"));
        String jwtToken=TokenUtils.generateSecurityToken(keys.get("accessKey"),"api", keys.get("accessSecret"), configuration);
        assertTrue("The token is not valid!", TokenUtils.isTokenValid(jwtToken, keys.get("accessSecret")));
    }
    @Test
    public void testGenerateAndValidate() throws Exception{
        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin");
        user.setUserRole(User.ROLE_ADMIN);
        HashMap<String, String> keys = userService.generateNewUser(user, "QRadar", "admin");
        assertNotNull(keys);
        assertEquals(3, keys.size());
        assertTrue("Keys do not contain accessKey" , keys.containsKey("accessKey"));
        assertTrue("Keys do not contain accessSecret" , keys.containsKey("accessSecret"));

        //String requestBody = "{\"blah\":\"blah\", \"bleh\":\"bleh\"}";
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("blah", "blah");
        requestBody.put("bleh", "bleh");
        String jwtToken = getJwtToken(keys.get("accessSecret"), mapper.writeValueAsString(requestBody));
        SecurityProvider.SecurityInjectable<User> injectable = new SecurityProvider.SecurityInjectable<>(new UserAuthenticator(), true, configuration);
        String auth = new String(Base64.encode(keys.get("accessKey")+":"+jwtToken),"US-ASCII");

        HttpRequestContext httpRequestContext = getRequestContext(auth, getEntity( mapper.writeValueAsString(requestBody)),
                mapper.writeValueAsString(requestBody),
                requestBody,
                new ConcreteMultiValuedMap<>());
        HttpContext httpContext = getContext(httpRequestContext);
        User authUser = injectable.getValue(httpContext);
        assertEquals("The AccessKeys are not equals", keys.get("accessKey"), authUser.getUsername());
    }
    @Test
    public void testPayloadValidate() throws Exception{
        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin");
        user.setUserRole(User.ROLE_ADMIN);
        HashMap<String, String> keys = userService.generateNewUser(user, "QRadar", "viewer");
        assertNotNull(keys);
        assertEquals(3, keys.size());
        assertTrue("Keys do not contain accessKey" , keys.containsKey("accessKey"));
        assertTrue("Keys do not contain accessSecret" , keys.containsKey("accessSecret"));

        //String requestBody = "{\"blah\":\"blah\", \"bleh\":\"bleh\"}";
        HashMap<String, String> requestBody= new HashMap<>();
        requestBody.put("blahKey", "blahVal");
        requestBody.put("blehKey", "blehVal");

        ConcreteMultiValuedMap<String, String> queryParams = new ConcreteMultiValuedMap<>();
        queryParams.putSingle("queryKey1", "queryVal1");
        queryParams.putSingle("queryKey2", "queryVal2");

        ConcreteMultiValuedMap<String, String> payload = new ConcreteMultiValuedMap<>();
        payload.addAll(requestBody);
        payload.addAll(queryParams);

        String payloadJson = payload.toJsonString();
        String jwtToken = getJwtToken(keys.get("accessSecret"), payloadJson);
        SecurityProvider.SecurityInjectable<User> injectable = new SecurityProvider.SecurityInjectable<>(new UserAuthenticator(), true, configuration);
        String auth = new String(Base64.encode(keys.get("accessKey")+":"+jwtToken),"US-ASCII");

        HttpRequestContext httpRequestContext = getRequestContext(auth, getEntity( mapper.writeValueAsString(requestBody)),
                mapper.writeValueAsString(requestBody), requestBody, payload);
        HttpContext httpContext = getContext(httpRequestContext);
        User authUser = injectable.getValue(httpContext);
        assertEquals("The AccessKeys are not equals", keys.get("accessKey"), authUser.getUsername());
    }
    @Test
    public void testDisableAuth() throws Exception{
        // curl  -H 'Content-Type: application/json' -H 'Accept: application/json' 'http://localhost:9080/service/global/statistics?startTime=2016-07-29T00%3A00%3A00.000Z&endTime=2016-07-30T00%3A00%3A00.000Z'
        // should return an error

        // curl  -H 'X-E8-Disable-Auth: true' -H 'Content-Type: application/json' -H 'Accept: application/json' 'http://localhost:9080/service/global/statistics?startTime=2016-07-29T00%3A00%3A00.000Z&endTime=2016-07-30T00%3A00%3A00.000Z'
        // should return the data when the environment in config.yml is set to 'DEV'

        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin");
        user.setUserRole(User.ROLE_ADMIN);
        HashMap<String, String> keys = userService.generateNewUser(user, "QRadar", "viewer");
        assertNotNull(keys);
        assertEquals(3, keys.size());
        assertTrue("Keys do not contain accessKey" , keys.containsKey("accessKey"));
        assertTrue("Keys do not contain accessSecret" , keys.containsKey("accessSecret"));

        //String requestBody = "{\"blah\":\"blah\", \"bleh\":\"bleh\"}";
        HashMap<String, String> requestBody= new HashMap<>();
        requestBody.put("blahKey", "blahVal");
        requestBody.put("blehKey", "blehVal");

        ConcreteMultiValuedMap<String, String> queryParams = new ConcreteMultiValuedMap<>();
        queryParams.putSingle("queryKey1", "queryVal1");
        queryParams.putSingle("queryKey2", "queryVal2");

        ConcreteMultiValuedMap<String, String> payload = new ConcreteMultiValuedMap<>();
        payload.addAll(requestBody);
        payload.addAll(queryParams);

        String payloadJson = payload.toJsonString();
        String jwtToken = getJwtToken(keys.get("accessSecret"), payloadJson);
        SecurityProvider.SecurityInjectable<User> injectable = new SecurityProvider.SecurityInjectable<>(new UserAuthenticator(), true, configuration);
        String auth = new String(Base64.encode(keys.get("accessKey")+":"+jwtToken),"US-ASCII");

        HttpRequestContext httpRequestContext = getRequestContext("", getEntity( mapper.writeValueAsString(requestBody)),
                mapper.writeValueAsString(requestBody), requestBody, null, true);
        HttpContext httpContext = getContext(httpRequestContext);
        User authUser = injectable.getValue(httpContext);
        //assertEquals("The AccessKeys are not equals", keys.get("accessKey"), authUser.getUsername());
    }
    @Test
    public void testPayloadValidateFailed() throws Exception{
        try {
            User user = new User();
            user.setUsername("admin");
            user.setPassword("admin");
            user.setUserRole(User.ROLE_ADMIN);
            HashMap<String, String> keys = userService.generateNewUser(user, "QRadar", "viewer");
            assertNotNull(keys);
            assertEquals(3, keys.size());
            assertTrue("Keys do not contain accessKey", keys.containsKey("accessKey"));
            assertTrue("Keys do not contain accessSecret", keys.containsKey("accessSecret"));

            //String requestBody = "{\"blah\":\"blah\", \"bleh\":\"bleh\"}";
            HashMap<String, String> requestBody = new HashMap<>();
            requestBody.put("blahKey", "blahVal");
            requestBody.put("blehKey", "blehVal");

            ConcreteMultiValuedMap<String, String> queryParams = new ConcreteMultiValuedMap<>();
            queryParams.putSingle("queryKey1", "queryVal1");
            queryParams.putSingle("queryKey2", "queryVal2");

            ConcreteMultiValuedMap<String, String> payload = new ConcreteMultiValuedMap<>();
            payload.addAll(requestBody);
            payload.addAll(queryParams);

            //adding extra query params & request body items
            queryParams.putSingle("queryKey2", "queryVal2");
            requestBody.put("blahDeKey", "blahDeVal");

            String payloadJson = payload.toJsonString();
            String jwtToken = getJwtToken(keys.get("accessSecret"), payloadJson);
            SecurityProvider.SecurityInjectable<User> injectable = new SecurityProvider.SecurityInjectable<>(new UserAuthenticator(), true, configuration);
            String auth = new String(Base64.encode(keys.get("accessKey") + ":" + jwtToken), "US-ASCII");

            HttpRequestContext httpRequestContext = getRequestContext(auth, getEntity(mapper.writeValueAsString(requestBody)),
                    mapper.writeValueAsString(requestBody), requestBody, payload);
            HttpContext httpContext = getContext(httpRequestContext);
            User authUser = injectable.getValue(httpContext);
            assertEquals("The AccessKeys are not equals", keys.get("accessKey"), authUser.getUsername());
        }catch(WebApplicationException ex){
            ex.printStackTrace();
            assertNotNull(ex);
        }
    }
    @Test
    public void testUserLogin() throws Exception{
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setUserRole(User.ROLE_ADMIN);

        User user = new User();
        user.setUsername("myuser");
        user.setPassword("w3lc0m3T0E8");
        user.setUserRole(User.ROLE_VIEWER);

        // Add a new user
        Response addUserResp = userService.add(admin, user);
        assertEquals(Response.Status.OK.getStatusCode(), addUserResp.getStatus());
        String addUserReturn = addUserResp.getEntity().toString();
        assertEquals("{\"message\":\"User added successfully..\"}", addUserReturn);

        // Log the user in and generate the security token for the user
        Response loginResp = userService.userLogin(getHeaders(user.getUsername(), user.getPassword()));
        assertEquals(Response.Status.OK.getStatusCode(), loginResp.getStatus());
        String loginReturn = loginResp.getEntity().toString();
        HashMap<String, String> data = mapper.readValue(loginReturn, new TypeReference<HashMap<String,String>>() {});
        assertTrue("The log in response does not contain securityToken", data.containsKey("securityToken"));

        // validate the security token
        SecurityProvider.SecurityInjectable<User> injectable = new SecurityProvider.SecurityInjectable<>(new UserAuthenticator(), true, configuration);
        String token = new String(Base64.encode(user.getUsername()+":"+data.get("securityToken")), "US-ASCII");

        HttpRequestContext httpRequestContext = getRequestContext(token, getEntity(""), "",
                new HashMap<>(),
                new ConcreteMultiValuedMap<>());

        HttpContext httpContext = getContext(httpRequestContext);
        User authUser = injectable.getValue(httpContext);
        assertEquals("The Username are not equals", user.getUsername(), authUser.getUsername());
    }

    @Test
    public void testUserLoginAndApi() throws Exception{
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setUserRole(User.ROLE_ADMIN);

        User user = new User();
        user.setUsername("myuser");
        user.setPassword("w3lc0m3T0E8");
        // only admins can fetch the user details
        user.setUserRole(User.ROLE_ADMIN);

        // Add a new user
        Response addUserResp = userService.add(admin, user);
        assertEquals(Response.Status.OK.getStatusCode(), addUserResp.getStatus());
        String addUserReturn = addUserResp.getEntity().toString();
        assertEquals("{\"message\":\"User added successfully..\"}", addUserReturn);

        // Log the user in and generate the security token for the user
        Response loginResp = userService.userLogin(getHeaders(user.getUsername(), user.getPassword()));
        assertEquals(Response.Status.OK.getStatusCode(), loginResp.getStatus());
        String loginReturn = loginResp.getEntity().toString();
        HashMap<String, String> data = mapper.readValue(loginReturn, new TypeReference<HashMap<String,String>>() {});
        assertTrue("The log in response does not contain securityToken", data.containsKey("securityToken"));

        // validate the security token
        SecurityProvider.SecurityInjectable<User> injectable = new SecurityProvider.SecurityInjectable<>(new UserAuthenticator(), true, configuration);
        String token = new String(Base64.encode(user.getUsername()+":"+data.get("securityToken")), "US-ASCII");

        HttpRequestContext httpRequestContext = getRequestContext(token, getEntity(""), "",
                new HashMap<>(),
                new ConcreteMultiValuedMap<>());

        HttpContext httpContext = getContext(httpRequestContext);
        User authUser = injectable.getValue(httpContext);
        assertEquals("The Username are not equals", user.getUsername(), authUser.getUsername());
        assertEquals("The Role are not equals", user.getUserRole(), authUser.getUserRole());
        //ensure that we can invoke the api
        Response resp = userService.getAll(authUser);
        assertEquals(200, resp.getStatus());

    }
    @Test
    public void testRegenerateToken() throws Exception{
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setUserRole(User.ROLE_ADMIN);

        User user = new User();
        user.setUsername("myuser");
        user.setPassword("w3lc0m3T0E8");
        user.setUserRole(User.ROLE_VIEWER);

        // Add a new user
        Response addUserResp = userService.add(admin, user);
        assertEquals(Response.Status.OK.getStatusCode(), addUserResp.getStatus());
        String addUserReturn = addUserResp.getEntity().toString();
        assertEquals("{\"message\":\"User added successfully..\"}", addUserReturn);

        // Log the user in and generate the security token for the user
        Response loginResp = userService.userLogin(getHeaders(user.getUsername(), user.getPassword()));
        assertEquals(Response.Status.OK.getStatusCode(), loginResp.getStatus());
        String loginReturn = loginResp.getEntity().toString();
        HashMap<String, String> data = mapper.readValue(loginReturn, new TypeReference<HashMap<String,String>>() {});
        assertTrue("The log in response does not contain securityToken", data.containsKey("securityToken"));

        // validate the security token
        SecurityProvider.SecurityInjectable<User> injectable = new SecurityProvider.SecurityInjectable<User>(new UserAuthenticator(), true, configuration);
        String token = new String(Base64.encode(user.getUsername()+":"+data.get("securityToken")), "US-ASCII");

        HttpRequestContext httpRequestContext = getRequestContext(token, getEntity(""), "",
                new HashMap<>(),
                new ConcreteMultiValuedMap<>());

        HttpContext httpContext = getContext(httpRequestContext);
        User authUser = injectable.getValue(httpContext);
        assertEquals("The AccessKeys are not equals", user.getUsername(), authUser.getUsername());
        HashMap<String, String> map = userService.regenerateToken(user, getHeaders(user.getUsername(), token));
        assertNotNull(map);
        assertNotEquals(0, map.size());
        assertTrue(map.containsKey("securityToken"));
        assertNotEquals(data.get("securityToken"), map.get("securityToken"));
    }

    @Test
    public void testGetAllRoleNull() throws Exception{
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setUserRole(User.ROLE_ADMIN);

        User user = new User();
        user.setUsername("myuser");
        user.setPassword("w3lc0m3T0E8");
        user.setUserRole(User.ROLE_VIEWER);

        // Add a new user
        Response addUserResp = userService.add(admin, user);
        assertEquals(Response.Status.OK.getStatusCode(), addUserResp.getStatus());
        String addUserReturn = addUserResp.getEntity().toString();
        assertEquals("{\"message\":\"User added successfully..\"}", addUserReturn);

        // Log the user in and generate the security token for the user
        Response loginResp = userService.userLogin(getHeaders(user.getUsername(), user.getPassword()));
        assertEquals(Response.Status.OK.getStatusCode(), loginResp.getStatus());
        String loginReturn = loginResp.getEntity().toString();
        HashMap<String, String> data = mapper.readValue(loginReturn, new TypeReference<HashMap<String,String>>() {});
        assertTrue("The log in response does not contain securityToken", data.containsKey("securityToken"));

        // validate the security token
        SecurityProvider.SecurityInjectable<User> injectable = new SecurityProvider.SecurityInjectable<>(new UserAuthenticator(), true, configuration);
        String token = new String(Base64.encode(user.getUsername()+":"+data.get("securityToken")), "US-ASCII");

        HttpRequestContext httpRequestContext = getRequestContext(token, getEntity(""), "",
                new HashMap<>(),
                new ConcreteMultiValuedMap<>());

        HttpContext httpContext = getContext(httpRequestContext);
        User authUser = injectable.getValue(httpContext);
        authUser.setUserRole(null);
        assertEquals("The AccessKeys are not equals", user.getUsername(), authUser.getUsername());
        Response resp = userService.getAll(authUser);
        assertEquals(401, resp.getStatus());
    }
    private HttpEntity getEntity(String data){
        BasicHttpEntity httpEntity  = new BasicHttpEntity();
        byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
        httpEntity.setContent(new ByteArrayInputStream(bytes));
        httpEntity.setContentLength(bytes.length);
        return httpEntity;
    }

    private HttpRequestContext getRequestContext(String authHeader,  HttpEntity entity, String body,
                                                 HashMap<String, String>  requestBody,
                                                 MultivaluedMap<String, String> queryParams){
        return getRequestContext(authHeader, entity, body, requestBody, queryParams, false);
    }
    private HttpRequestContext getRequestContext(String authHeader,  HttpEntity entity, String body,
                                                 HashMap<String, String>  requestBody,
                                                 MultivaluedMap<String, String> queryParams,
                                                 boolean disableAuth){
        HttpRequestContext context = Mockito.mock(HttpRequestContext.class);

        when(context.getHeaderValue(eq(HttpHeaders.AUTHORIZATION))).thenReturn(authHeader);
        when(context.getHeaderValue(eq(HttpHeaders.ACCEPT_LANGUAGE))).thenReturn("te-IN");
        when(context.getHeaderValue(eq(HttpHeaders.USER_AGENT))).thenReturn("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
        when(context.getHeaderValue(eq(HttpHeaders.AUTHORIZATION))).thenReturn(authHeader);

        if(disableAuth)
             when(context.getHeaderValue(eq("X-E8-Disable-API-Auth"))).thenReturn("true");

        if(entity!=null) {
            when(context.getEntity(HttpEntity.class)).thenReturn(entity);
        }
        if(body!=null){
            when(context.getEntity(String.class)).thenReturn(body);
        }
        if(requestBody!=null){
            when(context.getEntity(HashMap.class)).thenReturn(requestBody);
        }
        if(queryParams!=null) {
            when(context.getQueryParameters()).thenReturn(queryParams);
        }
        return context;
    }
    private HttpContext getContext(HttpRequestContext requestContext){
        HttpContext context = Mockito.mock(HttpContext.class);
        when(context.getRequest()).thenReturn(requestContext);
        return context;
    }
    private String getJwtToken(String accessSecret, String payload ) throws Exception{
        SecurityTokenConfiguration secTokenConf = configuration.getSecurityToken();
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setPayload(payload);
        jwtBuilder.signWith(secTokenConf.getSignatureAlgorithm(), accessSecret.getBytes("US-ASCII"));
        String token = jwtBuilder.compact();
        Jws<Claims> jws = Jwts.parser()
                .setSigningKey(accessSecret.getBytes(Charset.forName("US-ASCII")))
                .parseClaimsJws(token);
        return token;
    }
    private HttpHeaders getHeaders(String username, String password) throws Exception{
        return getHeaders(username, password, false);
    }
    private HttpHeaders getHeaders(String username, String password, boolean disableAuth) throws Exception{
        HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);
        String authHeader = new String(Base64.encode(username+":"+password), "US-ASCII");
        List<String> list = new ArrayList<>();
        list.add(authHeader);
        when(httpHeaders.getRequestHeader(eq(HttpHeaders.AUTHORIZATION))).thenReturn(list);
        if(disableAuth) {
             List<String> alist = new ArrayList<>();
             alist.add("true");
             when(httpHeaders.getRequestHeader(eq("X-E8-Disable-API-Auth"))).thenReturn(alist);
        }
        return httpHeaders;
    }
}
