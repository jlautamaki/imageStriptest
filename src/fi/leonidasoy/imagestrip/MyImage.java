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
	private static List<MyImage> images = MyImage.getImages();
	private String metadataString=null;
	private int width = -1;
	private int height = -1;
	private final FileResource fullSizedFileResource;
	private final File file;
	
	private static String getFilename(URL url){		
        
		return FilenameUtils.getName(url.getFile());		
	}

	private String getCroppedFilename(int height){
        return "croppedimage"+height+file.getName();		
	}

	private String getScaledFilename(int height){
        return "scaledimage"+height+file.getName();		
	}
	
	public MyImage(URL url) {
		this(getFile(url));
	}
	
	public MyImage(File imageFile) {
		this.file   = imageFile;
		this.fullSizedFileResource = new FileResource(file);
		this.initWidthAndheight();
	}
	
	private static File getFile(URL url) {
	    return MyUtil.downloadFile(url,getFilename(url));
	}

	public FileResource getFileResource() {
	    return fullSizedFileResource;
	}

	
	public FileResource getCroppedFileResource(int imgSize) {
        return MyUtil.cropAndResizeFile(getFileResource(),getCroppedFilename(imgSize),imgSize,true);
	}

	public FileResource getScaledFileResource(int imgSize) {
        return MyUtil.cropAndResizeFile(getFileResource(),getScaledFilename(imgSize),imgSize,false);
	}
	
	public static synchronized List<MyImage> getImages() {
		if (images==null){
        	if (DuplicateRemover.useThisClass){
        		images = DuplicateRemover.getFilteredFiles(0);
        	}else{
    			images = getImagesFromDropbox();	
            }
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
		return height;
	}

	public int getWidth() {
		return width;
	}
		
	private void initWidthAndheight() {
		BufferedImage bimg;
		try {
			bimg = ImageIO.read(file);
			width          = bimg.getWidth();
			height         = bimg.getHeight();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
