package fi.leonidasoy.imagestrip;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class ProgressBarLayout extends CssLayout{
	private ProgressBar bar = new ProgressBar(0.05f);
	private Label label = new Label("Starting up");
	private UI ui;
	private String currentJobName;
	private int progress;
	private int numberOfStepsInTask;
	private float sizeInPercentages;
	private boolean pushIncrediments;

	public ProgressBarLayout(UI ui, boolean pushIncrediments) {
		setSizeFull();
		this.pushIncrediments = pushIncrediments;
		
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
		label.setValue("<center>" + string + "</center>");
		if (pushIncrediments){
			ui.push();			
		}
	}

	public void doJobIncrediment() {
		progress++;
		float tmp = (float) progress/numberOfStepsInTask;
		float value = this.sizeInPercentages*tmp;
		System.out.println("tmp: " +tmp+ " current: " + value);
		bar.setValue(bar.getValue()+value);
		label.setValue("<center>" + currentJobName  + "  (" + progress + "/" + numberOfStepsInTask + ")</center>");	            	
		if (pushIncrediments){
			ui.push();			
		}
	}

	public void startJob(int numberOfSteps, float sizeInPercentages) {
		progress=0;
		this.sizeInPercentages = sizeInPercentages;
		numberOfStepsInTask = numberOfSteps;
		label.setValue("<center>" + currentJobName  + "  (" + progress + "/" + numberOfStepsInTask + ")</center>");	            	
		if (pushIncrediments){
			ui.push();			
		}
	}
}
