package fi.leonidasoy.imagestrip;

import com.dropbox.core.*;

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
}
