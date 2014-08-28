package fi.leonidasoy.imagestrip;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DuplicateRemover {
	//change this to true if you want to test
	static public final boolean useThisClass = false;
	
	//download all the pictures
	static public synchronized void downloadImages(){
		return;
	}
	
	//return list of filtered imagefiles
	static public synchronized List<MyImage> getFilteredFiles(int filterThreshold){
		List<MyImage> images = new ArrayList<MyImage>();

		//while all the images are filtered
		String filename="";
		File myImageFile = new File(filename);
		MyImage newImage = new MyImage(myImageFile);
		images.add(newImage);
		
		return images;
	}
}
