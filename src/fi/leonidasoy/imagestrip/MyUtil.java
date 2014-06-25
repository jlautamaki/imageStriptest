package fi.leonidasoy.imagestrip;

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

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.vaadin.server.FileResource;

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

	
	static public FileResource getCroppedFile(String url, String filename, String filenamecropped,int imgSize) {
    	File file = downloadFile(url,filename);
        return cropAndResizeFile(file,filenamecropped,imgSize,true);
	}

	static public FileResource getScaledFile(String url, String filename, String filenamescaled,int imgSize) {
    	File file = downloadFile(url,filename);
        return cropAndResizeFile(file,filenamescaled,imgSize,false);
	}

	/*
	<dependency>
		<groupId>commons-io</groupId>
		<artifactId>commons-io</artifactId>
		<version>2.4</version>
	</dependency>

	<dependency org="commons-io" name="commons-io" rev="2.4"/>*/

	static public File downloadFile(String urlString, String filename) {
		URL url;
		Path path = Paths.get(filename);
		boolean notexists = Files.notExists(path);
		File destination = new File(filename);
		if (notexists) {
			try {
				url = new URL(urlString);
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
		
		String[] urls = list.toArray(new String[] {});
/*		String[] urls = {
	    		"http://cdn2.business2community.com/wp-content/uploads/2013/04/google-.jpg",
	            "http://upload.wikimedia.org/wikipedia/commons/e/ec/Jurvetson_Google_driverless_car_trimmed.jpg",
	            "http://www.picshunger.com/wp-content/uploads/2014/04/summer-31.jpg",
	            "http://fc06.deviantart.net/fs70/i/2013/170/5/2/ivy_and_harley_summer_vacation_by_pennysilver-d69s6wr.jpg",
	            "http://www.uwec.edu/Summer/images/2014-Summer-Session-WEB-Ad-2.jpg",
	            "http://hdwallimg.com/wp-content/uploads/2014/02/Funny-Cat-Night-Wallpaper-HD-.jpg",
	            "http://img2.wikia.nocookie.net/__cb20110424032627/half-life/en/images/a/a1/TRIVIAL_TEST.jpg",
	            "http://braukaiser.com/wiki/images/6/63/Batch_61_iodine_test.jpg",
	            "http://upload.wikimedia.org/wikipedia/commons/thumb/f/f0/Pride_%26_Happy.jpg/682px-Pride_%26_Happy.jpg",
	            "http://img3.wikia.nocookie.net/__cb20120709013602/fantendo/images/a/ab/The-Mario-Bros-mario-and-luigi-9298164-1955-2560.jpg",
	            "http://www.mariowiki.com/images/thumb/b/bf/BrawlWario.jpg/220px-BrawlWario.jpg",
	            "http://snowbrains.com/wp-content/uploads/2014/01/url-2.jpeg",
	            "http://images2.visitnsw.com/sites/default/files/galleries/snowboarding-perisher.jpg",
	            "http://upload.wikimedia.org/wikipedia/commons/9/96/Midsummer_bonfire_in_Pielavasi,_Finland.JPG", 
	    };*/
		

/*        DbxRequestConfig config = new DbxRequestConfig(
            "JavaTutorial/1.0", Locale.getDefault().toString());
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, n);

        String accessToken = authFinish.accessToken;
        DbxClient client = new DbxClient(config, accessToken);
        
        DbxEntry.WithChildren listing = client.getMetadataWithChildren("/Jari's%20photos");
        System.out.println("Files in the root path:");
        for (DbxEntry child : listing.children) {
            System.out.println("	" + child.name + ": " + child.toString());
        }
*/      
        //"Jari's%20photos"
		return urls;
	}
}