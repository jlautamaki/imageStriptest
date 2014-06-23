package com.example.imagestriptest;

import javax.servlet.annotation.WebServlet;
import com.dropbox.core.*;

import org.vaadin.cssinject.CSSInject;
import org.vaadin.peter.imagestrip.ImageStrip;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;


@SuppressWarnings("serial")
@Theme("mobiletheme")
//@Theme("imagestriptest")
public class ImagestriptestUI extends UI implements ValueChangeListener {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ImagestriptestUI.class, widgetset = "com.example.imagestriptest.widgetset.ImagestriptestWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	/*CSSinject
	<dependency>
	   <groupId>org.vaadin.addons</groupId>
	   <artifactId>cssinject</artifactId>
	   <version>2.0.3</version>
	</dependency>
	
	<dependency org="org.vaadin.addons" name="cssinject" rev="2.0.3" />

	touchkit
	<dependency>
   		<groupId>com.vaadin.addon</groupId>
   		<artifactId>vaadin-touchkit-agpl</artifactId>
   		<version>4.0.0.beta1</version>
	</dependency>
	
	<dependency org="com.vaadin.addon" name="vaadin-touchkit-agpl" rev="4.0.0.beta1" />
	
	
	dropbox api
	<dependency>
		<groupId>com.dropbox.core</groupId>
		<artifactId>dropbox-core-sdk</artifactId>
		<version>1.7.6</version>
	</dependency>
	
	<dependency org="com.dropbox.core" name="dropbox-core-sdk" rev="1.7.6"/>	
	*/
    private static String[] urls = {
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
    };
	
	@Override
	protected void init(VaadinRequest request) {

		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);
		injectStyles();		
		this.setStyleName("mainWindow");
		
        int imgSize=140;
		int height = getUI().getPage().getWebBrowser().getScreenHeight();
		int nmbrOfStrips = height/(imgSize+10);
		for(int i=0; i<nmbrOfStrips;i++){
			layout.addComponent(createStrip(imgSize));			
		}
	}

	private void injectStyles() {
		CSSInject css = new CSSInject(getUI());
		css.setStyles(".mainWindow {background-color: #000000;} "
				+ ".metadataViewer {background-color: #ffffff; opacity: 0.8;}");
	}

	private Component createStrip(int imgSize) {
	     // Create new horizontally aligned strip of images
        ImageStrip strip = new ImageStrip();

        // Add ValueChangeListener to listen for image selection
        strip.addValueChangeListener(this);

        // Use animation
        strip.setAnimated(true);

        // Make strip to behave like select
        strip.setSelectable(true);
        
        // Set size of the box surrounding the images
        strip.setImageBoxWidth(imgSize);
        strip.setImageBoxHeight(imgSize);

        // Set maximum size of the images
        strip.setImageMaxWidth(imgSize);
        strip.setImageMaxHeight(imgSize);
        
        strip.setHeight(imgSize+10, UNITS_PIXELS);
        
        // Limit how many images are visible at most simultaneously
        //strip.setMaxAllowed(6);

//        urls = MyUtil.getUrlsFromDropbox();
        if (urls != null){
	        for(String url:urls){
	            addImage(strip, url,imgSize);        	
	        }
        }
		return strip;
	}
	
	private void addImage(ImageStrip strip, String url,int imgSize){
		String filename=MyUtil.getFilename(url);
		String filenamecropped=MyUtil.getCroppedFilename(url);

        FileResource res = MyUtil.getCroppedFile(url,filename,filenamecropped,imgSize);
        
        // Add few images to the strip using different methods
        strip.addImage(res);		
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		Property property = event.getProperty();
		ImageStrip.Image value = (ImageStrip.Image) property.getValue();
		int index = value.getImageIndex();
		
		ImageViewer viewer = new ImageViewer(urls[index]);
		viewer.center();
		this.addWindow(viewer);
	}

}