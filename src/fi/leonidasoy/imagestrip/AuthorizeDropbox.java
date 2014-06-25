package fi.leonidasoy.imagestrip;

import com.dropbox.core.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Get a Dropbox authorization token
 *
 * @author Santtu Pajukanta, Leonidas Oy
 */
public class AuthorizeDropbox {
    private static DbxWebAuthNoRedirect webAuth;

    public static String getAuthorizeUrl() {
        DbxRequestConfig config = DropboxService.getConfig();
        DbxAppInfo appInfo = DropboxService.getAppInfo();

        webAuth = new DbxWebAuthNoRedirect(config, appInfo);

        return webAuth.start();
    }

    public static String getAuthToken(String code) throws DbxException {
        DbxAuthFinish authFinish = webAuth.finish(code);
        return authFinish.accessToken;
    }

    public static void main(String args[]) throws IOException, DbxException {
        String authorizeUrl = getAuthorizeUrl();

        System.out.println("1. Go to: " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Copy the authorization code.");
        String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

        String accessToken = getAuthToken(code);

        System.out.println("Authorization succeeded. Please add the following line to 'dropbox.properties':");
        System.out.println("auth_token=" + accessToken);
    }
}
