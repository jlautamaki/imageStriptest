package fi.leonidasoy.imagestrip;

import java.util.ArrayList;

import org.vaadin.peter.imagestrip.ImageStrip;
import org.vaadin.peter.imagestrip.ImageStrip.Image;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;

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
	
	public ImageStripWrapper(MyImage[] images, int imgSize, int numberOfImages, int offset, boolean cropImages) {
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
		//int value = (this.offset+urls.length/2)%urls.length;
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


	public void scrollToLeft(int i) {
		//i=1;
		offset=(offset+i)%images.length;
		strip.scrollToLeft();
		setMiddleSelected();
	}


	public void scrollToRight(int i) {
		//i=1;
		offset--;
		if (offset<0){
			offset+=images.length;
		}
		strip.scrollToRight();
		setMiddleSelected();
	}

	
	public int offsetComparedToMiddle(int clickedindex) {
		int middleOffset = getMiddleOffset();
		int value = clickedindex-offset;
		if (value < 0){
			value+=images.length;
		}
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


