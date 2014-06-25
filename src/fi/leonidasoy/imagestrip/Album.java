package fi.leonidasoy.imagestrip;

import java.util.Collection;

/**
 * @author Santtu Pajukanta, Leonidas Oy
 */
public interface Album {
    public Collection<? extends Picture> getPictures();
    public Collection<Album> getSubalbums();
}
