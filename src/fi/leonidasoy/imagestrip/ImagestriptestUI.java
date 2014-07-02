package fi.leonidasoy.imagestrip;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;

import java.io.File;
import java.util.ArrayList;

import org.imgscalr.Scalr.Rotation;
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
import com.vaadin.ui.Button;
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
	private static final String buttonPressed = "https://dl.dropboxusercontent.com/u/33984813/nuoli---hover.png";
	private static final String button_flipped = "https://dl.dropboxusercontent.com/u/33984813/nuoli-flipped.png";
	
	final private static String[] urls = MyUtil.getUrlsFromDropbox();

	//layouts and urls
//	final private GridLayout layout = new GridLayout();
	final private AbsoluteLayout absLayout = new AbsoluteLayout();
	final private VerticalLayout fullscreenlayout = new VerticalLayout();
	private MetadataViewer metadatawindow;

	private boolean metadataVisible = false;
	final private static int imgSize=140;

	//imagestripdata
	private ImageStripWrapper smallStrip = new ImageStripWrapper(urls, imgSize,5,0,true);
	private ImageStripWrapper bigStrip = new ImageStripWrapper(urls, imgSize*4,1,2,false);

	
	private ArrayList<org.vaadin.peter.imagestrip.ImageStrip.Image> bigStripImageList = new ArrayList<org.vaadin.peter.imagestrip.ImageStrip.Image>();
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
//				+ ".v-layout.v-vertical {background-color: #000000;}"
				+ ".reindeer .v-panel-content, .reindeer .white .v-panel-content {border: 0px solid #000000}"
				+ ".v-imagestrip .image-border {border-radius: 0px; background-color: #000000;}"
				+ ".v-imagestrip .image-border-selected {background-color: #629632}"
//				+ ".v-imagestrip .horizontal {background-color: #000000;}"
//				+ ".v-imagestrip {background-color: #000000;}"
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
                				
		Image scrollRight = MyUtil.getImage(this.button_flipped);
		scrollRight.setWidth("50px");
        scrollRight.addClickListener(new ClickListener() {
			@Override
			public void click(ClickEvent event) {
				scrollToRight(1);
			}});
				
		Image scrollLeft = MyUtil.getImage(this.button);
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
    
	private void initBigStripListener() {
		bigStrip.setListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
		        		int index = bigStrip.getIndex();
		        		changeToFullScreenImage(index);
		            }
		        });
	}

	private void initSmallStripListener() {
		smallStrip.setMiddleSelected();
		smallStrip.setListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
		        		Property property = event.getProperty();
		        		ImageStrip.Image value = (ImageStrip.Image) property.getValue();
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

	protected void scrollToRight(int i) {
		smallStrip.scrollToLeft(i);
		bigStrip.scrollToLeft(i);
		if(metadataVisible){
			metadatawindow.update(new File(MyUtil.getFilename(urls[bigStrip.getIndex()])));
		}
		this.imgMetaDataLabel.setValue(getImgMetaDataLabelText());
	}

	protected void scrollToLeft(int i) {
		smallStrip.scrollToRight(i);
		bigStrip.scrollToRight(i);
		if(metadataVisible){
			metadatawindow.update(new File(MyUtil.getFilename(urls[bigStrip.getIndex()])));
		}
		this.imgMetaDataLabel.setValue(getImgMetaDataLabelText());
	}
	
	private String getImgMetaDataLabelText() {
		return "<center>"+MetadataViewer.getImageLabelText(new File(MyUtil.getFilename(urls[bigStrip.getIndex()]))) + "</center>";
	}

	protected void changeToFullScreenImage(int index) {		
		System.out.println(urls);
		System.out.println(index);
		String url = urls[index];
		System.out.println(url);
		FileResource res = MyUtil.getFileResource(url);
		Image image = new Image(url,res);
		image.setWidth("100%");
		image.addClickListener(new ClickListener() {
			@Override
			public void click(ClickEvent event) {
				closeFullscreenView();
			}
        });
		fullscreenlayout.removeAllComponents();
		fullscreenlayout.addComponent(image);			
		fullscreenlayout.setWidth("100%");
		this.setContent(fullscreenlayout);
	}

	protected void closeFullscreenView() {
		setContent(gridLayout);
	}

	private Button initMetaDataButton() {
		final Button metadatabutton = new Button("Show metadata");

        metadatabutton.addClickListener(new Button.ClickListener() {
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
        metadatabutton.setWidth("100px");
        return metadatabutton;
	}
	
	protected void hideMetadata() {        
		absLayout.removeComponent(metadatawindow);
	}

	protected void showMetadata() {       
		metadatawindow = new MetadataViewer(new File(MyUtil.getFilename(urls[bigStrip.getIndex()])));
		absLayout.addComponent(metadatawindow, "top:13%; left:25%; right:25%");
		metadatawindow.setHeight(imgSize*3 + "px");
	}
		
	/*	private ImageStrip.Image getBigStripImage(int indexIn){
	return this.bigStripImageList.get(indexIn);
}*/

	
/*	private void changeImage(int indexIn){
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
	}*/

}