package com.securityx.modelfeature;


import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.Credentials;
import org.apache.http.client.HttpClient;
import org.apache.http.auth.*;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider; 
import org.apache.http.impl.client.AbstractHttpClient; 
import org.apache.http.params.HttpParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.HttpHost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by harish on 1/21/15.
 */

public class HttpClientWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientWrapper.class);


    /**
     *
     * @param base
     * @param proxyScheme
     * @param proxyHost
     * @param proxyPort
     * @param proxyUser
     * @param proxyPassword
     * @return
     */
    public static HttpClient wrapCloudChamberClient(HttpClient base, String proxyScheme,
                                    String proxyHost, int proxyPort, String proxyUser, String proxyPassword) {
        try {

            //create a trust Manager
            X509TrustManager tm = new X509TrustManager() {

                // TODO: Accept valid certificates. Implement these methods and throw exception when certificate Invalid.
                // if a certificate is invalid, checkClientTrusted() and checkServerTrusted()
                // is supposed to throw a CertificateException .
                //Currently, accept all certificates, never throw an exception.
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };


            // SSLSocketFactory
            //first, get the ssl context
            SSLContext ctx = SSLContext.getInstance("TLS");

            //initialize SSL context context with the TrustManager
            ctx.init(null, new TrustManager[]{tm}, null);

            //SSLSocketFactory
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);

            // Register SSLSocketFactory with input HttpClient
            ClientConnectionManager ccm = base.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();

            //protocol https, default port 443
            sr.register(new Scheme("https", ssf, 443));

            // Setup proxy host, port, auth if specified 
            if (proxyHost != null && !proxyHost.isEmpty()) {
               base.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxyHost, proxyPort, proxyScheme));
               LOGGER.debug("PROXY_HOST:<" + proxyHost+">, PROXY_PORT:<"+proxyPort+">, ProxyScheme:<"+proxyScheme+">");
            }

            if (proxyUser != null && !proxyUser.isEmpty()) {
                ((AbstractHttpClient)base).getCredentialsProvider().setCredentials(
                        new AuthScope(proxyHost, proxyPort),
                        new UsernamePasswordCredentials(proxyUser, proxyPassword));
               LOGGER.debug("PROXY_USER:<" + proxyUser+">");
            }
             
            return new DefaultHttpClient(ccm, base.getParams());

        } catch (Exception e) {
            LOGGER.error("Error getting HttpClient for CloudChamber => " + e);
            return null;
        }
    }
}
