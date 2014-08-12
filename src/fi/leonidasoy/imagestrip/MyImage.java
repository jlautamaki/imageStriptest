package fi.leonidasoy.imagestrip;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.vaadin.server.FileResource;

public class MyImage {
	private static List<MyImage> images;
	private FileResource fullSizedFile=null;
	private String metadataString=null;
	private final URL url;
	private int width = -1;
	private int height = -1;

	static {
		//MyImage.getImages();
	}
	
	private String getFilename(){		
        return FilenameUtils.getName(url.getFile());		
	}

	private String getCroppedFilename(int height){
        return "1croppedimage"+height+getFilename();		
	}

	private String getScaledFilename(int height){
        return "1scaledimage"+height+getFilename();		
	}
	
	public MyImage(URL url) {
		this.url = url;
	}

	//original url of this image
	public URL getUrl() {
		return url;
	}

	public FileResource getFileResource() {
		if (fullSizedFile==null){
			fullSizedFile = new FileResource(MyUtil.downloadFile(getUrl(),this.getFilename()));		
		}
	    return fullSizedFile;
	}
	
	public FileResource getCroppedFileResource(int imgSize) {
        return MyUtil.cropAndResizeFile(getFileResource(),getCroppedFilename(imgSize),imgSize,true);
	}

	public FileResource getScaledFileResource(int imgSize) {
        return MyUtil.cropAndResizeFile(getFileResource(),getScaledFilename(imgSize),imgSize,false);
	}
	
	public static synchronized List<MyImage> getImages() {
		if (images==null){
			images = getImagesFromDropbox();	
		}
		return images;
	}
	
	private static List<MyImage> getImagesFromDropbox() {
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
		return list;
	}

	public synchronized static MyImage getImage(int index) {
		return images.get(index);
	}

	public String getMetadata() {
		if (metadataString==null){
			metadataString = MetadataExtractor.getImageLabelText(getFileResource());
		}
		return metadataString;
	}

	public int getHeight() {
		if (height  == -1){
			initWidthAndheight();
		}
		return height;
	}

	public int getWidth() {
		if (width == -1){
			initWidthAndheight();
		}
		return width;
	}
		
	private void initWidthAndheight() {
		BufferedImage bimg;
		try {
			bimg = ImageIO.read(new File(getFilename()));
			width          = bimg.getWidth();
			height         = bimg.getHeight();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
