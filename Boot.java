import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;


public class Boot {
    private static CanvasFrame canvas = new CanvasFrame("Web Cam");
	
	public static void main(String args[]){
		canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		canvas.setSize(640, 480);
        FrameGrabber grabber = new OpenCVFrameGrabber(0); 
        try {
            grabber.start();
            Frame img;
            while (true) {
                img = grabber.grab();
                if (img != null) {
                    canvas.showImage(img);
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
}
