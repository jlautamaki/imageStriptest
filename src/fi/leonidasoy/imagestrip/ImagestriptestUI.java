package fi.leonidasoy.imagestrip;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;

import org.vaadin.cssinject.CSSInject;
import org.vaadin.peter.imagestrip.ImageStrip;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;


@SuppressWarnings("serial")
@Theme("mobiletheme")
@Push(PushMode.MANUAL) 
public class ImagestriptestUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ImagestriptestUI.class, widgetset = "fi.leonidasoy.imagestrip.widgetset.ImagestriptestWidgetset")
	public static class Servlet extends VaadinServlet {
	}

/*	@PreserveOnRefresh
	@SuppressWarnings("serial")
	@Theme("mobiletheme")
	@Widgetset("com.arvue.apps.imagestriptest.gwt.AppWidgetSet")
	//@Widgetset("com.vaadin.addon.touchkit.gwt.TouchKitWidgetSet")
	public class App extends UI{
*/
	
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

	private static final URL leonidasLogo;
	private static final URL button;
	//private static final URL buttonPressed;
	private static final URL button_flipped;
	
	static{
		try {
			leonidasLogo = new URL("https://db.tt/bnvnVqrc");
	    	button = new URL("https://dl.dropboxusercontent.com/u/33984813/nuoli.png");
	    	//buttonPressed = new URL("https://dl.dropboxusercontent.com/u/33984813/nuoli---hover.png");
	    	button_flipped = new URL("https://dl.dropboxusercontent.com/u/33984813/nuoli-flipped.png");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Failed to initialize private static final values");
		}
	}
	
	//layouts and urls
	final private static int imgSize=140;

	//imagestripdata
	private ImageStripWrapper smallStrip; 
	private ImageStripWrapper bigStrip;
	
	private GridLayout gridLayout;
	private Label imgMetaDataLabel;
	private int bigStripHeight = 0;
	
	@Override
	protected void init(VaadinRequest request) {
		getbigStripHeight();
		new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
            	initUI();
            }
        }, 500);		
	}
	
	private void initUI(){
		//styles etc.
		injectCssStyles();
		this.setStyleName("mainWindow");		
		this.setImmediate(true);

		//progress indicator to show progress of imageloads
		CssLayout layout = new CssLayout();
		layout.setSizeFull();

		ProgressBar bar = new ProgressBar(0.25f);
		bar.setStyleName("progressBar");
		bar.setWidth("300px");
		bar.setHeight("20px");

		Label label = new Label();
		label.setStyleName("progressLabel");
		label.setContentMode(ContentMode.HTML);
		label.setWidth("300px");
		label.setValue("<center>Refreshing image database.</center>");

		layout.addComponent(bar);
		layout.addComponent(label);
		setContent(layout);
		getUI().push();		
		
		//start actual initialization of app
		MyImage[] images = MyImage.getImages();
		bar.setValue(0.5f);
		label.setValue("<center>Downloading images.</center>");	            	
		getUI().push();		
		smallStrip = new ImageStripWrapper("smallstrip", images, imgSize,5,0,true);
		bar.setValue(0.75f);
		label.setValue("<center>Scaling and cropping images.</center>");	            	
		getUI().push();		
		bigStrip = new ImageStripWrapper("bigstrip", images, getbigStripHeight(),1,2,false);
		bar.setValue(0.95f);
		label.setValue("<center>Initializing main layout.</center>");	            	
		getUI().push();		
		setContent(createMainLayout());	            	
		getUI().push();		
	}
	
	private int getbigStripHeight() {
		if (this.bigStripHeight==0){
			bigStripHeight=getUI().getPage().getBrowserWindowHeight()-imgSize;
			//we do not want the pictures to be to small :)
			if (bigStripHeight<2*imgSize){
				bigStripHeight=2*imgSize;
			}
			//floored to nearest hungred (just to save size as the scaled and cropped images are stored to filesystem :)
			bigStripHeight=(bigStripHeight/100)*100;
			System.out.println("bigstripHeight = " + bigStripHeight);
		}
		return bigStripHeight;
	}

	private void injectCssStyles() {
		CSSInject css = new CSSInject(getUI());
		css.setStyles(""
				//centers imageborder and draws dashed line around it
				+ ".progressBar {position: absolute !important; top:50% !important; left:50% !important; margin-left: -150px !important; margin-top: -20px !important;}"
				//positions progressbar and label
				+ ".progressLabel {position: absolute !important; top:50% !important; left:50% !important; margin-left: -150px !important;}"
				//centers imageborder and draws dashed line around it
				+ ".imageBorder {border: 2px dashed rgb(0,234,80); position: absolute !important; top:0px !important; left:50% !important; margin-left: -75px !important;}"
				+ ".mainWindow {background-color: #000000;} "
				+ ".v-imagestrip {background-color: rgba(0,0,0,0);}"
				+ ".v-imagestrip .image-border {border-radius: 0px; background-color: rgba(0,0,0,0);}"
				+ ".v-imagestrip .strip-horizontal-scroller {width: 0px; height 0px;}"
				+ ".v-label.v-has-width {color: #ffffff}"
				);
	}
    
    private GridLayout createMainLayout(){
    	bigStrip.reCreateStrip();
    	smallStrip.reCreateStrip();
    	gridLayout = new GridLayout();
		final VerticalLayout sourceLayout = new VerticalLayout();
		sourceLayout.setWidth("100px");

		final VerticalLayout imgLayout = new VerticalLayout();

		Image image = MyUtil.getImage(leonidasLogo);
		image.setWidth("50px");
		
		final AbsoluteLayout bigStripLayout = new AbsoluteLayout();
		bigStripLayout.setHeight("100%");
		bigStripLayout.setWidth("100%");
		
		gridLayout.setSizeFull();
		gridLayout.setMargin(true);
        gridLayout.setRows(2);
        gridLayout.setColumns(3);
        gridLayout.setSpacing(true);
        gridLayout.setColumnExpandRatio(0, 0f);
        gridLayout.setColumnExpandRatio(1, 1f);
        gridLayout.setColumnExpandRatio(2, 0f);
        gridLayout.setRowExpandRatio(0, 1f);
        gridLayout.setRowExpandRatio(1, 0f);
                				
		Image scrollRight = MyUtil.getImage(button_flipped);
		scrollRight.setWidth("50px");
        scrollRight.addClickListener(new ClickListener() {
			@Override
			public void click(ClickEvent event) {
				scrollToRight();
			}});
				
		Image scrollLeft = MyUtil.getImage(button);
		scrollLeft.setWidth("50px");
        scrollLeft.addClickListener(new ClickListener() {
			@Override
			public void click(ClickEvent event) {
				scrollToLeft();
			}});
		
        gridLayout.addComponent(image, 0, 0);
        gridLayout.addComponent(bigStripLayout, 1, 0);
		gridLayout.addComponent(scrollRight,0, 1);
		gridLayout.addComponent(scrollLeft,2, 1);
		gridLayout.addComponent(createSmallStripLayout(), 1, 1);
			
		gridLayout.setComponentAlignment(scrollRight, Alignment.MIDDLE_CENTER);
		gridLayout.setComponentAlignment(scrollLeft, Alignment.MIDDLE_CENTER);
		
		Panel panel = new Panel("");
		bigStripLayout.addComponent(panel, "left: 0px; right: 0px; "+
		                           "top: 0px; bottom: 0px;");
		imgLayout.setSizeFull();
		Component component = bigStrip.getComponent();
		imgLayout.addComponent(component);
		imgLayout.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
		panel.setContent(imgLayout);
		imgMetaDataLabel = new Label(this.getImgMetaDataLabelText(this.bigStrip.getIndex()));
		imgMetaDataLabel.setContentMode(ContentMode.HTML);
		bigStripLayout.addComponent(imgMetaDataLabel, "left: 0px; right: 0px; "+
                "bottom: 0px;");
		
		initSmallStripListener();
		initBigStripListener();
    	return gridLayout;
    }

    private Component createSmallStripLayout() {
        //some additional space (+ 5) for border around the middle image
		String height = (smallStrip.getHeight() + 5) + "" + smallStrip.getHeightUnits();
		final int borderwidthint = (int) (smallStrip.getHeight() + 10);
		final String borderwidth =  borderwidthint + "" + smallStrip.getHeightUnits();
		
		final CssLayout test = new CssLayout();
		test.setHeight(height);
		test.setWidth("100%");
				
    	//absolutelayout for layouting components in z-order
    	//final AbsoluteLayout smallStripLayout = new AbsoluteLayout();
		//smallStripLayout.setHeight(height);
		//smallStripLayout.setWidth("100%");
		
    	//strip that shows images
		//smallStripLayout.addComponent(smallStrip.getComponent(), "left: 50% !important");
		
		//components that shows border around middle image and some layouting
		Panel borders = new Panel();
		borders.addStyleName("imageBorder");
		borders.setWidth(borderwidth);
		borders.setHeight(height);
		//VerticalLayout layoutforcenteringpanel = new VerticalLayout();
		//layoutforcenteringpanel.addComponent(borders);
		//smallStripLayout.addComponent(layoutforcenteringpanel,"left: 50%;");
		//layoutforcenteringpanel.addComponent(borders);
		//layoutforcenteringpanel.setComponentAlignment(borders, Alignment.TOP_CENTER);
//		smallStripLayout.addComponent(borders,"left: 40%; right: 40%; top: 0px; bottom: 0px;");
		
		test.addComponent(smallStrip.getComponent());
		test.addComponent(borders);
		test.addStyleName("cssLayout");
		
//		return smallStripLayout;
		return test;
	}

	//listeners for the bigStrip
	private void initBigStripListener() {
		bigStrip.setListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
		        		int index = bigStrip.getIndex();
		        		changeToFullScreenImage(index);
		            }
		        });
	}

	//listeners for smallstrip
	private void initSmallStripListener() {
		smallStrip.setListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
        		ImageStrip.Image value = (ImageStrip.Image) event.getProperty().getValue();
        		int clickedindex = value.getImageIndex();
        		int moveToLeft = smallStrip.offsetComparedToMiddle(clickedindex);
        		//should be between -2 and 2
        		assert Math.abs(moveToLeft)<=2;
        		
        		if (moveToLeft>0){
        			scrollToRight();
        			if (moveToLeft==2){
	        			new Timer().schedule(new TimerTask() {
	        	            @Override
	        	            public void run() {
	        	            	scrollToRight();
	        	        		getUI().push();
	        	            }
	        	        }, 500);
        			}
        		}else if (moveToLeft<0){
        			scrollToLeft();
        			if (moveToLeft==-2){
	        			new Timer().schedule(new TimerTask() {
	        	            @Override
	        	            public void run() {
	        	            	scrollToLeft();
	        	        		getUI().push();
	        	            }
	        	        }, 500);
        			}
        		}
            }
        });
	}

	//scrolls both strips and updates other fields
	protected void scrollToRight() {
		smallStrip.scrollToLeft();
		bigStrip.scrollToLeft();
		this.imgMetaDataLabel.setValue(getImgMetaDataLabelText(bigStrip.getIndex()));
	}

	//scrolls both strips and updates other fields
	protected void scrollToLeft() {
		smallStrip.scrollToRight();
		bigStrip.scrollToRight();
		this.imgMetaDataLabel.setValue(getImgMetaDataLabelText(bigStrip.getIndex()));
	}

	//String that contains some information about image
	private String getImgMetaDataLabelText(int index) {
		return "<center>" + MyImage.getImage(index).getMetadata() + "</center>";
	}

	//opens image to fullscreen
	protected void changeToFullScreenImage(int index) {		
		MyImage selectedImage = MyImage.getImage(index);
		URL url = selectedImage.getUrl();
		FileResource res = selectedImage.getFileResource();
		Image image = new Image(url.getFile(),res);
		image.setWidth("100%");
		image.addClickListener(new ClickListener() {
			@Override
			public void click(ClickEvent event) {
				closeFullscreenView();
			}
        });
		VerticalLayout fullscreenlayout = new VerticalLayout();
		fullscreenlayout.addComponent(image);			
		fullscreenlayout.setWidth("100%");
		this.setContent(fullscreenlayout);
	}

	//closes fullscreen imageviewer
	protected void closeFullscreenView() {
		setContent(gridLayout);
	}
}