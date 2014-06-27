package fi.leonidasoy.imagestrip;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Rotation;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Image;

public class MyUtil {
	static int tmp = 4;
	static public String getFilename(String url){
        int hash = url.hashCode()+tmp;
        return "image"+hash+".jpg";		
	}

	static public String getCroppedFilename(String url){
        int hash = url.hashCode()+tmp;
        return "croppedimage"+hash+".jpg";		
	}

	static public String getScaledFilename(String url){
        int hash = url.hashCode()+tmp;
        return "scaledimage"+hash+".jpg";		
	}

	static public FileResource getFileResource(String url) {
		File file = downloadFile(url);
        return new FileResource(file);
	}
	
	static public FileResource getCroppedFile(String url, int imgSize) {
    	File file = downloadFile(url);
        return cropAndResizeFile(file,getCroppedFilename(url),imgSize,true);
	}

	static public FileResource getScaledFile(String url, int imgSize) {
    	File file = downloadFile(url);
        return cropAndResizeFile(file,getScaledFilename(url),imgSize,false);
	}

	/*
	<dependency>
		<groupId>commons-io</groupId>
		<artifactId>commons-io</artifactId>
		<version>2.4</version>
	</dependency>

	<dependency org="commons-io" name="commons-io" rev="2.4"/>*/

	static public File downloadFile(String urlString) {
		String filename = getFilename(urlString);
		Path path = Paths.get(filename);
		boolean notexists = Files.notExists(path);
		File destination = new File(filename);
		if (notexists) {
			try {
				URL url = new URL(urlString);
				System.out.println("Downloading " + url);
				org.apache.commons.io.FileUtils.copyURLToFile(url, destination);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return destination;
	}

	/*
	 
	imgscalr - A Java Image Scaling Library:
	<dependency>
		<groupId>org.imgscalr</groupId>
		<artifactId>imgscalr-lib</artifactId>
		<version>4.2</version>
	</dependency>
 
	 <dependency org="org.imgscalr" name="imgscalr-lib" rev="4.2"/>	 
	 */
	static public FileResource cropAndResizeFile(File img, String filename,int imgSize,boolean crop) {
		try {
			Path path = Paths.get(filename);
			boolean notexists = Files.notExists(path);
			File destination = new File(filename);
			if (notexists) {
				BufferedImage in = ImageIO.read(img);
		        double scaleValue = calculateScaling(in.getHeight(),in.getWidth(),imgSize);        
	
		        int newWidth = (int) Math.ceil(in.getWidth()*scaleValue);
		        int newHeight = (int) Math.ceil(in.getHeight()*scaleValue);
		        
		        BufferedImage imgout = Scalr.resize(in, Scalr.Method.QUALITY, Scalr.Mode.FIT_EXACT,
		        		newWidth, newHeight, Scalr.OP_ANTIALIAS);        

		        RenderedImage imgout2;
				if (crop){
			        imgout2 = Scalr.crop(imgout, (newWidth-imgSize)/2,(newHeight-imgSize)/2, imgSize, imgSize);		        	
			        ImageIO.write(imgout2, "jpg", destination);	        
		        }else{
			        ImageIO.write(imgout, "jpg", destination);	        		        	
		        }

			}
	        return new FileResource(destination);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

/*	public static File rotate(File file, Rotation rotation){
		File destination = new File(file.getName()+"mod.jpg");
		BufferedImage in;
		try {
	        int newWidth = 50;
	        
			in = ImageIO.read(file);
			BufferedImage imgout2 = Scalr.rotate(in,rotation,Scalr.OP_ANTIALIAS);			
	        ImageIO.write(imgout2, "jpg", destination);	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return destination;
	}
*/
	
	static public double calculateScaling(int height, int width,int imgSize) {
		int smaller = height;
		if (width<height){
			smaller = width;
		}
		double value = (double) imgSize/smaller; 
		return value;
	}

	public static String[] getUrlsFromDropbox() {
        DbxClient client = DropboxService.getClient();
        ArrayList<String> list = new ArrayList<String>();
        DropboxAlbum album;
		try {
			album = new DropboxAlbum(client, "/Jari's photos");
	        for (Picture picture : album.getPictures()) {
	        	System.out.println(picture.toString());
	            list.add(picture.getOriginal().toString());
	        }
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return list.toArray(new String[] {});
	}

	
    public static BufferedImage rotate(BufferedImage image, int degrees) {
        int w = image.getWidth();
        int h = image.getHeight();

        // Enough space for the image
        int nd = (int) Math.ceil(Math.sqrt(w * w + h * h)) + 2;

        BufferedImage t = new BufferedImage(nd, nd, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = t.createGraphics();
        g.rotate(Math.toRadians(degrees), nd / 2, nd / 2);
        g.translate(nd / 2 - w / 2, nd / 2 - h / 2);

        g.drawImage(image, 0, 0, null);
        g.dispose();

        // Crop the invisible parts
        return t;
    }
	
	public static Image getImage(String url) {
		File file = downloadFile(url);
		FileResource resource = new FileResource(file);
		return new Image("",resource);
	}
}