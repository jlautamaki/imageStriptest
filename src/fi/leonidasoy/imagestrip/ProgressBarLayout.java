package fi.leonidasoy.imagestrip;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;

public class ProgressBarLayout extends CssLayout{
	private ProgressBar bar = new ProgressBar(0.05f);
	private Label label = new Label("Starting up");
	private UI ui;
	private String currentJobName;
	private float currentJobSize;
	private int progress;
	private int taskLength;

	public ProgressBarLayout(UI ui) {
		setSizeFull();

		this.ui = ui;
		
		bar.setStyleName("progressBar");
		bar.setWidth("300px");
		bar.setHeight("20px");

		label.setStyleName("progressLabel");
		label.setContentMode(ContentMode.HTML);
		label.setWidth("300px");

		addComponent(bar);
		addComponent(label);
	}

	public void setValue(String string,float f) {
		bar.setValue(f);
		currentJobName = string;
		currentJobSize = f;
		label.setValue("<center>" + string + "</center>");	            	
	}

	public void doJobIncrediment() {
		progress++;
		label.setValue("<center>" + currentJobName  + "  (" + progress + "/" + taskLength + ")</center>");	            	
	}

	public void startJob(int length) {
		progress=0;
		taskLength = length;
		label.setValue("<center>" + currentJobName  + "  (" + progress + "/" + taskLength + ")</center>");	            	
		bar.setValue(bar.getValue()+currentJobSize/taskLength);
	}
}
