package fi.leonidasoy.imagestrip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class MetadataViewer extends Panel{
			
	AbsoluteLayout absLayout = new AbsoluteLayout();
	VerticalLayout layout = new VerticalLayout();
	private File file;

    public MetadataViewer(File file) {
    	//style injected at UI-class
    	this.addStyleName("metadataViewer");
    	this.setContent(layout);
    	this.file = file;
    }

	@Override
    public void attach() {
        super.attach();
        
        try {
			Metadata metadata = readMetadata("http://cdn2.business2community.com/wp-content/uploads/2013/04/google-.jpg");
			// iterate through metadata directories
			int i=0;
			for (Directory directory : metadata.getDirectories()) {
			    for (Tag tag : directory.getTags()) {
			    	Label label = new Label(tag.toString());
//			    	Label label = new Label(i+": " +tag.toString());
			    	layout.addComponent(label);
					i++;
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
	}

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
	public Metadata readMetadata(String url) throws MalformedURLException, IOException, JpegProcessingException{				
		return JpegMetadataReader.readMetadata(file);
	}
}