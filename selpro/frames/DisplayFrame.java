package selpro.frames;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class DisplayFrame extends CanvasFrame implements Runnable{
	private static final long serialVersionUID = 0L;
	
	public DisplayFrame(String title) {
		super(title);
		setSize(640, 480);
	}

	@Override
	public void run(){
	    FrameGrabber grabber = new OpenCVFrameGrabber(0); 
	    try {
	        grabber.start();
	        Frame img;
	        while (true) {
	            img = grabber.grab();
	            if (img != null) {
	                showImage(img);
	            }
	        }
	    } catch (FrameGrabber.Exception e) {
	    	e.printStackTrace();
	    }

	}	
}
