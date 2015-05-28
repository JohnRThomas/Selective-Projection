package selpro.frames;

import static org.bytedeco.javacpp.opencv_core.cvScalar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_photo;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import selpro.CameraReader;

public class ControlFrame extends JFrame implements Drawable, CameraReader.FrameListener{
	private static final long serialVersionUID = 0L;
	private ProjectionFrame projectionFrame;
	private DisplayFrame displayFrame;
	private DrawState drawState = DrawState.CHECKER;
	//private OpenCVFrameConverter.ToMat matConverter = new OpenCVFrameConverter.ToMat();
	private OpenCVFrameConverter.ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();
	private static CvScalar threshMin = cvScalar(100, 100, 100, 0);//BGR-A
	private static CvScalar threshMax = cvScalar(255, 255, 255, 0);//BGR-A
	private static CvScalar detectMin = cvScalar(180, 180, 180, 0);//BGR-A
	private static CvScalar detectMax = cvScalar(255, 255, 255, 0);//BGR-A

	protected boolean findProjection = false;
	protected boolean saveMask = false;

	public ControlFrame(String title, ProjectionFrame projectionFrame, DisplayFrame displayFrame) {
		super(title);
		this.projectionFrame = projectionFrame;
		this.displayFrame = displayFrame;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640, 480);
		JPanel mainPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JPanel sliderPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		add(mainPanel);
		addButtons(buttonPanel);
		addSliders(sliderPanel);
		mainPanel.add(buttonPanel);
		mainPanel.add(sliderPanel);
	}

	private void addButtons(JPanel panel) {
		JPanel topPanel = new JPanel();
		JPanel midPanel = new JPanel();
		JPanel botPanel = new JPanel();
		panel.add(topPanel);
		panel.add(midPanel);
		panel.add(botPanel);

		JButton findProjection_btn = new JButton("Find Projection");
		findProjection_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.WHITE_FILL;
				projectionFrame.invalidate();
				projectionFrame.repaint();
				findProjection = true;
			}
		});

		topPanel.add(findProjection_btn);

		JButton red_btn = new JButton("RED");
		red_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.RED_FILL;
				projectionFrame.invalidate();
				projectionFrame.repaint();
			}
		});

		midPanel.add(red_btn);

		JButton green_btn = new JButton("GREEN");
		green_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.GREEN_FILL;
				projectionFrame.invalidate();
				projectionFrame.repaint();
			}
		});

		midPanel.add(green_btn);

		JButton blue_btn = new JButton("BLUE");
		blue_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.BLUE_FILL;
				projectionFrame.invalidate();
				projectionFrame.repaint();
			}
		});

		midPanel.add(blue_btn);

		JButton white_btn = new JButton("WHITE");
		white_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.WHITE_FILL;
				projectionFrame.invalidate();
				projectionFrame.repaint();
			}
		});

		midPanel.add(white_btn);

		JButton checker_btn = new JButton("CHECKER");
		checker_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.CHECKER;
				projectionFrame.invalidate();
				projectionFrame.repaint();
			}
		});

		midPanel.add(checker_btn);

		JButton saveMask_btn = new JButton("Save Mask");
		saveMask_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

		botPanel.add(saveMask_btn);

		JButton clearMask_btn = new JButton("Clear Mask");
		clearMask_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

		botPanel.add(clearMask_btn);

		JButton show_btn = new JButton("Show texture");
		show_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

		botPanel.add(show_btn);
	}
	public void addSliders(JPanel panel){
		JLabel red_lbl = new JLabel("Red: 100.0-255.0");
		RangeSlider red_sld = new RangeSlider(100, 255, 0, 255);
		red_sld.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				red_lbl.setText("Red: " + threshMin.red(red_sld.getValue()).red() + "-" + threshMax.red(red_sld.getUpperValue()).red());

			}
		});

		JLabel green_lbl = new JLabel("Green: 100.0-255.0");
		RangeSlider green_sld = new RangeSlider(100, 255, 0, 255);
		green_sld.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				green_lbl.setText("Green: " + threshMin.green(green_sld.getValue()).green() + "-" + threshMax.green(green_sld.getUpperValue()).green());
			}
		});

		JLabel blue_lbl = new JLabel("Blue: 100.0-255.0");
		RangeSlider blue_sld = new RangeSlider(100, 255, 0, 255);
		blue_sld.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				blue_lbl.setText("Blue: " + threshMin.blue(blue_sld.getValue()).blue() + "-" + threshMax.blue(blue_sld.getUpperValue()).blue());
			}
		});
		panel.add(red_lbl);
		panel.add(red_sld);
		panel.add(green_lbl);
		panel.add(green_sld);
		panel.add(blue_lbl);
		panel.add(blue_sld);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void processFrame(Frame img) {
		try {
			/*
			 * Projection Mapping
			 */
			Mat img_mat = iplConverter.convertToMat(img);
			Mat gray_mat = new Mat();

			displayFrame.showImage(img);
			Thread.sleep(1000);
			opencv_imgproc.cvtColor(img_mat, gray_mat, opencv_imgproc.CV_BGR2GRAY);
			displayFrame.showImage(iplConverter.convert(gray_mat));

			opencv_photo.fastNlMeansDenoising(gray_mat, gray_mat);
			displayFrame.showImage(iplConverter.convert(gray_mat));
			Thread.sleep(1000);
			opencv_imgproc.equalizeHist(gray_mat, gray_mat);
			displayFrame.showImage(iplConverter.convert(gray_mat));
			opencv_photo.fastNlMeansDenoising(gray_mat, gray_mat);
			displayFrame.showImage(iplConverter.convert(gray_mat));


			Mat bw_mat = new Mat();
			opencv_imgproc.Canny(gray_mat, bw_mat, 30, 90, 3, true);
			displayFrame.showImage(iplConverter.convert(bw_mat));
			Thread.sleep(1000);

			//opencv_imgproc.dilate(bw_mat, bw_mat, new Mat(), new Point(1, 1), 1, 1, new Scalar(1));

			MatVector contours = new MatVector();
			Mat approx_mat = new Mat();
			opencv_imgproc.findContours(bw_mat.clone(), contours, opencv_imgproc.CV_RETR_EXTERNAL, opencv_imgproc.CV_CHAIN_APPROX_SIMPLE);


			Mat dst_mat = img_mat.clone();
			for(long i = 0; i < contours.size(); i++){
				Mat tmp_mat = new Mat(contours.get(i));
				// Approximate contour with accuracy proportional to the contour perimeter
				opencv_imgproc.approxPolyDP(tmp_mat, approx_mat, opencv_imgproc.arcLength(tmp_mat, true)*0.02, true);

				// Skip small or non-convex objects 
				if (Math.abs(opencv_imgproc.contourArea(tmp_mat)) < 100 || !opencv_imgproc.isContourConvex(approx_mat)){
					continue;
				}

				// Number of vertices of polygonal curve
				int vtc = approx_mat.arrayHeight();

				if (vtc == 4){
					float[] vec = new float[8];
					approx_mat.getFloatBuffer().get(vec);

					// Get the cosines of all corners
					LinkedList<Double> cos = new LinkedList<Double>();
					for (int x = 2; x < vtc + 1; x++){
						double dx1 = vec[(x-2)*2] - vec[(x % vtc)*2];
						double dy1 = vec[((x-2)*2)+1] - vec[((x % vtc)*2)+1];
						double dx2 = vec[(x-1)*2] - vec[(x % vtc)*2];
						double dy2 = vec[((x-1)*2)+1] - vec[((x % vtc)*2)+1];
						cos.push((dx1*dx2 + dy1*dy2)/Math.sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10));
					}

					// Sort ascending the cosine values
					cos.sort((a, b) -> a.compareTo(b));

					// Get the lowest and the highest cosine
					double mincos = cos.getFirst();
					double maxcos = cos.getLast();

					// Use the degrees obtained above and the number of vertices
					// to determine the shape of the contour
					//if (mincos >= -0.15 && maxcos <= 0.35){
					Rect r = opencv_imgproc.boundingRect(contours.get(i));
					opencv_core.rectangle(dst_mat, r, new Scalar(255,0,255,0));
					
					//}
				}
			}
			
			displayFrame.showImage(iplConverter.convert(dst_mat));
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//*/

		/*
		 * Mask finder
		 *
		IplImage img_ipl = iplConverter.convert(img);

		//create binary image of original size
		IplImage mask_ipl = opencv_core.cvCreateImage(opencv_core.cvGetSize(img_ipl), 8, 1);

		//apply thresholding
		opencv_core.cvInRangeS(img_ipl, threshMin, threshMax, mask_ipl);

		//smooth filter the mask
		opencv_imgproc.cvSmooth(mask_ipl, mask_ipl, opencv_imgproc.CV_MEDIAN, 7, 0, 0, 0);

		//Apply the mask to the output image
		Mat img_mat = iplConverter.convertToMat(img);
		Mat mask_mat = iplConverter.convertToMat(iplConverter.convert(mask_ipl));
		Mat final_mat = iplConverter.convertToMat(iplConverter.convert(mask_ipl));
		opencv_core.bitwise_and(img_mat, img_mat, final_mat, mask_mat);

		displayFrame.showImage(iplConverter.convert(final_mat));

		//*/
	}

	@Override
	public void draw(Graphics g, int width, int height) {
		switch(drawState){
		case CHECKER:
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
			g.setColor(Color.BLACK);
			for(int y = 0; y < height; y+=50){
				for(int x = 0; x < width; x+=50){
					if((x/10+y/10) % 2 == 1)g.fillRect(x, y, 50, 50);
				}	
			}
			break;
		case WHITE_BORDER:
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
			g.setColor(Color.BLACK);
			g.fillRect(30, 30, width-60, height-60);
			break;
		case WHITE_CORNERS:
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, width, height);
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, 50, 50);
			g.fillRect(0, height-50, 50, 50);
			g.fillRect(width-50, 0, 50, 50);
			g.fillRect(width-50, height-50, 50, 50);
			break;
		case RED_FILL:
			g.setColor(Color.RED);
			g.fillRect(0, 0, width, height);
			break;
		case GREEN_FILL:
			g.setColor(Color.GREEN);
			g.fillRect(0, 0, width, height);
			break;
		case BLUE_FILL:
			g.setColor(Color.BLUE);
			g.fillRect(0, 0, width, height);
			break;
		case WHITE_FILL:
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
			break;
		default:
			break;
		}
	}

	private enum DrawState{
		CHECKER(),
		RED_FILL(),
		GREEN_FILL(),
		BLUE_FILL(),
		WHITE_CORNERS(),
		WHITE_BORDER(),
		WHITE_FILL();
	}
}
