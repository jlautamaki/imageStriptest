package fi.leonidasoy.imagestrip;

//annotations
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.VaadinServletConfiguration;

//addons
import org.vaadin.cssinject.CSSInject;
import org.vaadin.peter.imagestrip.ImageStrip;

//java
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//vaadin
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.VaadinServlet;
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

//@Theme("mobiletheme")
@PreserveOnRefresh
@SuppressWarnings("serial")
@Push(PushMode.AUTOMATIC)
public class ImagestriptestUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ImagestriptestUI.class, widgetset = "fi.leonidasoy.imagestrip.widgetset.ImagestriptestWidgetset")
	public static class Servlet extends VaadinServlet {
	}
	//@VaadinServletConfiguration(productionMode = false, ui = ImagestriptestUI.class, widgetset = "fi.leonidasoy.imagestrip.widgetset.ImagestriptestWidgetset")	
	//public static class Servlet extends TouchKitServlet {
	//}
	
	/*
	<dependency>
      <groupId>com.google.jimfs</groupId>
	  <artifactId>jimfs</artifactId>
	  <version>1.0</version>
	</dependency>
	 
	<dependency org="com.google.jimfs" name="jimfs" rev="1.0" />
	
	 
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
	
	//layouts and urls (related to screensize and initilized in initvariables function
	private int imgSize;
	private int horizontalBorder;
	private int smallStripLayoutHeight;
	private int imageBorderHeight;
	private Label imgMetaDataLabel;
	
	//both imagestrips
	private ImageStripWrapper smallStrip; 
	private ImageStripWrapper bigStrip;
	
	//some variables that are related to screenheight
	private int bigStripHeight;
	private int bigStripLayoutHeight;
	private int nmbOfSmallsTripImages;
    private static List<MyImage> images = new ArrayList<MyImage>();
	private int imageBorderWidth;
	private String logoWidthString;
	private String fontsizeString;
	final private CssLayout bigStripLayout = new CssLayout();
	final private CssLayout smallStripLayout = new CssLayout();
	final private Panel borders = new Panel();
	private Image scrollLeft;
	private Image scrollRight;

	@Override
	protected void init(VaadinRequest request) {
				//styles etc.
		this.setStyleName("mainWindow");		
		this.setImmediate(true);
		this.setResizeLazy(true);
		
		initVariables();
		injectCssStyles();

		bigStripLayout.setStyleName("bigStripLayout");
        smallStripLayout.setStyleName("smallStripLayout");
		borders.addStyleName("imageBorder");

		setContent(createMainLayout());
		
		Page.getCurrent().addBrowserWindowResizeListener(new Page.BrowserWindowResizeListener() {
			@Override
			public void browserWindowResized(BrowserWindowResizeEvent event) {
				new ResizeThread().start();
			}
		});
		new InitThread().start();
	}
	
	class InitThread extends Thread {
        @Override
        public void run() {
        	final UI ui = UI.getCurrent();
            ui.accessSynchronously(new Runnable() {
                @Override
                public void run() {
        			//currently also done statically in myimage
        			images = MyImage.getImages();
                }
            });

            
            ui.access(new Runnable() {
                @Override
                public void run() {
        			smallStrip = new ImageStripWrapper("smallImageStrip", images, imgSize,nmbOfSmallsTripImages,0,true);
        			smallStrip.setListener(getSmallStripListener());
        			
        			Component c = smallStrip.getComponent();
        			smallStripLayout.addComponent(c);

        			//border for middle-image
        			borders.setWidth(imageBorderWidth + "" + smallStrip.getHeightUnits());
        			borders.setHeight(imageBorderHeight + "px");

        			smallStripLayout.addComponent(borders);		
        			initScrollButtonListeners();
        		}});
        			
            ui.access(new Runnable() {
                @Override
                public void run() {
        			bigStrip = new ImageStripWrapper("bigImageStrip", images, bigStripHeight,1,(nmbOfSmallsTripImages-1)/2,false);
        			bigStrip.setListener(getBigStripListener());

        			Component c = bigStrip.getComponent();
        			bigStripLayout.addComponent(c);        

        			imgMetaDataLabel = new Label(getImgMetaDataLabelText(bigStrip.getIndex()));
        			imgMetaDataLabel.setStyleName("metaDataLabel");
        			imgMetaDataLabel.setContentMode(ContentMode.HTML);
        			bigStripLayout.addComponent(imgMetaDataLabel);		        	        
        		}});													
        }
    }	
	
   class ResizeThread extends Thread {	        
        @Override
        public void run() {
        	
        	final UI ui = UI.getCurrent();
        	
        	ui.accessSynchronously(new Runnable() {	
				@Override
				public void run() {
					//update variables and css
					initVariables();
					injectCssStyles();
					push();
				}
        	});
												
        	ui.access(new Runnable() {
                @Override
                public void run() {							
					//update bigstrip			
        			bigStripLayout.removeComponent(bigStrip.getComponent());			
        			bigStrip.resize(bigStripHeight,1,(nmbOfSmallsTripImages-1)/2);
        			bigStripLayout.addComponent(bigStrip.getComponent());
					push();
        			}});

			ui.access(new Runnable() {
			    @Override
			    public void run() {
					//update smallStrip
					smallStripLayout.removeComponent(smallStrip.getComponent());
					smallStrip.resize(imgSize,nmbOfSmallsTripImages,0);			
					smallStripLayout.addComponent(smallStrip.getComponent());
					push();
					}});

			ui.access(new Runnable() {
			    @Override
			    public void run() {
			    	//update borders
			    	borders.setWidth(imageBorderWidth + "" + smallStrip.getHeightUnits());
					borders.setHeight(imageBorderHeight + "px");
					push();
				}
			});
        }
    }
	   
	private void initVariables() {
		int smallscreenthreshold = 500;
		int windowHeight = this.getPage().getBrowserWindowHeight();
		int windowWidth = this.getPage().getBrowserWindowWidth();
		if (windowHeight<smallscreenthreshold || windowWidth < smallscreenthreshold){
			imgSize=40;
			horizontalBorder=7;
			logoWidthString = "width:4%;";
		}else{
			imgSize=120;
			horizontalBorder=20;
			logoWidthString = "";
		}
		
		fontsizeString = "font-size:x-small;";
		smallStripLayoutHeight=imgSize + 2*horizontalBorder;
		imageBorderHeight = imgSize + 5;
		imageBorderWidth = imgSize +10;
		
		//height based variables
		bigStripLayoutHeight = windowHeight -smallStripLayoutHeight-horizontalBorder *3;
		if (windowWidth<smallscreenthreshold){
			bigStripHeight=(int) (windowWidth*0.8f-48);						
		}else{
			bigStripHeight=bigStripLayoutHeight-48;			
		}
		//floored to nearest 25 (just to save size as the scaled and cropped images are stored to filesystem :)
		bigStripHeight=(bigStripHeight/25)*25;

		//width based variables
		int pageWidth = (int) (UI.getCurrent().getPage().getBrowserWindowWidth()*0.75f);
		int tmp=pageWidth/(imgSize+20);
		if (tmp>1&&tmp%2==0){
			tmp-=1;
		}
		nmbOfSmallsTripImages = tmp;
	}

	private void injectCssStyles() {
		CSSInject css = new CSSInject(UI.getCurrent());
		
		css.setStyles(""
				//positions progressbar and label
				+ ".progressBar {position: absolute !important; top:50% !important; left:50% !important; margin-left: -150px !important; margin-top: -20px !important;}"
				+ ".progressLabel {color: rgb(203,203,203); position: absolute !important; top:50% !important; left:50% !important; margin-left: -150px !important;}"

				//mainlayout
				+ ".mainWindow {background-color: #000000;} "

				+ ".logo {position: absolute; top:1%; left:2%; "+logoWidthString+"} "
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
				+ ".metaDataLabel {position: absolute; color: rgb(32,32,32); bottom: 0px !important; "+fontsizeString+"}"

				//centers imageborder and draws dashed line around it
				+ ".imageBorder {position: absolute; border: 2px solid rgb(137,137,139); background-color: rgba(0,0,0,0); position: absolute !important; top:0px !important; left:50% !important; margin-left: -"+imageBorderWidth/2+"px !important; top:50% !important; margin-top: -"+imgSize/2+"px !important;}"
				
				//fullscreenlayout
				+ ".fullScreenLayout {vertical-align: middle; text-align: center !important;}"
				+ ".fullScreenImage {max-width: 98%; max-height: 94%; margin: 0; padding: 0; border: 0;}"				
				);
	}
    
    private Layout createMainLayout(){    	
    	mainLayout.setSizeFull();
        addKeyPressListeners();
    	
    	//add logo
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
		final VerticalLayout imgLayout = new VerticalLayout();
		imgLayout.setSizeFull();
		
        componentLayout.addComponent(smallStripLayout);
		componentLayout.addComponent(bigStripLayout);
		initScrollButtons();

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

	private void initScrollButtons(){
		scrollRight = MyUtil.getImage(button_flipped);
		scrollLeft = MyUtil.getImage(button);
		
		scrollRight.setStyleName("scrollRight");
		scrollLeft.setStyleName("scrollLeft");
	}

	private void initScrollButtonListeners(){
	    scrollRight.addClickListener(new ClickListener() {
			@Override
			public void click(ClickEvent event) {
				scrollToRight();
			}
	    });
	
	    
	    scrollLeft.addClickListener(new ClickListener() {
			@Override
			public void click(ClickEvent event) {
				scrollToLeft();
			}
		});
	    smallStripLayout.addComponent(scrollLeft);
	    smallStripLayout.addComponent(scrollRight);
	}
	
	//listeners for the bigStrip
	private ValueChangeListener getBigStripListener() {
		return new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
        		int index = bigStrip.getIndex();
        		changeToFullScreenImage(index);
            }
        };
	}

	//listeners for smallstrip
	private ValueChangeListener getSmallStripListener() {
		return new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
        		ImageStrip.Image value = (ImageStrip.Image) event.getProperty().getValue();
        		int clickedindex = value.getImageIndex();
        		int moveToLeft = smallStrip.offsetComparedToMiddle(clickedindex);
        		
        		if (moveToLeft>0){
         			scrollToLeft();
         			moveToLeft--;
         			for(int i = 1; i <= moveToLeft; i++){
         				new Timer().schedule(new TimerTask() {
	        	            @Override
	        	            public void run() {
	        	            	scrollToLeft();
	        	        		getUI().push();
	        	            }
	        	        }, 500*i);
        			}
        		}else if (moveToLeft<0){
        			scrollToRight();
        			moveToLeft++;
         			for(int i = 1; i <= -moveToLeft; i++){
	        			new Timer().schedule(new TimerTask() {
	        	            @Override
	        	            public void run() {
	        	            	scrollToRight();
	        	        		getUI().push();
	        	            }
	        	        }, 500*i);
        			}
        		}
        	}
        };
	}

	//scrolls both strips and updates other fields
	protected void scrollToRight() {
		smallStrip.scrollToRight();
		bigStrip.scrollToRight();
		this.imgMetaDataLabel.setValue(getImgMetaDataLabelText(bigStrip.getIndex()));
	}

	//scrolls both strips and updates other fields
	protected void scrollToLeft() {
		smallStrip.scrollToLeft();
		bigStrip.scrollToLeft();
		this.imgMetaDataLabel.setValue(getImgMetaDataLabelText(bigStrip.getIndex()));
	}

	//String that contains some information about image
	private String getImgMetaDataLabelText(int index) {
		return "<center>" + MyImage.getImage(index).getMetadata() + "</center>";
	}

	//opens image to fullscreen
	protected void changeToFullScreenImage(int index) {		
		MyImage selectedImage = MyImage.getImage(index);
		FileResource res = selectedImage.getFileResource();
		Image image = new Image(res.getFilename(),res);
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
		setContent(layout);			
	}

	//closes fullscreen imageviewer
	protected void closeFullscreenView() {
		setContent(mainLayout);
	}	
}