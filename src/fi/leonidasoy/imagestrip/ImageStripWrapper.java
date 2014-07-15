package fi.leonidasoy.imagestrip;

import java.util.ArrayList;

import org.vaadin.cssinject.CSSInject;
import org.vaadin.peter.imagestrip.ImageStrip;
import org.vaadin.peter.imagestrip.ImageStrip.Image;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

public class ImageStripWrapper {

	private ImageStrip strip;
	//current offset to original
	private int offset;
	//are images cropped?
	final private boolean cropImages;
	//max dimension of images
	final private int imgSize;
	//number of images visible
	final private int numberOfImages;
	private ValueChangeListener listener = null;
	final private MyImage[] images;
	private ArrayList<ImageStrip.Image> imagesAddedToStrip = new ArrayList<ImageStrip.Image>();
	final private String styleName;
	
	public ImageStripWrapper(String styleName, MyImage[] images, int imgSize, int numberOfImages, int offset, boolean cropImages) {
		this.styleName = styleName;
		this.images=images;
		this.offset = offset;
		this.cropImages = cropImages;
		this.imgSize = imgSize;
		this.numberOfImages = numberOfImages;
		reCreateStrip();
	}
	
	public void reCreateStrip() {
	     // Create new horizontally aligned strip of images
       ImageStrip striptmp = new ImageStrip();
       striptmp.setStyleName(this.styleName);
       
       // Use animation
       striptmp.setAnimated(true);

       // Make strip to behave like select
       striptmp.setSelectable(true);
       
       // Set size of the box surrounding the images
       striptmp.setImageBoxWidth(imgSize+10);
       striptmp.setImageBoxHeight(imgSize+10);

       // Set maximum size of the images
       striptmp.setImageMaxWidth(imgSize);
       striptmp.setImageMaxHeight(imgSize);
       
       striptmp.setHeight(imgSize, Unit.PIXELS);
       
       // Limit how many images are visible at most simultaneously
       striptmp.setMaxAllowed(this.numberOfImages);        	
	   
       this.imagesAddedToStrip.clear();
        for(int i=0; i<this.images.length; i++){
        	MyImage img = images[calculateUrlIndex(i)];
        	striptmp = addImage(striptmp, img,this.cropImages,this.imgSize);        	
        }

       striptmp.setSelectable(true);
       if (this.listener!=null){
   			strip.addValueChangeListener(listener);			
       }
       strip = striptmp;
	}

	public int calculateUrlIndex(int i) {
		//System.out.println("index = "+i+", offset = " + offset);
		return (i+offset)%images.length;
	}


	private ImageStrip addImage(ImageStrip tmpStrip, MyImage myimg, boolean cropImages, int imgSize){
		Image img;
		if (cropImages){
			img = tmpStrip.addImage(myimg.getCroppedFileResource(imgSize));			
		}else{
			img = tmpStrip.addImage(myimg.getScaledFileResource(imgSize));						
		}
		this.imagesAddedToStrip.add(img);
		return tmpStrip;
	}

	public Component getComponent() {
		return strip; 
	}

	public int getIndex() {
		setMiddleSelected();
		return offset;
	}
	
	void setMiddleSelected() {
		strip.removeValueChangeListener(listener);			
		int value = (this.offset+getMiddleOffset())%images.length;
		strip.setValue(this.imagesAddedToStrip.get(value));
		if (listener!=null){
			strip.addValueChangeListener(listener);						
		}
	}

	public void setListener(ValueChangeListener listener) {
		this.listener  = listener;
		strip.addValueChangeListener(listener);			
	}

	public int getMiddleOffset() {
		return this.numberOfImages/2;
	}


	public void scrollToLeft(int i, UI ui) {
		System.out.println("Scrolling left " +i + " times.");
		while(i>0){
			offset++;
			offset=offset%images.length;
			strip.scrollToLeft();
			setMiddleSelected();
			i--;
		}
		fixCss(ui);
	}


	private void fixCss(UI ui) {
		// should be width: 810px; height: 140px; top: 0px; left: 182px;	
		CSSInject css = new CSSInject(ui);
		css.setStyles(".v-strip .selectable {width: 810px !important; left: 182px !important;}");
	}

	public void scrollToRight(int i, UI ui) {
		System.out.println("Scrolling right " +i + " times.");
		while(i>0){
			offset--;
			if (offset<0){
				offset+=images.length;
			}
			strip.scrollToRight();
			setMiddleSelected();
			i--;
		}
		fixCss(ui);
	}

	
	public int offsetComparedToMiddle(int clickedindex) {
		int value = clickedindex-offset;
		if (value < 0){
			value+=images.length;
		}
		//Component indexes from right, but we would like to compare to middle element 
		int middleOffset = getMiddleOffset();
		value-=middleOffset;
		return value;
	}

	public Unit getHeightUnits() {
		return strip.getHeightUnits();
	}

	public float getHeight() {
		return strip.getHeight();
	}
}


