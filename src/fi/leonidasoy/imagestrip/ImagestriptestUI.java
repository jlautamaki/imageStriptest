package fi.leonidasoy.imagestrip;

import javax.servlet.annotation.WebServlet;

import java.io.File;
import java.util.ArrayList;
import org.vaadin.cssinject.CSSInject;
import org.vaadin.peter.imagestrip.ImageStrip;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;


@SuppressWarnings("serial")
@Theme("mobiletheme")
//@Theme("imagestriptest")
public class ImagestriptestUI extends UI implements ValueChangeListener {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ImagestriptestUI.class, widgetset = "fi.leonidasoy.imagestrip.widgetset.ImagestriptestWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	private static String[] urls = MyUtil.getUrlsFromDropbox();

	/*
	imagestrip
	<dependency>
   		<groupId>org.vaadin.addons</groupId>
   		<artifactId>imagestrip</artifactId>
   		<version>3.0</version>
	</dependency>
	 
	<dependency org="org.vaadin.addons" name="imagestrip" rev="3.0" /> 
	 
	CSSinject 
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

	
	final VerticalLayout sourceLayout = new VerticalLayout();
	final VerticalLayout imgLayout = new VerticalLayout();

	final AbsoluteLayout absLayout = new AbsoluteLayout();
	final GridLayout layout = new GridLayout();

	private boolean metadataVisible = false;
	final private Button metadatabutton = new Button("Show metadata");
	private Button dbbutton = new Button("DB");
	private Button fbbutton = new Button("FB");

	private Image image;

	int imgSize=140;

	private MetadataViewer metadatawindow;

	private int imgindex=0;

	private ImageStrip bigStrip;

	private ImageStrip smallStrip;

	private ArrayList<org.vaadin.peter.imagestrip.ImageStrip.Image> bigStripImageList = new ArrayList<org.vaadin.peter.imagestrip.ImageStrip.Image>();

	@Override
	protected void init(VaadinRequest request) {
		initButtons();		
		
		injectStyles();		
		this.setStyleName("mainWindow");
		
		setContent(layout);

		sourceLayout.setWidth("100px");
		sourceLayout.addComponent(dbbutton);
		sourceLayout.addComponent(fbbutton);

		absLayout.setHeight("100%");
		absLayout.setWidth("100%");
		layout.setSizeFull();
		layout.setMargin(true);
        layout.setRows(2);
        layout.setColumns(3);
        layout.setSpacing(true);
        layout.setColumnExpandRatio(0, 0f);
        layout.setColumnExpandRatio(1, 1f);
        layout.setColumnExpandRatio(2, 0f);
        layout.setRowExpandRatio(0, 1f);
        layout.setRowExpandRatio(1, 0f);
                
        smallStrip = createStrip(imgSize,0,true);
                
        layout.addComponent(sourceLayout, 0, 0);
        layout.addComponent(absLayout, 1, 0);
		layout.addComponent(metadatabutton, 2, 0);
		layout.addComponent(smallStrip,0, 1, 2, 1);
	
		Panel panel = new Panel("");
		panel.setSizeFull(); // Fill the entire given area
		absLayout.addComponent(panel, "left: 0px; right: 0px; "+
		                           "top: 0px; bottom: 0px;");
		imgLayout.setSizeFull();
		bigStrip = createStrip(imgSize*4,1,false);
		imgLayout.addComponent(bigStrip);
		panel.setContent(imgLayout);

//		panel.

	}


	private void initButtons() {
        this.metadatabutton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				if (metadataVisible){
					hideMetadata();			
					metadataVisible=false;
					metadatabutton.setCaption("Show metadata");
				}else{
					showMetadata();					
					metadataVisible=true;
					metadatabutton.setCaption("Hide metadata");
				}
			}
        });
        this.metadatabutton.setWidth("100px");
	}
	

	private ImageStrip.Image getBigStripImage(int indexIn){
		return this.bigStripImageList.get(indexIn);
	}
	
	private void changeImage(int indexIn){
		ImageStrip.Image image = getBigStripImage(indexIn);
		System.out.println(image.getImageIndex());
		bigStrip.setValue(image);
//		bigStrip.
//		int i=0;
//		while(i<1){
//			System.out.println("indexIn = " + indexIn + " compareIndex = " + compareIndex);
//			bigStrip.scrollToRight();
//			compareIndex = getBigStripIndex();
//			i++;
//		}

//		bigStrip.setValue(index);
//        ExternalResource resource = new ExternalResource(url);        
//       	image.setSource(resource);
//       	image.setHeight("100%");
//		image.setWidth("100%");
	}

	protected void hideMetadata() {        
		absLayout.removeComponent(metadatawindow);
	}

	protected void showMetadata() {       
		metadatawindow = new MetadataViewer(new File(MyUtil.getFilename(urls[imgindex])));
		absLayout.addComponent(metadatawindow, "top:0%; left:5%");
		metadatawindow.setHeight(imgSize*4-50 + "px");
		metadatawindow.setWidth("95%");
	}

	
	private void injectStyles() {
		CSSInject css = new CSSInject(getUI());
		css.setStyles(".mainWindow {background-color: #000000;} "
				+ ".metadataViewer {background-color: #ffffff; opacity: 0.8;}"
				+ ".v-layout.v-vertical {background-color: #000000;}"
				+ ".reindeer .v-panel-content, .reindeer .white .v-panel-content {border: 0px solid #000000}"
				+ ".v-imagestrip .selectable .image-border {border-radius: 0px;}"
				+ ".v-imagestrip .horizontal {background-color: #000000;}"
				+ ".v-imagestrip {background-color: #000000;}"
				);
	}

	private ImageStrip createStrip(int imgSize, int maxImage,boolean crop) {
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
        
        strip.setHeight(imgSize, UNITS_PIXELS);
        
        // Limit how many images are visible at most simultaneously
        if (maxImage!=0){
            strip.setMaxAllowed(maxImage);        	
        }
        if (urls != null){
	        for(String url:urls){
	            addImage(strip, url,imgSize,crop);        	
	        }
        }
        strip.setSelectable(true);
		return strip;
	}
	
	private void addImage(ImageStrip strip, String url,int imgSize,boolean crop){
		String filename=MyUtil.getFilename(url);
		if (crop){
			String filenamecropped=MyUtil.getCroppedFilename(url);
			strip.addImage(MyUtil.getCroppedFile(url,filename,filenamecropped,imgSize));			
		}else{
			String filenamescaled=MyUtil.getScaledFilename(url);
			org.vaadin.peter.imagestrip.ImageStrip.Image tmp = strip.addImage(MyUtil.getScaledFile(url,filename,filenamescaled,imgSize));						
			bigStripImageList.add(tmp);
		}
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		Property property = event.getProperty();
		ImageStrip.Image value = (ImageStrip.Image) property.getValue();
		int index = value.getImageIndex();
		changeImage(index);
	}

}