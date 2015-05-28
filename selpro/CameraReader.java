package selpro;

import java.util.ArrayList;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class CameraReader extends Thread{
	private ArrayList<FrameListener> listeners = new ArrayList<FrameListener>();
	private FrameGrabber grabber;
	public CameraReader(){
		grabber = new OpenCVFrameGrabber(0); 
		try {
			grabber.start();
			this.start();
		} catch (FrameGrabber.Exception e) {
			e.printStackTrace();
		}

	}


	@Override
	public void run(){
		try {
			Frame img;
			while (true) {
				img = grabber.grab();
				if (img != null) {
					for(FrameListener l : listeners){
						l.processFrame(img);
					}
				}
			}
		} catch (FrameGrabber.Exception e) {
			e.printStackTrace();
		}

	}			

	public void addFrameListener(FrameListener l){
		listeners.add(l);
	}

	public interface FrameListener{
		public void processFrame(Frame img);
	}
}