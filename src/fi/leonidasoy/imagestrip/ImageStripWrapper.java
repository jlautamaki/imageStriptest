package fi.leonidasoy.imagestrip;

import java.util.ArrayList;

import org.vaadin.peter.imagestrip.ImageStrip;
import org.vaadin.peter.imagestrip.ImageStrip.Image;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;

public class ImageStripWrapper {

	private ArrayList<Image> images = new ArrayList<Image>();
	private ImageStrip strip;
	//original imageurls
	final private String[] urls;
	//current offset to original
	private int offset;
	//are images cropped?
	final private boolean cropImages;
	//max dimension of images
	final private int imgSize;
	//number of images visible
	final private int numberOfImages;
	private ValueChangeListener listener = null;
	
	public ImageStripWrapper(String[] urls, int imgSize, int numberOfImages, int offset, boolean cropImages) {
		this.urls=urls;
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
       striptmp.setImageBoxWidth(imgSize);
       striptmp.setImageBoxHeight(imgSize);

       // Set maximum size of the images
       striptmp.setImageMaxWidth(imgSize);
       striptmp.setImageMaxHeight(imgSize);
       
       striptmp.setHeight(imgSize, Unit.PIXELS);
       
       // Limit how many images are visible at most simultaneously
       striptmp.setMaxAllowed(this.numberOfImages);        	
       if (urls != null){
    	   images.clear();
	        for(int i=0; i<urls.length; i++){
	        	String url = urls[calculateUrlIndex(i)];
	        	striptmp = addImage(striptmp, url,this.cropImages,this.imgSize);        	
	        }
       }
       striptmp.setSelectable(true);
       if (this.listener!=null){
   			strip.addValueChangeListener(listener);			
       }
       strip = striptmp;
	}

	public int calculateUrlIndex(int i) {
		//System.out.println("index = "+i+", offset = " + offset);
		return (i+offset)%urls.length;
	}


	private ImageStrip addImage(ImageStrip tmpStrip, String url, boolean cropImages, int imgSize){
		Image img;
		if (cropImages){
			img = tmpStrip.addImage(MyUtil.getCroppedFile(url,imgSize));			
		}else{
			img = tmpStrip.addImage(MyUtil.getScaledFile(url,imgSize));						
		}
		images.add(img);
		return tmpStrip;
	}

	public Component getComponent() {
		return strip; 
	}

	public int getIndex() {
		setSomethingElseSelected();
		return offset;
	}
	
	private void setSomethingElseSelected() {
		strip.removeValueChangeListener(listener);			
		int value = (this.offset+urls.length/2)%urls.length;
		strip.setValue(images.get(value));
		strip.addValueChangeListener(listener);			
	}

	public void setListener(ValueChangeListener listener) {
		this.listener  = listener;
		strip.addValueChangeListener(listener);			
	}

	public int getMiddleOffset() {
		return this.numberOfImages/2;
	}


	public void scrollToLeft(int i) {
		i=1;
		offset=(offset+i)%urls.length;
		strip.scrollToLeft();
		setSomethingElseSelected();
	}


	public void scrollToRight(int i) {
		i=1;
		offset--;
		if (offset<0){
			offset+=urls.length;
		}
		strip.scrollToRight();
		setSomethingElseSelected();
	}

	
	public int offsetComparedToMiddle(int clickedindex) {
		int middleOffset = getMiddleOffset();
		int offser = offset;
		int value = clickedindex-offset;
		if (value < 0){
			value+=urls.length;
		}
		value-=middleOffset;
		return value;
	}
}


