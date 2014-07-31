package fi.leonidasoy.imagestrip;

//annotations
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.addon.touchkit.server.TouchKitServlet;
import com.vaadin.addon.touchkit.ui.NavigationManager;




//addons
import org.vaadin.cssinject.CSSInject;
import org.vaadin.peter.imagestrip.ImageStrip;




//java
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;




//vaadin
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@PreserveOnRefresh
@SuppressWarnings("serial")
@Push(PushMode.AUTOMATIC) 
//@Theme("mobiletheme")
public class ImagestriptestUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ImagestriptestUI.class, widgetset = "fi.leonidasoy.imagestrip.widgetset.ImagestriptestWidgetset")
	//@VaadinServletConfiguration(productionMode = false, ui = ImagestriptestUI.class, widgetset = "com.vaadin.addon.touchkit.gwt.TouchKitWidgetSet")
	public static class Servlet extends TouchKitServlet {
	}
	
	/*
	javax.servlet
	<dependency>
		<groupId>javax.servlet</groupId>
		<artifactId>javax.servlet-api</artifactId>
		<version>3.0.1</version>
	</dependency>  
	 
	 <dependency org="javax.servlet" name="javax.servlet-api" rev="3.0.1"/>
	 
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
   		<version>4.0.0.beta2</version>
	</dependency>
	
	<dependency org="com.vaadin.addon" name="vaadin-touchkit-agpl" rev="4.0.0.beta2" />
	
	
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
	private ProgressBarLayout progressBar;
	
	@Override
	protected void init(VaadinRequest request) {
		UI ui = getUI();
		getbigStripHeight(ui);
		//styles etc.
		injectCssStyles(getUI());
		this.setStyleName("mainWindow");		
		this.setImmediate(true);
		//progress indicator to show progress of imageloads
		progressBar = new ProgressBarLayout(ui);
		progressBar.setValue("Downloading imagelist.", 0.25f);
		ui.setContent(progressBar);

		//UI is initialized in another thread as it takes some time
		//while initializing websockets are used for updating progressbar
		new InitializerThread().start();
	}
	
	   class InitializerThread extends Thread {	        
           private MyImage[] images;
	        @Override
	        public void run() {
        		//get imagelist
	        	access(new Runnable() {

					@Override
                    public void run() {
                		images = MyImage.getImages(progressBar, getUI());
                    }
                });
	    		//download, scale and crop images
	        	access(new Runnable() {
                    @Override
                    public void run() {
                		progressBar.setValue("Downloading images.",0.5f);
                		smallStrip = new ImageStripWrapper("smallstrip", images, imgSize,5,0,true,progressBar);
                    }
                });
	    		//download, scale and crop images
	        	access(new Runnable() {
                    @Override
                    public void run() {
                		progressBar.setValue("Scaling and cropping images.",0.75f);
                		bigStrip = new ImageStripWrapper("bigstrip", images, getbigStripHeight(getUI()),1,2,false,progressBar);
                    }
                });
	    		//start actual initialization of app
	        	access(new Runnable() {
                    @Override
                    public void run() {
                		progressBar.setValue("Initializing main layout.",0.95f);
                		System.out.println ("1");
                		Layout layout = createMainLayout();
                		System.out.println ("2");
                		progressBar.setValue("Ready.",0.99f);
                		System.out.println (layout);
                		setContent(layout);	            	
                    }
                });
	        }
	   }
	
	private int getbigStripHeight(UI ui) {
		if (this.bigStripHeight==0){
			bigStripHeight=ui.getPage().getBrowserWindowHeight()-imgSize;
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

	private void injectCssStyles(UI ui) {
		CSSInject css = new CSSInject(ui);
		css.setStyles(""
				//centers imageborder and draws dashed line around it
				+ ".progressBar {position: absolute !important; top:50% !important; left:50% !important; margin-left: -150px !important; margin-top: -20px !important;}"
				//positions progressbar and label
				+ ".progressLabel {position: absolute !important; top:50% !important; left:50% !important; margin-left: -150px !important;}"
				//centers imageborder and draws dashed line around it
				+ ".imageBorder {border: 2px dashed rgb(0,234,80); background-color: rgba(0,0,0,0); position: absolute !important; top:0px !important; left:50% !important; margin-left: -75px !important;}"
				+ ".fullScreenLayout {vertical-align: middle; text-align: center !important;}"
				+ ".fullScreenImage {max-width: 98%; max-height: 94%; margin: 0; padding: 0; border: 0;}"				
				+ ".mainWindow {background-color: #000000;} "
				+ ".reindeer .v-panel-content {border: 0px rgba(0,0,0,0); background-color: rgba(0,0,0,0);}"
				+ ".v-imagestrip {background-color: rgba(0,0,0,0);}"
				+ ".v-imagestrip .image-border {border-radius: 0px; background-color: rgba(0,0,0,0);}"
				+ ".v-imagestrip .strip-horizontal-scroller {width: 0px; height 0px;}"
				+ ".v-label.v-has-width {color: #ffffff}"
				);
	}
    
    private GridLayout createMainLayout(){
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
		//gridLayout.addComponent(new SwipeViewTestMgr(),2,0);

		gridLayout.addComponent(scrollRight,0, 1);
		gridLayout.addComponent(createSmallStripLayout(), 1, 1);
		gridLayout.addComponent(scrollLeft,2, 1);
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
		
		final CssLayout smallstriplayout = new CssLayout();
		smallstriplayout.setHeight(height);
		smallstriplayout.setWidth("100%");
						
		//components that shows border around middle image and some layouting
		Panel borders = new Panel();
		borders.addStyleName("imageBorder");
		borders.setWidth(borderwidth);
		borders.setHeight(height);
		
		smallstriplayout.addComponent(smallStrip.getComponent());
		smallstriplayout.addComponent(borders);		
		return smallstriplayout;
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
		image.addClickListener(new ClickListener() {
			@Override
			public void click(ClickEvent event) {
				closeFullscreenView();
			}
        });
		CssLayout layout = new CssLayout();
		layout.setSizeFull();		
		image.setStyleName("fullScreenImage");
		layout.setStyleName("fullScreenLayout");
		layout.addComponent(image);			
		this.setContent(layout);			
	}

	//closes fullscreen imageviewer
	protected void closeFullscreenView() {
		setContent(gridLayout);
	}
	
//	public static class SwipeViewTestMgr extends NavigationManager {

//        public SwipeViewTestMgr() {

//           setCurrentComponent(new Label("currentcomponent!!!!"));

//            addNavigationListener(new NavigationListener() {
//                public void navigate(NavigationEvent event) {
//                    if (event.getDirection() == Direction.FORWARD) {
//                    	System.out.println("to right");
//                    } else {
//                    	System.out.println("to left");
//                    }
//                }
//            });
//        }
//	}
}