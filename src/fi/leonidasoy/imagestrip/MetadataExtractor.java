package fi.leonidasoy.imagestrip;

import java.io.IOException;
import java.net.MalformedURLException;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.vaadin.server.FileResource;

public class MetadataExtractor{
	/*
	  	http://mvnrepository.com/artifact/com.drewnoakes/metadata-extractor/2.6.2
		Maven:
	  		<dependency>	
		  		<groupId>com.drewnoakes</groupId>
				<artifactId>metadata-extractor</artifactId>
				<version>2.6.2</version>
			</dependency>
		Ivy:
			<dependency org="com.drewnoakes" name="metadata-extractor" rev="2.6.2"/>
	 */
	private static Metadata readMetadata(FileResource fileResource) throws MalformedURLException, IOException, JpegProcessingException{				
		return JpegMetadataReader.readMetadata(fileResource.getSourceFile());
	}

	public static String getImageLabelText(FileResource fileResource) {
		String txt = "";
		boolean first = true;
		try {
			Metadata metadata = readMetadata(fileResource);
			// iterate through metadata directories
			for (Directory directory : metadata.getDirectories()) {
			    for (Tag tag : directory.getTags()) {
			    	String tagString = tag.toString();
			    	if (filterTagString(tagString)){
			    		if (first){
					    	first=false;			    			
			    		}else{
			    			txt += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			    		}
			    		txt += tag.toString().replace("[Exif SubIFD] ", "").replace("[Exif IFD0] ", "");
			    	}
			    }
			}
		} catch (JpegProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return txt;
	}
	/*
	[Exif IFD0] Artist - Jari Leino
	[Exif SubIFD] Exposure Time - 1/800 sec
	[Exif SubIFD] F-Number - F2.8
	[Exif SubIFD] Focal Length - 50.0 mm
	[Exif SubIFD] White Balance Mode - Manual white balance

	Otsikot ovat: Artist: <esim. Jari Leino>
	Shutter speed: <näytä pelkkä data. esim 1/800 sec>
	Aperture value: <esim F2.8>
	Focal Length: <esim 50.0 mm>
	White Balance: <esim. Manual white balance>*/
	private static boolean filterTagString(String tagString) {
		if (tagString.contains("Artist")||
			tagString.contains("Exposure Time")||
			tagString.contains("Focal Length")||
			tagString.contains("F-Number")||
			tagString.contains("White Balance Mode")){
			return true;
		}
		return false;
	}
}