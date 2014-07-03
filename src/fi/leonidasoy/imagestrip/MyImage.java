package fi.leonidasoy.imagestrip;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.vaadin.server.FileResource;

public class MyImage {
	private static MyImage[] images;
	private FileResource fullSizedFile=null;
	private String metadataString=null;
	private final URL url;

	private String getFilename(){		
        return FilenameUtils.getName(url.getFile());		
	}

	private String getCroppedFilename(){
        return "croppedimage"+getFilename();		
	}

	private String getScaledFilename(){
        return "scaledimage"+getFilename();		
	}
	
	public MyImage(URL url) {
		this.url = url;
	}

	//original url of this image
	public URL getUrl() {
		// TODO Auto-generated method stub
		return url;
	}

	public FileResource getFileResource() {
		if (fullSizedFile==null){
			fullSizedFile = new FileResource(MyUtil.downloadFile(getUrl(),this.getFilename()));		
		}
	    return fullSizedFile;
	}
	
	public FileResource getCroppedFileResource(int imgSize) {
        return MyUtil.cropAndResizeFile(getFileResource(),getCroppedFilename(),imgSize,true);
	}

	public FileResource getScaledFileResource(int imgSize) {
        return MyUtil.cropAndResizeFile(this.getFileResource(),getScaledFilename(),imgSize,false);
	}
	
	public static MyImage[] getImages() {
		if (images==null){
			images = getImagesFromDropbox();	
		}
		return images;
	}
	
	private static MyImage[] getImagesFromDropbox() {
        DbxClient client = DropboxService.getClient();
        ArrayList<MyImage> list = new ArrayList<MyImage>();
        DropboxAlbum album;
		try {
			album = new DropboxAlbum(client, "/Jari's photos");
	        for (Picture picture : album.getPictures()) {
	        	System.out.println(picture.toString());
	        	URL url = picture.getOriginal().toURL();
	        	list.add(new MyImage(url));
	        }
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return list.toArray(new MyImage[] {});
	}

	public static MyImage getImage(int index) {
		// TODO Auto-generated method stub
		return images[index];
	}

	public String getMetadata() {
		if (metadataString==null){
			metadataString = MetadataExtractor.getImageLabelText(getFileResource());
		}
		return metadataString;
	}
}
