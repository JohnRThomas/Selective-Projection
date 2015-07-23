package selpro.boot;

import javax.swing.UIManager;

import org.opencv.core.Core;

import selpro.CameraReader;
import selpro.frames.ControlFrame;
import selpro.frames.ProjectionFrame;

public class Main {

	public static void main(String args[]){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
		//Set the window theming
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		//open up a connection to an available webcam
		CameraReader camera = new CameraReader();	
		
		//Create the three frames
		ProjectionFrame projectionFrame = new ProjectionFrame("Projector Output");
		ControlFrame controlFrame = new ControlFrame("Control Panel", projectionFrame);
	
		//Set where the camera callback send it's frames
		camera.addFrameListener(controlFrame);
		
		
		//Show the frames and move them to their proper locations relative to each other.
		controlFrame.setLocation(3065, 485);
		controlFrame.setVisible(true);
		projectionFrame.setLocation(controlFrame.getX() + controlFrame.getWidth(), controlFrame.getY());
		projectionFrame.setVisible(true);
		
		/*try {
			Mat img = Imgcodecs.imread("/home/john2/Pictures/firstex.jpg");
			while(true){
				controlFrame.processFrame(img);
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
