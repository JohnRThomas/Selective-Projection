package selpro;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class CameraReader extends Thread{
	private ArrayList<FrameListener> listeners = new ArrayList<FrameListener>();
	private volatile VideoCapture camera;
	public CameraReader(){
		camera = new VideoCapture(0); 
		camera.open(0);
		if(camera.isOpened()){
			this.start();
		}else{
			System.err.println("Failed to open camera 0!");
			System.exit(1);
		}

	}


	@Override
	public void run(){
		Mat img = new Mat();
		while (true) {
			camera.read(img);
			for(FrameListener l : listeners){
				if (img != null) {
					l.processFrame(img);
				}
			}
		}
	}			

	public void addFrameListener(FrameListener l){
		listeners.add(l);
	}

	public interface FrameListener{
		public void processFrame(Mat img);
	}

	public void changeGrabber(int c){
		VideoCapture temp = new VideoCapture(c);
		temp.open(c);
		if(temp.isOpened()){
			camera = temp;
		}
	}
}
