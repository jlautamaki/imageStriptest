package fi.leonidasoy.imagestrip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vaadin.peter.imagestrip.ImageStrip;
import org.vaadin.peter.imagestrip.ImageStrip.Image;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class ImageStripWrapper implements Serializable {

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
	final private List<MyImage> images;
	private ArrayList<ImageStrip.Image> imagesAddedToStrip = new ArrayList<ImageStrip.Image>();
	final private String styleName;
	
	public ImageStripWrapper(String styleName, List<MyImage> images, int imgSize, int numberOfImages, int offset, boolean cropImages, ProgressBarLayout progressBar) {
		this.styleName = styleName;
		this.images=images;
		this.offset = offset;
		this.cropImages = cropImages;
		this.imgSize = imgSize;
		this.numberOfImages = numberOfImages;
		initStrip(progressBar);
	}
	
	private void initStrip(ProgressBarLayout progressBar) {
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
       progressBar.startJob(images.size(),0.25f);
        for(int i=0; i<this.images.size(); i++){
        	MyImage img = images.get(calculateUrlIndex(i));
        	striptmp = addImage(striptmp, img,this.cropImages,this.imgSize);        	
        	progressBar.doJobIncrediment();
        }

       //striptmp.setImmediate(false);
       striptmp.setSelectable(true);
       if (this.listener!=null){
   			strip.addValueChangeListener(listener);			
       }
       strip = striptmp;
	}

	public int calculateUrlIndex(int i) {
		//System.out.println("index = "+i+", offset = " + offset);
		int length = images.size();
		int value =  (i+offset+length)%length;
		return value;
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
		return (offset+images.size())%images.size();
	}
	
	void setMiddleSelected() {
		strip.removeValueChangeListener(listener);			
		int value = (this.offset+getMiddleOffset()+images.size())%images.size();
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

	public void scrollToLeft(){
		strip.scrollToLeft();
		offset=(offset+1)%images.size();
		setMiddleSelected();
	}
	
	public void scrollToRight() {
		strip.scrollToRight();
		offset=(offset-1+images.size())%images.size();
		setMiddleSelected();
	}
	
	public int offsetComparedToMiddle(int clickedindex) {
		int value = clickedindex-offset;
		if (value < 0){
			value+=images.size();
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


