package com.example.imagestriptest;

import java.io.File;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ImageViewer extends Window implements ClickListener {
			
	//AbsoluteLayout layout = new AbsoluteLayout();
	AbsoluteLayout absLayout = new AbsoluteLayout();
	VerticalLayout vertLayout = new VerticalLayout();
	Panel panel = new Panel("Metadata Panel");
	private String url;
	private Button b2;
	private Button b1;
	private MetadataViewer metadatawindow;

    public ImageViewer(String url) {
    	this.url = url;
    }

	@Override
    public void attach() {
        super.attach();
        
        this.setHeight("95%");
        this.setWidth("95%");
 
        ExternalResource resource = new ExternalResource(url);        
        this.setContent(vertLayout);    
    	Image image = new Image("", resource);
    	image.setSizeFull();
   

        this.addClickListener(this);
        image.addClickListener(this);

        b1 = new Button("Show metadata");
        b1.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				showMetadata();
			}
        });
        
        b2 = new Button("Hide metadata");
        b2.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				hideMetadata();
			}
        });
        b1.setVisible(true);
        b2.setVisible(false);
        vertLayout.addComponent(b1);//,"top:0px; left:0px");
        vertLayout.addComponent(b2);//,"top:0px; left:0px");
    	absLayout.addComponent(image);
        vertLayout.addComponent(absLayout);
        absLayout.setHeight("500px");
 
    }

	protected void hideMetadata() {        
		b2.setVisible(false);
		absLayout.removeComponent(metadatawindow);
		b1.setVisible(true);
	}

	protected void showMetadata() {       
        b1.setVisible(false);
		metadatawindow = new MetadataViewer(new File(MyUtil.getFilename(url)));
		absLayout.addComponent(metadatawindow, "top:0%; left:5%");
		metadatawindow.setHeight("400px");
		metadatawindow.setWidth("95%");
		b2.setVisible(true);		
	}

	@Override
	public void click(ClickEvent event) {
		this.close();
	}		
}