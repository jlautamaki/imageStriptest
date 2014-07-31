package fi.leonidasoy.imagestrip;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.vaadin.server.FileResource;
import com.vaadin.ui.UI;

public class MyImage {
	private static MyImage[] images;
	private FileResource fullSizedFile=null;
	private String metadataString=null;
	private final URL url;
	private int width = -1;
	private int height = -1;

	private String getFilename(){		
        return FilenameUtils.getName(url.getFile());		
	}

	private String getCroppedFilename(int height){
        return "croppedimage"+height+getFilename();		
	}

	private String getScaledFilename(int height){
        return "scaledimage"+height+getFilename();		
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
        return MyUtil.cropAndResizeFile(getFileResource(),getCroppedFilename(imgSize),imgSize,true);
	}

	public FileResource getScaledFileResource(int imgSize) {
        return MyUtil.cropAndResizeFile(this.getFileResource(),getScaledFilename(imgSize),imgSize,false);
	}
	
	public static MyImage[] getImages(ProgressBarLayout progressBar, UI ui) {
		if (images==null){
			images = getImagesFromDropbox(progressBar, ui);	
		}
		return images;
	}
	
	private static MyImage[] getImagesFromDropbox(ProgressBarLayout progressBar, UI ui) {
        DbxClient client = DropboxService.getClient();
        ArrayList<MyImage> list = new ArrayList<MyImage>();
        DropboxAlbum album;
		try {
			album = new DropboxAlbum(client, "/Jari's photos");
			progressBar.setValue("Found " +album.getPictures().size() + "pictures.",0.4f);

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
