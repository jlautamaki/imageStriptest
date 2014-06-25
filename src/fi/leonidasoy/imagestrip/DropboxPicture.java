package fi.leonidasoy.imagestrip;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import fi.leonidasoy.imagestrip.Picture;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Santtu Pajukanta, Leonidas Oy
 */
public class DropboxPicture implements Picture {
    private URI original;

    DropboxPicture(DbxClient client, DbxEntry dbxPicture) throws DbxException {
        loadPicture(client, dbxPicture);
    }

    @Override
    public URI getOriginal() {
        return original;
    }

    @Override
    public String toString() {
        return "DropboxPicture(" + getOriginal().toString() + ")";
    }

    private static String previewUrlToContentUrl(String previewUrl) {
        return previewUrl.replace("www.dropbox.com", "dl.dropboxusercontent.com");
    }

    private void loadPicture(DbxClient client, DbxEntry dbxPicture) throws DbxException {
        String previewUrl = client.createShareableUrl(dbxPicture.path);
        String contentUrl = previewUrlToContentUrl(previewUrl);

        try {
            original = new URI(contentUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Dropbox returned an invalid shareable URL: " + previewUrl);
        }
    }
}
