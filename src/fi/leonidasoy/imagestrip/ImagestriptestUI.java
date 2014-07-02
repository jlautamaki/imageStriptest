package fi.leonidasoy.imagestrip;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;

import java.io.File;
import org.vaadin.cssinject.CSSInject;
import org.vaadin.peter.imagestrip.ImageStrip;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;


@SuppressWarnings("serial")
@Theme("mobiletheme")
//@Theme("imagestriptest")
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

	private static final String leonidasLogo = "https://db.tt/bnvnVqrc";
	private static final String button = "https://dl.dropboxusercontent.com/u/33984813/nuoli.png";
	//private static final String buttonPressed = "https://dl.dropboxusercontent.com/u/33984813/nuoli---hover.png";
	private static final String button_flipped = "https://dl.dropboxusercontent.com/u/33984813/nuoli-flipped.png";
	
	final private static String[] urls = MyUtil.getUrlsFromDropbox();

	//layouts and urls
	final private static int imgSize=140;

	//imagestripdata
	private ImageStripWrapper smallStrip = new ImageStripWrapper(urls, imgSize,5,0,true);
	private ImageStripWrapper bigStrip = new ImageStripWrapper(urls, imgSize*4,1,2,false);
	
	private GridLayout gridLayout;
	private Label imgMetaDataLabel;
	
	@Override
	protected void init(VaadinRequest request) {
		injectCssStyles();		
		this.setStyleName("mainWindow");		
		setContent(createMainLayout());
	}
	
	private void injectCssStyles() {
		CSSInject css = new CSSInject(getUI());
		css.setStyles(".mainWindow {background-color: #000000;} "
				+ ".metadataViewer {opacity: 0.7;background-color: #ffffff;}"
				+ ".reindeer .v-panel-content, .reindeer .white .v-panel-content {border: 0px solid #000000}"
				+ ".v-imagestrip .image-border {border-radius: 0px; background-color: #000000;}"
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
		
		final AbsoluteLayout absLayout = new AbsoluteLayout();
		absLayout.setHeight("100%");
		absLayout.setWidth("100%");

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
				scrollToRight(1);
			}});
				
		Image scrollLeft = MyUtil.getImage(button);
		scrollLeft.setWidth("50px");
        scrollLeft.addClickListener(new ClickListener() {
			@Override
			public void click(ClickEvent event) {
				scrollToLeft(1);
			}});
		
        gridLayout.addComponent(image, 0, 0);
        gridLayout.addComponent(absLayout, 1, 0);
		//gridLayout.addComponent(this.initMetaDataButton(), 2, 0);
		gridLayout.addComponent(scrollRight,0, 1);
		gridLayout.addComponent(scrollLeft,2, 1);
		gridLayout.addComponent(smallStrip.getComponent(), 1, 1);
	
		gridLayout.setComponentAlignment(scrollRight, Alignment.MIDDLE_CENTER);
		gridLayout.setComponentAlignment(scrollLeft, Alignment.MIDDLE_CENTER);

		
		Panel panel = new Panel("");
		absLayout.addComponent(panel, "left: 0px; right: 0px; "+
		                           "top: 0px; bottom: 0px;");
		imgLayout.setSizeFull();
		Component component = bigStrip.getComponent();
		imgLayout.addComponent(component);
		imgLayout.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
		panel.setContent(imgLayout);
		imgMetaDataLabel = new Label(this.getImgMetaDataLabelText());
		imgMetaDataLabel.setContentMode(ContentMode.HTML);
		absLayout.addComponent(imgMetaDataLabel, "left: 0px; right: 0px; "+
                "bottom: 0px;");
		
		initSmallStripListener();
		initBigStripListener();
    	return gridLayout;
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
        			scrollToRight(Math.abs(moveToLeft));
        		}else if (moveToLeft<0){
        			scrollToLeft(moveToLeft);
        		}
            }
        });
	}

	//scrolls both strips and updates other fields
	protected void scrollToRight(int i) {
		smallStrip.scrollToLeft(i);
		bigStrip.scrollToLeft(i);
		this.imgMetaDataLabel.setValue(getImgMetaDataLabelText());
	}

	//scrolls both strips and updates other fields
	protected void scrollToLeft(int i) {
		smallStrip.scrollToRight(i);
		bigStrip.scrollToRight(i);
		this.imgMetaDataLabel.setValue(getImgMetaDataLabelText());
	}

	//String that contains some information about image
	private String getImgMetaDataLabelText() {
		return "<center>"+MetadataExtractor.getImageLabelText(new File(MyUtil.getFilename(urls[bigStrip.getIndex()]))) + "</center>";
	}

	//opens image to fullscreen
	protected void changeToFullScreenImage(int index) {		
		String url = urls[index];
		FileResource res = MyUtil.getFileResource(url);
		Image image = new Image(url,res);
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