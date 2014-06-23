package com.example.imagestriptest;

import java.awt.image.BufferedImage;
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
import com.dropbox.core.DbxDelta.Entry;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.vaadin.server.FileResource;

public class MyUtil {
	static public String getFilename(String url){
        int hash = url.hashCode();
        return "image-"+hash+".jpg";		
	}

	static public String getCroppedFilename(String url){
        int hash = url.hashCode();
        return "croppedimage-"+hash+".jpg";		
	}
		
	static public FileResource getCroppedFile(String url, String filename, String filenamecropped,int imgSize) {
    	File file = downloadFile(url,filename);
        return cropAndResizeFile(file,filenamecropped,imgSize);
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
	static public FileResource cropAndResizeFile(File img, String filenamecropped,int imgSize) {
		try {
			Path path = Paths.get(filenamecropped);
			boolean notexists = Files.notExists(path);
			File destination = new File(filenamecropped);
			if (notexists) {
				BufferedImage in = ImageIO.read(img);
		        double scaleValue = calculateScaling(in.getHeight(),in.getWidth(),imgSize);        
	
		        int newWidth = (int) Math.ceil(in.getWidth()*scaleValue);
		        int newHeight = (int) Math.ceil(in.getHeight()*scaleValue);
		        
		        BufferedImage resized = Scalr.resize(in, Scalr.Method.QUALITY, Scalr.Mode.FIT_EXACT,
		        		newWidth, newHeight, Scalr.OP_ANTIALIAS);        
	
		        BufferedImage cropped = Scalr.crop(resized, (newWidth-imgSize)/2,(newHeight-imgSize)/2, imgSize, imgSize);
		        ImageIO.write(cropped, "jpg", destination);	        
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

//	public static String[] getUrlsFromDropbox() {
//	}
}