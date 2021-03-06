package fi.leonidasoy.imagestrip;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;

import java.nio.file.Paths;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Image;

public class MyUtil {
	private static FileSystem fs = Jimfs.newFileSystem(Configuration.unix());

	static public Path getPath(String filename){
		//true is not working perfectly :(
		boolean inmemory = false;
		if (inmemory ){
			return fs.getPath(filename);
		}else{
			return Paths.get(filename);
		}
	}
	
	
	/*
	 * <dependency> <groupId>commons-io</groupId>
	 * <artifactId>commons-io</artifactId> <version>2.4</version> </dependency>
	 * 
	 * <dependency org="commons-io" name="commons-io" rev="2.4"/>
	 */

	static public File downloadFile(URL source, String filename) {
		Path path = getPath(filename);
		File destination = new File(filename);
		System.out.println("Filename: " + filename + " @: " + path.toAbsolutePath());
		if (Files.notExists(path)) {
			try {
				System.out.println("Downloading " + source);
				org.apache.commons.io.FileUtils.copyURLToFile(source,
						destination);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return destination;
	}

	/*
	 * 
	 * imgscalr - A Java Image Scaling Library: <dependency>
	 * <groupId>org.imgscalr</groupId> <artifactId>imgscalr-lib</artifactId>
	 * <version>4.2</version> </dependency>
	 * 
	 * <dependency org="org.imgscalr" name="imgscalr-lib" rev="4.2"/>
	 */
	static public FileResource cropAndResizeFile(FileResource fileResource,
			String filename, int imgSize, boolean crop) {
		try {
			Path path = getPath(filename);
			File destination = new File(filename);
			if (Files.notExists(path)) {
				BufferedImage in = ImageIO.read(fileResource.getSourceFile());
				double scaleValue = calculateScaling(in.getHeight(),
						in.getWidth(), imgSize);

				int newWidth = (int) Math.ceil(in.getWidth() * scaleValue);
				int newHeight = (int) Math.ceil(in.getHeight() * scaleValue);

				BufferedImage imgout = Scalr.resize(in,
						Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC,
						newWidth, newHeight, Scalr.OP_ANTIALIAS);

				RenderedImage imgout2;
				if (crop) {
					imgout2 = Scalr.crop(imgout, (newWidth - imgSize) / 2,
							(newHeight - imgSize) / 2, imgSize, imgSize);
					ImageIO.write(imgout2, "jpg", destination);
				} else {
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

	static public double calculateScaling(int height, int width, int imgSize) {
		int smaller = height;
		if (width < height) {
			smaller = width;
		}
		double value = (double) imgSize / smaller;
		return value;
	}

	public static Image getImage(URL source) {
		String filename = FilenameUtils.getName(source.getFile());
		File file = downloadFile(source, filename);
		FileResource resource = new FileResource(file);
		return new Image("", resource);
	}
}