package selpro.boot;

import javax.swing.UIManager;

import selpro.CameraReader;
import selpro.frames.ControlFrame;
import selpro.frames.DisplayFrame;
import selpro.frames.ProjectionFrame;

public class Main {

	public static void main(String args[]){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		CameraReader camera = new CameraReader();	
		ProjectionFrame projectionFrame = new ProjectionFrame("Projector Output");
		DisplayFrame displayFrame = new DisplayFrame("Camera");
		ControlFrame controlFrame = new ControlFrame("Control Panel", projectionFrame, displayFrame);
		camera.addFrameListener(controlFrame);
		controlFrame.setVisible(true);

		displayFrame.setLocation(controlFrame.getWidth()+controlFrame.getX(), controlFrame.getY());
		displayFrame.setVisible(true);

		projectionFrame.setLocation(displayFrame.getWidth()+displayFrame.getX(), displayFrame.getY());
		projectionFrame.setDrawer(controlFrame);
		projectionFrame.setVisible(true);

	}
}
