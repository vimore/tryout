package com.securityx.health.agent;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;
import org.knowm.dropwizard.sundial.SundialConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HealthAgentConfiguration extends Configuration {

    @JsonProperty("template")
    @NotEmpty
    public String template;

    @JsonProperty("defaultName")
    @NotEmpty
    public String defaultName;

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public static class PushGateway extends ClusterInfo{

        @JsonProperty("proxy_host")
        public String proxyHost;

        @JsonProperty("proxy_port")
        public String proxyPort;

        @JsonProperty("proxy_user")
        public String proxyUser;

        @JsonProperty("proxy_pass")
        public String proxyPass;

        public String getProxyHost() {
            return proxyHost;
        }

        public void setProxyHost(String proxyHost) {
            this.proxyHost = proxyHost;
        }

        public int getProxyPort() {
            return Integer.valueOf(proxyPort);
        }

        public void setProxyPort(String proxyPort) {
            this.proxyPort = proxyPort;
        }

        public String getProxyUser() {
            return proxyUser;
        }

        public void setProxyUser(String proxyUser) {
            this.proxyUser = proxyUser;
        }

        public String getProxyPass() {
            return proxyPass;
        }

        public void setProxyPass(String proxyPass) {
            this.proxyPass = proxyPass;
        }


        public String getProxyHost(HealthAgentConfiguration configuration) throws Exception{
            return getItem(configuration, proxyHost);
        }

        public int getProxyPort(HealthAgentConfiguration configuration) throws Exception{
            return Integer.valueOf(getItem(configuration, proxyPort));
        }

        public String getProxyUser(HealthAgentConfiguration configuration) throws Exception{
            return getItem(configuration, proxyUser);
        }
        public String getProxyPass(HealthAgentConfiguration configuration) throws Exception{
            return getItem(configuration, proxyPass);
        }
    }

    @JsonProperty("push_gateways")
    @Valid
    @NotNull
    public PushGateway pushGateway = new PushGateway();

    public PushGateway getPushGateway() {
        return pushGateway;
    }

    public void setPushGateway(PushGateway pushGateway) {
        this.pushGateway = pushGateway;
    }

    @JsonProperty("cluster_info")
    @Valid
    @NotNull
    public ClusterInfo clusterInfo = new ClusterInfo();

    public ClusterInfo getClusterInfo() {
        return clusterInfo;
    }

    public void setClusterInfo(ClusterInfo clusterInfo) {
        this.clusterInfo = clusterInfo;
    }

    public static class ClusterInfo{
        @JsonProperty("hosts")
        public Host[] hosts;

        @JsonProperty("tag")
        public String tag;

        @JsonProperty("track.directory")
        public String trackDirectory;

        public Host[] getHosts() {
            return hosts;
        }

        public void setHosts(Host[] hosts) {
            this.hosts = hosts;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTrackDirectory() {
            return trackDirectory;
        }

        public void setTrackDirectory(String trackDirectory) {
            this.trackDirectory = trackDirectory;
        }
    }
    public static class Host{
        @JsonProperty("type")
        @NotNull
        public String type;

        @JsonProperty("scheme")
        @NotNull
        public String scheme="http";

        @JsonProperty("host")
        @NotNull
        public String host;

        @JsonProperty("port")
        public int port=80;

        @JsonProperty("path")
        public String path="";

        @JsonProperty("query")
        public String query="";

        @JsonProperty("user")
        public String user="";

        @JsonProperty("pass")
        @DefaultValue("")
        public String password;

        @JsonProperty("directory")
        public String directory = "/tmp/e8sec-api-sever/metrics";

        @JsonProperty("headers")
        public List<HashMap<String, String>> headers =  new ArrayList<>();

        @JsonProperty("filter_pattern")
        public String filterPattern;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getUser() {
            return user;
        }

        public String getUser(HealthAgentConfiguration configuration) throws Exception{
            return getItem(configuration, user);
        }
        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public String getPassword(HealthAgentConfiguration configuration) throws Exception{
            return getItem(configuration, password);
        }
        public void setPassword(String password) {
            this.password = password;
        }

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }

        public String getScheme() {
            return scheme;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }

        public List<HashMap<String, String>> getHeaders() {
            return headers;
        }

        public void setHeaders(List<HashMap<String, String>> headers) {
            this.headers = headers;
        }

        public String getFilterPattern() {
            return filterPattern;
        }

        public void setFilterPattern(String filterPattern) {
            this.filterPattern = filterPattern;
        }

        @Override
        public String toString(){
            return String.format("type: %s, scheme: %s, host: %s, port: %s, path: %s, user: %s, password: %s, directory: %s",
                    type,  scheme, host, port, path, user, password, directory);
        }
    }

    @Valid
    @NotNull
    public SundialConfiguration sundialConfiguration = new SundialConfiguration();

    @JsonProperty("sundial")
    public SundialConfiguration getSundialConfiguration() {

        return sundialConfiguration;
    }

    // Cloudchamber configuration section.
    @Valid
    @NotNull
    @JsonProperty
    private CloudChamberConfiguration cloudchamber = new CloudChamberConfiguration();

    @JsonProperty
    public CloudChamberConfiguration getcloudchamber() { return cloudchamber; }

    @JsonProperty
    public void setCloudchamber(CloudChamberConfiguration cloudchamber) { this.cloudchamber = cloudchamber; }

    public static String getItem(Object obj, String key) throws Exception{
        if(key.matches("%.*?%")){
            return getKeyedItemByReflection(obj, key);
        }
        return key;
    }
    public static String getKeyedItemByReflection(Object obj,String key) throws Exception{
        key =  key.replaceAll("%", "");
        String[] strs =key.split("\\.");
        Object cur = obj;
        for(String str : strs){
            Method m = cur.getClass().getDeclaredMethod("get"+str);
            cur = m.invoke(cur);
        }
        return cur.toString();
    }
    public void fixUpCloudChamberConfig() throws Exception{
        HealthAgentConfiguration configuration = this;
        HealthAgentConfiguration.PushGateway pushGateway = configuration.getPushGateway();
        String proxyHost = pushGateway.getProxyHost(configuration);
        pushGateway.setProxyHost(proxyHost);
        int proxyPort = pushGateway.getProxyPort(configuration);
        pushGateway.setProxyPort(Integer.toString(proxyPort));
        String proxyUser = pushGateway.getProxyUser(configuration);
        pushGateway.setProxyUser(proxyUser);
        String proxyPass = pushGateway.getProxyPass(configuration);
        pushGateway.setProxyPass(proxyPass);
        HealthAgentConfiguration.Host[] hosts = pushGateway.getHosts();
        for(HealthAgentConfiguration.Host host: hosts){
            switch (host.getType()){
                case "cloud-chamber":
                    List<HashMap<String, String>> headers = host.getHeaders();
                    for(HashMap<String, String> map: headers){
                        String value = map.get("value");
                        String actValue = HealthAgentConfiguration.getItem(configuration, value);
                        map.put("value", actValue);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
