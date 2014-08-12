package fi.leonidasoy.imagestrip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.vaadin.peter.imagestrip.ImageStrip;
import org.vaadin.peter.imagestrip.ImageStrip.Image;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class ImageStripWrapper implements Serializable {

	// current offset to original
	private int offset;
	// are images cropped?
	final private boolean cropImages;
	// max dimension of images
	private int imgSize;
	// number of images visible
	private int maxAllowed;
	private ValueChangeListener listener = null;
	final private List<MyImage> images;
	final private String styleName;

	private ArrayList<ImageStrip.Image> imagesAddedToStrip = new ArrayList<ImageStrip.Image>();
	private ImageStrip component = null;

	public ImageStripWrapper(String styleName, List<MyImage> images,
			int imgSize, int maxAllowed, int offset, boolean cropImages,
			ProgressBarLayout progressBar, ValueChangeListener valueChangeListener, UI ui) {
		this.styleName = styleName;
		this.images = images;
		this.offset = offset;
		this.cropImages = cropImages;
		this.imgSize = imgSize;
		this.maxAllowed = maxAllowed;
		this.listener = valueChangeListener;
		createStrip(progressBar, ui);
	}

	private void createStrip(ProgressBarLayout progressBar,UI ui) {
		this.imagesAddedToStrip.clear();
		component = new ImageStrip();
		component.setStyleName(this.styleName);
		component.setAnimated(true);
		this.imagesAddedToStrip.clear();
		if (progressBar != null) {
			progressBar.startJob(images.size(), 0.25f);
		}
		updateStrip(ui);		

		for (int i = 0; i < this.images.size(); i++) {
			MyImage img = images.get(calculateUrlIndex(i));
			addImage(img, this.cropImages, this.imgSize);
			if (progressBar != null) {
				progressBar.doJobIncrediment();
			}
		}
		component.setSelectable(true);
		component.addValueChangeListener(listener);
	}

	private void updateStrip(UI ui) {
		// Set size of the box surrounding the images
		component.setImageBoxWidth(imgSize + 10);
		component.setImageBoxHeight(imgSize + 10);

		// Set maximum size of the images
		component.setImageMaxWidth(imgSize);
		component.setImageMaxHeight(imgSize);

		component.setHeight(imgSize, Unit.PIXELS);

		// Limit how many images are visible at most simultaneously
		component.setMaxAllowed(this.maxAllowed);
		/*int moveToLeft = this.maxAllowed/2 - this.currentMiddle;
		if (moveToLeft>0){
        	scrollToRight(Math.abs(moveToLeft));
		}else if (moveToLeft<0){
			scrollToLeft(Math.abs(moveToLeft));
		}*/
	}

	public int calculateUrlIndex(int i) {
		int length = images.size();
		int value = (i + offset + length) % length;
		return value;
	}

	private void addImage(MyImage myimg,
			boolean cropImages, int imgSize) {
		Image img;
		if (cropImages) {
			img = this.component.addImage(myimg.getCroppedFileResource(imgSize));
		} else {
			img = this.component.addImage(myimg.getScaledFileResource(imgSize));
		}
		this.imagesAddedToStrip.add(img);
	}

	public Component getComponent(ProgressBarLayout progressBar) {
		return component;
	}

	public int getIndex() {
		setMiddleSelected();
		return (offset + images.size()) % images.size();
	}

	void setMiddleSelected() {
		component.removeValueChangeListener(listener);			
		int value = (this.offset+getMiddleOffset()+images.size())%images.size();
		component.setValue(this.imagesAddedToStrip.get(value));
		if (listener!=null){
			component.addValueChangeListener(listener);						
		}
	}
	
	public int getMiddleOffset() {
		return this.maxAllowed / 2;
	}
		
	public void scrollToLeft(){
		component.scrollToLeft();
		offset=(offset+1)%images.size();
		setMiddleSelected();
	}
	
	public void scrollToRight() {
		component.scrollToRight();
		offset=(offset-1+images.size())%images.size();
		setMiddleSelected();
	}
	
	public Unit getHeightUnits() {
		return getComponent(null).getHeightUnits();
	}

	public float getHeight() {
		return getComponent(null).getHeight();
	}

	public void updateSize(int imgSize, int maxAllowed, int offset, UI ui) {
		this.offset = offset;
		this.imgSize = imgSize;
		this.maxAllowed = maxAllowed;
		this.updateStrip(ui);
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
	}}
