package fi.leonidasoy.imagestrip;

//annotations
import javax.servlet.annotation.WebServlet;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.addon.touchkit.server.TouchKitServlet;

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
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
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
			leonidasLogo = new URL("https://dl.dropboxusercontent.com/s/f0zf88kd68orrqb/leonidas-logo.png");
	    	button = new URL("https://dl.dropboxusercontent.com/s/0ae0qefrr961l14/arrow-right.png");
	    	button_flipped = new URL("https://dl.dropboxusercontent.com/s/9v4eixawq9tjkcp/arrow-left.png");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Failed to initialize private static final values");
		}
	}

	final private CssLayout mainLayout = new CssLayout();
	
	//layouts and urls
	final private static int imgSize=140;
	final private static int horizontalBorder = 20;
	final private static int smallStripLayoutHeight=imgSize + 2*horizontalBorder;
	final private static int imageBorderHeight = imgSize + 5;

	//progressbars etc. before actual app starts
	private Label imgMetaDataLabel;
	private ProgressBarLayout progressBar;
	
	//both imagestrips
	private ImageStripWrapper smallStrip; 
	private ImageStripWrapper bigStrip;
	
	//some variables that are related to screenheight
	private int bigStripHeight = 0;
	private int bigStripLayoutHeight = 0;
	private int nmbOfSmallsTripImages;
	
	
	
	@Override
	protected void init(VaadinRequest request) {
		UI ui = getUI();
		initVariables(ui);
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
						smallStrip = new ImageStripWrapper("smallstrip", images, imgSize,nmbOfSmallsTripImages,0,true,progressBar);
                    }
                });
	    		//download, scale and crop images
	        	access(new Runnable() {
                    @Override
                    public void run() {
                		progressBar.setValue("Scaling and cropping images.",0.75f);
                		bigStrip = new ImageStripWrapper("bigstrip", images, bigStripHeight,1,(nmbOfSmallsTripImages-1)/2,false,progressBar);
                    }
                });
	    		//start actual initialization of app
	        	access(new Runnable() {
                    @Override
                    public void run() {
                		progressBar.setValue("Initializing main layout.",0.95f);
                		Layout layout = createMainLayout();
                		progressBar.setValue("Ready.",0.99f);
                		setContent(layout);	            	
                    }
                });
	        }
	   }
	
	private void initVariables(UI ui) {
		//height based variables
		bigStripLayoutHeight = ui.getPage().getBrowserWindowHeight() -smallStripLayoutHeight-horizontalBorder *3;
		bigStripHeight=bigStripLayoutHeight-64;
		//we do not want the pictures to be to small :)
		if (bigStripHeight<2*imgSize){
			bigStripHeight=2*imgSize;
		}
		//floored to nearest 25 (just to save size as the scaled and cropped images are stored to filesystem :)
		bigStripHeight=(bigStripHeight/25)*25;

		//width based variables
		int pageWidth = (int) (ui.getPage().getBrowserWindowWidth()*0.75f);
		int numberofimages=pageWidth/(imgSize+20);
		int numberofImagesMinusMarginals = numberofimages;
		int oddNumberOfImages = (numberofImagesMinusMarginals/2)*2+1;
				
		nmbOfSmallsTripImages = oddNumberOfImages;
	}

	private void injectCssStyles(UI ui) {
		CSSInject css = new CSSInject(ui);

		int metadatalabelBottom = (bigStripLayoutHeight - bigStripHeight)/4;
		
		css.setStyles(""
				//positions progressbar and label
				+ ".progressBar {position: absolute !important; top:50% !important; left:50% !important; margin-left: -150px !important; margin-top: -20px !important;}"
				+ ".progressLabel {position: absolute !important; top:50% !important; left:50% !important; margin-left: -150px !important;}"

				//mainlayout
				+ ".mainWindow {background-color: #000000;} "

				+ ".logo {position: absolute; top:1%; left:2%;} "
				+ ".componentLayout {position: absolute; background-color: rgb(203,203,203); top:0px; left:8%; width:92%;} "
				
				+ ".bigStripLayout {position: absolute; background-color: rgb(239,239,239); top:"+horizontalBorder+"px !important; height:"+bigStripLayoutHeight+"px !important; left:2%; width:96% !important;} "
				+ ".smallStripLayout {position: absolute; background-color: rgb(239,239,239); bottom:"+horizontalBorder+"px !important; left:2%; height:"+smallStripLayoutHeight+"px !important; width:96% !important;} "

				+ ".scrollLeft {position: absolute; right:24px; width:24px; top:50%; height: 40px; margin-top: -20px !important;}"
				+ ".scrollRight {position: absolute; left:24px; width:24px; top:50%; height: 40px; margin-top: -20px !important;}"

				+ ".smallImageStrip {position: absolute; top:50%; margin-top: -"+imgSize/2+"px !important;}"
				+ ".reindeer .v-panel-content {border: 0px rgba(0,0,0,0); background-color: rgba(0,0,0,0);}"
				+ ".v-imagestrip {background-color: rgba(0,0,0,0);}"
				+ ".v-imagestrip .image-border {border-radius: 0px; background-color: rgba(0,0,0,0);}"
				+ ".v-imagestrip .strip-horizontal-scroller {width: 0px; height 0px;}"				

				+ ".bigImageStrip {position: absolute; top:50%; margin-top: -"+bigStripHeight/2+"px !important;}"
				+ ".metaDataLabel {position: absolute; color: rgb(32,32,32); bottom: 0px !important;}"

				//centers imageborder and draws dashed line around it
				+ ".imageBorder {position: absolute; border: 2px solid rgb(137,137,139); background-color: rgba(0,0,0,0); position: absolute !important; top:0px !important; left:50% !important; margin-left: -75px !important; top:50% !important; margin-top: -70px !important;}"
				
				//fullscreenlayout
				+ ".fullScreenLayout {vertical-align: middle; text-align: center !important;}"
				+ ".fullScreenImage {max-width: 98%; max-height: 94%; margin: 0; padding: 0; border: 0;}"				
				);
	}
    
    private Layout createMainLayout(){    	
    	mainLayout.setSizeFull();

        addKeyPressListeners();
    	
    	//logo
		Image logo = MyUtil.getImage(leonidasLogo);
		logo.setStyleName("logo");
		
		mainLayout.addComponent(logo);
    	
		//grey layout that contains all the other elements
		CssLayout componentLayout = new CssLayout();
		componentLayout.setHeight("100%");
		
		componentLayout.setStyleName("componentLayout");
		mainLayout.addComponent(componentLayout);
		
		//bigStripLayout
		//metadatapanel to bigstriplayout
		final CssLayout bigStripLayout = new CssLayout();
		bigStripLayout.setStyleName("bigStripLayout");

		final VerticalLayout imgLayout = new VerticalLayout();
		imgLayout.setSizeFull();
		Component component = bigStrip.getComponent();
		component.addStyleName("bigImageStrip");
		
		bigStripLayout.addComponent(component);        

		imgMetaDataLabel = new Label(this.getImgMetaDataLabelText(this.bigStrip.getIndex()));
		imgMetaDataLabel.setStyleName("metaDataLabel");
		imgMetaDataLabel.setContentMode(ContentMode.HTML);

		bigStripLayout.addComponent(imgMetaDataLabel);		
		componentLayout.addComponent(bigStripLayout);
        
        //smallStripLayout
		Image scrollRight = MyUtil.getImage(button_flipped);
		scrollRight.setStyleName("scrollRight");
        scrollRight.addClickListener(new ClickListener() {
			@Override
			public void click(ClickEvent event) {
				scrollToRight();
			}});
				
		Image scrollLeft = MyUtil.getImage(button);
		scrollLeft.setStyleName("scrollLeft");
        scrollLeft.addClickListener(new ClickListener() {
			@Override
			public void click(ClickEvent event) {
				scrollToLeft();
			}});

        Layout smallStripLayout = createSmallStripLayout();
        smallStripLayout.setStyleName("smallStripLayout");
        smallStripLayout.addComponent(scrollLeft);
        smallStripLayout.addComponent(scrollRight);
        
        componentLayout.addComponent(smallStripLayout);
				
        //init listeners
        initSmallStripListener();
		initBigStripListener();
		
    	return mainLayout;
    }

    private void addKeyPressListeners() {
    	// Let's create a customized shortcut that jumps to the next field
        addAction(new ShortcutListener("Next field", KeyCode.ARROW_LEFT,
                null) {
            @Override
            public void handleAction(final Object sender, final Object target) {
            	scrollToLeft();
            }
        });

        addAction(new ShortcutListener("Next field", KeyCode.ARROW_RIGHT,
                null) {
            @Override
            public void handleAction(final Object sender, final Object target) {
            	scrollToRight();
            }
        });
	}

	private Layout createSmallStripLayout() {		
		
		final CssLayout smallstriplayout = new CssLayout();
						
		//components that shows border around middle image and some layouting
		Panel borders = new Panel();
		borders.addStyleName("imageBorder");

		borders.setWidth(Float.toString(smallStrip.getHeight() +10) + "" + smallStrip.getHeightUnits());
		borders.setHeight(imageBorderHeight + "px");
		
		Component c = smallStrip.getComponent();
		c.addStyleName("smallImageStrip");
		smallstriplayout.addComponent(c);
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
        		
        		if (moveToLeft>0){
         			scrollToRight();
         			moveToLeft--;
         			for(int i = 1; i <= moveToLeft; i++){
         				new Timer().schedule(new TimerTask() {
	        	            @Override
	        	            public void run() {
	        	            	scrollToRight();
	        	        		getUI().push();
	        	            }
	        	        }, 500*i);
        			}
        		}else if (moveToLeft<0){
        			scrollToLeft();
        			moveToLeft++;
         			for(int i = 1; i <= -moveToLeft; i++){
	        			new Timer().schedule(new TimerTask() {
	        	            @Override
	        	            public void run() {
	        	            	scrollToLeft();
	        	        		getUI().push();
	        	            }
	        	        }, 500*i);
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
		setContent(mainLayout);
	}	
}