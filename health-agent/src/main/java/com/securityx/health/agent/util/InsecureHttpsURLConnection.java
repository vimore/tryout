package com.securityx.health.agent.util;

import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;


public class InsecureHttpsURLConnection {
    private static SSLSocketFactory sslSocketFactory = null;
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(InsecureHttpsURLConnection.class);

    public static void request(URL url, boolean enableCertCheck) throws Exception {
        BufferedReader reader = null;
        // Repeat several times to check persistence.
        LOGGER.info("Cert checking=[{}]",(enableCertCheck?"enabled":"disabled"));
        for (int i = 0; i < 5; ++i) {
            try {

                HttpURLConnection httpConnection = (HttpsURLConnection) url.openConnection();

                // Normally, instanceof would also be used to check the type.
                if( ! enableCertCheck ) {
                    setAcceptAllVerifier((HttpsURLConnection)httpConnection);
                }

                reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()), 1);

                char[] buf = new char[1024];
                StringBuilder sb = new StringBuilder();
                int count;
                while( -1 < (count = reader.read(buf)) ) {
                    sb.append(buf, 0, count);
                }
                LOGGER.debug(sb.toString());

                reader.close();

            } catch (IOException ex) {
                LOGGER.error("failed to connect {}. Error: {}",url, ex.getMessage(), ex);
                if( null != reader ) {
                    reader.close();
                }
            }
        }
    }

    /**
     * Overrides the SSL TrustManager and HostnameVerifier to allow
     * all certs and hostnames.
     * WARNING: This should only be used for testing, or in a "safe" (i.e. firewalled)
     * environment.
     *
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static void setAcceptAllVerifier(HttpsURLConnection connection)
            throws NoSuchAlgorithmException, KeyManagementException {

        // Create the socket factory.
        // Reusing the same socket factory allows sockets to be
        // reused, supporting persistent connections.
        if( null == sslSocketFactory) {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, ALL_TRUSTING_TRUST_MANAGER, new java.security.SecureRandom());
            sslSocketFactory = sc.getSocketFactory();
        }

        connection.setSSLSocketFactory(sslSocketFactory);

        // Since we may be using a cert with a different name, we need to ignore
        // the hostname as well.
        connection.setHostnameVerifier(ALL_TRUSTING_HOSTNAME_VERIFIER);
    }

    private static final TrustManager[] ALL_TRUSTING_TRUST_MANAGER = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }
    };

    private static final HostnameVerifier ALL_TRUSTING_HOSTNAME_VERIFIER  = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
