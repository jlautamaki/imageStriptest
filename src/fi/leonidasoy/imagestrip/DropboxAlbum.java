package fi.leonidasoy.imagestrip;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import fi.leonidasoy.imagestrip.Album;
import fi.leonidasoy.imagestrip.Picture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Santtu Pajukanta, Leonidas Oy
 */
public class DropboxAlbum implements Album {
    private ArrayList<DropboxPicture> pictures;

    public DropboxAlbum(DbxClient client, String path) throws DbxException {
        loadFolder(client, path);
    }

    @Override
    public Collection<DropboxPicture> getPictures() {
        return pictures;
    }

    @Override
    public Collection<Album> getSubalbums() {
        return Collections.emptyList();
    }

    private void loadFolder(DbxClient client, String path) throws DbxException {
        DbxEntry.WithChildren dbxAlbum = client.getMetadataWithChildren(path);

        pictures = new ArrayList<DropboxPicture>(dbxAlbum.children.size());

        for(DbxEntry dbxPicture : dbxAlbum.children) {
            pictures.add(new DropboxPicture(client, dbxPicture));
        }
    }
}
