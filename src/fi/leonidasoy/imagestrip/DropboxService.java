package fi.leonidasoy.imagestrip;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * Manages a static instance of DbxClient.
 *
 * @author Santtu Pajukanta, Leonidas Oy
 */
public class DropboxService {
    private static Properties properties;
    private static DbxClient client;
    private static DbxRequestConfig config;
    
    public static DbxClient getClient() {
        if (client == null) {
            String authToken = getProperties().getProperty("auth_token");
            if (authToken == null) {
                throw new Error("auth_token is missing from dropbox.properties (run AuthorizeDropbox first)");
            }

            client = new DbxClient(getConfig(), authToken);
        }

        return client;
    }

    private static Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            properties.setProperty("app_key","oepcu1fu2wjz1te");
    		properties.setProperty("app_secret","3trd7xze2g1ks8i");
			properties.setProperty("user_agent","fi.leonidasoy.imagestrip.dropbox/1.0");
			properties.setProperty("auth_token","wrrwr-NKmHcAAAAAAAAABqBgPlWYkt3kI8qpCCf-LVIez4UGLGfbGzv813jDUVs9");            
        }

        return properties;
    }

    static DbxRequestConfig getConfig() {
        if (config == null) {
            config = new DbxRequestConfig(
                    getProperties().getProperty("user_agent"),
                    Locale.getDefault().toString()
            );
        }

        return config;
    }

    static DbxAppInfo getAppInfo() {
        return new DbxAppInfo(
                getProperties().getProperty("app_key"),
                getProperties().getProperty("app_secret")
        );
    }
}
