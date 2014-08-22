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
	// current offset to original
	private int offset;
	// are images cropped?
	final private boolean cropImages;
	// max dimension of images
	private int imgSize;
	// number of images visible
	private int numberOfVisibleImages;
	private ValueChangeListener listener = null;
	final private List<MyImage> images;
	private ArrayList<ImageStrip.Image> imagesAddedToStrip = new ArrayList<ImageStrip.Image>();
	final private String styleName;
	private int originalImgSize;

	public ImageStripWrapper(String styleName, List<MyImage> images,
			int imgSize, int numberOfVisibleImages, int offset, boolean cropImages) {
		this.styleName = styleName;
		this.images = images;
		this.originalImgSize = imgSize;
		this.imgSize = imgSize;
		this.numberOfVisibleImages = numberOfVisibleImages;
		this.offset = offset;
		this.cropImages = cropImages;
		initStrip();
	}

	public void resize(int imgSize, int numberOfVisibleImages, int offset) {
		this.imgSize = imgSize;
		this.offset=offset;
		if (originalImgSize>imgSize && this.numberOfVisibleImages == numberOfVisibleImages){
			refreshStripSize();
		}else{
			this.originalImgSize=this.imgSize;
			this.numberOfVisibleImages = numberOfVisibleImages;
			initStrip();			
		}
		this.setListener(listener);
	}

	private void initStrip() {
		// Create new horizontally aligned strip of images
		this.strip = new ImageStrip();
		this.strip.setStyleName(this.styleName);

		// Use animation
		this.strip.setAnimated(true);

		// Make strip to behave like select
		this.strip.setSelectable(true);

		refreshStripSize();

		// Limit how many images are visible at most simultaneously
		strip.setMaxAllowed(this.numberOfVisibleImages);

		this.imagesAddedToStrip.clear();
		for (int i = 0; i < this.images.size(); i++) {
			MyImage img = images.get(calculateUrlIndex(i));
			addImage(img, this.cropImages, this.imgSize);
		}

		// striptmp.setImmediate(false);
		strip.setSelectable(true);
		if (this.listener != null) {
			strip.addValueChangeListener(listener);
		}
	}

	private void refreshStripSize() {

		// Set size of the box surrounding the images
		strip.setImageBoxWidth(imgSize + 10);
		strip.setImageBoxHeight(imgSize + 10);

		// Set maximum size of the images
		strip.setImageMaxWidth(imgSize);
		strip.setImageMaxHeight(imgSize);

		strip.setHeight(imgSize, Unit.PIXELS);
	}

	public int calculateUrlIndex(int i) {
		// System.out.println("index = "+i+", offset = " + offset);
		int length = images.size();
		int value = (i + offset + length) % length;
		return value;
	}

	private void addImage(MyImage myimg,
			boolean cropImages, int imgSize) {
		Image img;
		if (cropImages) {
			img = strip.addImage(myimg.getCroppedFileResource(imgSize));
		} else {
			img = strip.addImage(myimg.getScaledFileResource(imgSize));
		}
		this.imagesAddedToStrip.add(img);
	}

	public Component getComponent() {
		return strip;
	}

	public int getIndex() {
		setMiddleSelected();
		return (offset + images.size()) % images.size();
	}

	void setMiddleSelected() {
		strip.removeValueChangeListener(listener);
		int value = (this.offset + getMiddleOffset() + images.size())
				% images.size();
		strip.setValue(this.imagesAddedToStrip.get(value));
		if (listener != null) {
			strip.addValueChangeListener(listener);
		}
	}

	public void setListener(ValueChangeListener listener) {
		this.listener = listener;
		strip.addValueChangeListener(listener);
	}

	public int getMiddleOffset() {
		return this.numberOfVisibleImages / 2;
	}

	public void scrollToLeft() {
		strip.scrollToLeft();
		offset = (offset + 1) % images.size();
		setMiddleSelected();
	}

	public void scrollToRight() {
		strip.scrollToRight();
		offset = (offset - 1 + images.size()) % images.size();
		setMiddleSelected();
	}

	public int offsetComparedToMiddle(int clickedindex) {
		int value = clickedindex - offset;
		if (value < 0) {
			value += images.size();
		}
		// Component indexes from right, but we would like to compare to middle
		// element
		int middleOffset = getMiddleOffset();
		value -= middleOffset;
		return value;
	}

	public Unit getHeightUnits() {
		return strip.getHeightUnits();
	}

	public float getHeight() {
		return strip.getHeight();
	}
}