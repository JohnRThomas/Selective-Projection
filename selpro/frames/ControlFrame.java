package selpro.frames;

import static org.bytedeco.javacpp.opencv_core.cvScalar;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_photo;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import selpro.CameraReader;

public class ControlFrame extends JFrame implements CameraReader.FrameListener{
	private static final long serialVersionUID = 0L;
	private ProjectionFrame projectionFrame;
	private DisplayFrame displayFrame;
	private OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
	private static CvScalar threshMin = cvScalar(0, 0, 0, 0);//BGR-A
	private static CvScalar threshMax = cvScalar(255, 255, 255, 0);//BGR-A

	private Mat projectionContour = null;
	private Mat projectionMask = null;
	private boolean findProjection = false;
	private boolean saveMask = false;
	private DrawState drawState = DrawState.CHECKER;

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
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {}
				findProjection = true;
			}
		});

		topPanel.add(findProjection_btn);

		JButton red_btn = new JButton("RED");
		red_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.RED_FILL;
			}
		});

		midPanel.add(red_btn);

		JButton green_btn = new JButton("GREEN");
		green_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.GREEN_FILL;
			}
		});

		midPanel.add(green_btn);

		JButton blue_btn = new JButton("BLUE");
		blue_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.BLUE_FILL;
			}
		});

		midPanel.add(blue_btn);

		JButton white_btn = new JButton("WHITE");
		white_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.WHITE_FILL;
			}
		});

		midPanel.add(white_btn);

		JButton checker_btn = new JButton("CHECKER");
		checker_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.CHECKER;
			}
		});

		midPanel.add(checker_btn);

		JButton applyMask_btn = new JButton("Apply Mask");
		applyMask_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveMask = true;
				drawState = DrawState.CHECKER;
			}
		});

		botPanel.add(applyMask_btn);

		JButton clearMask_btn = new JButton("Clear Mask");
		clearMask_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				projectionMask = null;
			}
		});

		botPanel.add(clearMask_btn);
	}
	public void addSliders(JPanel panel){
		JLabel red_lbl = new JLabel("Red: 0.0-255.0");
		RangeSlider red_sld = new RangeSlider(0, 255, 0, 255);
		red_sld.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				red_lbl.setText("Red: " + threshMin.red(red_sld.getValue()).red() + "-" + threshMax.red(red_sld.getUpperValue()).red());

			}
		});

		JLabel green_lbl = new JLabel("Green: 0.0-255.0");
		RangeSlider green_sld = new RangeSlider(0, 255, 0, 255);
		green_sld.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				green_lbl.setText("Green: " + threshMin.green(green_sld.getValue()).green() + "-" + threshMax.green(green_sld.getUpperValue()).green());
			}
		});

		JLabel blue_lbl = new JLabel("Blue: 0.0-255.0");
		RangeSlider blue_sld = new RangeSlider(0, 255, 0, 255);
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
	public void processFrame(Frame img) {
		Mat img_mat = converter.convertToMat(img);
		IplImage img_ipl = converter.convert(img);

		if(findProjection){
			/*
			 * Projection Mapping
			 */
			Mat gray_mat = new Mat();

			//Show the original frame
			displayFrame.showImage(img);
			try {Thread.sleep(1000);} catch (InterruptedException e) {}


			//Change to grayscale
			opencv_imgproc.cvtColor(img_mat, gray_mat, opencv_imgproc.CV_BGR2GRAY);
			displayFrame.showImage(converter.convert(gray_mat));
			try {Thread.sleep(1000);} catch (InterruptedException e) {}

			//de-noise
			opencv_photo.fastNlMeansDenoising(gray_mat, gray_mat);
			displayFrame.showImage(converter.convert(gray_mat));
			try {Thread.sleep(1000);} catch (InterruptedException e) {}

			//Smooth any jagged areas
			IplImage gray_ipl = converter.convert(converter.convert(gray_mat));
			opencv_imgproc.cvSmooth(gray_ipl, gray_ipl, opencv_imgproc.CV_MEDIAN, 3, 0, 0, 0);
			displayFrame.showImage(converter.convert(gray_ipl));
			try {Thread.sleep(1000);} catch (InterruptedException e) {}

			//Threshold Filter
			IplImage mask_ipl = opencv_core.cvCreateImage(opencv_core.cvGetSize(img_ipl), 8, 1);
			opencv_core.cvInRangeS(gray_ipl, cvScalar(6), cvScalar(255), mask_ipl);
			Mat final_mat = converter.convertToMat(converter.convert(mask_ipl));
			opencv_core.bitwise_and(gray_mat, gray_mat, final_mat, converter.convertToMat(converter.convert(mask_ipl)));
			gray_mat = final_mat;
			displayFrame.showImage(converter.convert(gray_mat));
			try {Thread.sleep(1000);} catch (InterruptedException e) {}

			//equalize the contrast
			opencv_imgproc.equalizeHist(gray_mat, gray_mat);
			displayFrame.showImage(converter.convert(gray_mat));
			try {Thread.sleep(1000);} catch (InterruptedException e) {}

			//Threshold Filter
			gray_ipl = converter.convert(converter.convert(gray_mat));
			mask_ipl = opencv_core.cvCreateImage(opencv_core.cvGetSize(img_ipl), 8, 1);
			opencv_core.cvInRangeS(gray_ipl, cvScalar(10), cvScalar(255), mask_ipl);
			gray_mat = converter.convertToMat(converter.convert(mask_ipl));
			displayFrame.showImage(converter.convert(gray_mat));
			try {Thread.sleep(1000);} catch (InterruptedException e) {}

			//Edge Detection
			Mat bw_mat = new Mat();
			opencv_imgproc.Canny(gray_mat, bw_mat, 30, 90, 3, true);
			displayFrame.showImage(converter.convert(bw_mat));
			try {Thread.sleep(2000);} catch (InterruptedException e) {}

			//Find the contours
			MatVector contours = new MatVector();
			Mat approx_mat = new Mat();
			opencv_imgproc.findContours(bw_mat.clone(), contours, opencv_imgproc.CV_RETR_EXTERNAL, opencv_imgproc.CV_CHAIN_APPROX_SIMPLE);

			for(long i = 0; i < contours.size(); i++){
				Mat tmp_mat = new Mat(contours.get(i));
				// Approximate contour with accuracy proportional to the contour perimeter
				opencv_imgproc.approxPolyDP(tmp_mat, approx_mat, opencv_imgproc.arcLength(tmp_mat, true)*0.02, true);

				// Skip small or non-convex objects 
				if (Math.abs(opencv_imgproc.contourArea(tmp_mat)) < 100 || !opencv_imgproc.isContourConvex(approx_mat)){
					continue;
				}

				// Number of vertices of polygonal curve
				if (approx_mat.arrayHeight() == 4){
					projectionContour = contours.get(i);
					findProjection = false;
				}
			}
		}

		/*
		 * Find the mask
		 */

		//create binary image of original size
		IplImage mask_ipl = opencv_core.cvCreateImage(opencv_core.cvGetSize(img_ipl), 8, 1);

		//apply thresholding
		opencv_core.cvInRangeS(img_ipl, threshMin, threshMax, mask_ipl);

		//smooth filter the mask
		opencv_imgproc.cvSmooth(mask_ipl, mask_ipl, opencv_imgproc.CV_MEDIAN, 7, 0, 0, 0);

		Mat mask_mat = converter.convertToMat(converter.convert(mask_ipl));
		if(saveMask){
			projectionMask = mask_mat;
		}
		Mat final_mat = converter.convertToMat(converter.convert(mask_ipl));
		opencv_core.bitwise_and(img_mat, img_mat, final_mat, mask_mat);

		if(projectionContour != null){
			Rect r = opencv_imgproc.boundingRect(projectionContour);
			opencv_core.rectangle(final_mat, r, new Scalar(0,255,255,0));
		}

		displayFrame.showImage(converter.convert(final_mat));
		draw();
	}

	public void draw() {
		Dimension size = projectionFrame.getCanvas().getSize();
		int height = size.height;
		int width = size.width;
		Mat base_mat = new Mat(height, width, 16);

		switch(drawState){
		case CHECKER:
			opencv_core.rectangle(base_mat, new Rect(0, 0, width, height), new Scalar(255,255,255,0), opencv_core.CV_FILLED, 8, 0);
			for(int y = 0; y < height; y+=50){
				for(int x = 0; x < width; x+=50){
					if((x/10+y/10) % 2 == 1)opencv_core.rectangle(base_mat, new Rect(x, y, 50, 50), new Scalar(0,0,0,0), opencv_core.CV_FILLED, 8, 0);
				}	
			}
			break;
		case RED_FILL:
			opencv_core.rectangle(base_mat, new Rect(0, 0, width, height), new Scalar(0,0,255,0), opencv_core.CV_FILLED, 8, 0);
			break;
		case GREEN_FILL:
			opencv_core.rectangle(base_mat, new Rect(0, 0, width, height), new Scalar(0,255,0,0), opencv_core.CV_FILLED, 8, 0);
			break;
		case BLUE_FILL:
			opencv_core.rectangle(base_mat, new Rect(0, 0, width, height), new Scalar(255,0,0,0), opencv_core.CV_FILLED, 8, 0);
			break;
		case WHITE_FILL:
			opencv_core.rectangle(base_mat, new Rect(0, 0, width, height), new Scalar(255,255,255,0), opencv_core.CV_FILLED, 8, 0);
			break;
		default:
			break;
		}

		Mat final_mat = new Mat(height, width, 16);
		
		if(projectionMask != null){
			/*float[] srcCorners = new float[8];
			float[] dstCorners= new float[8];
			
			CvMat affine_mat = new CvMat();
			opencv_imgproc.cvGetAffineTransform(srcCorners, dstCorners, affine_mat);
			
			Mat mask_mat = new Mat(height, width, 16);
			//projectionMask;
			//opencv_imgproc.warpAffine(arg0, arg1, arg2, arg3);
			opencv_core.bitwise_and(base_mat, base_mat, final_mat, mask_mat);*/
		}else{
			opencv_core.bitwise_and(base_mat, base_mat, final_mat);
		}
		projectionFrame.showImage(converter.convert(final_mat));
	}

	private enum DrawState{
		CHECKER(),
		RED_FILL(),
		GREEN_FILL(),
		BLUE_FILL(),
		WHITE_FILL();
	}
}
