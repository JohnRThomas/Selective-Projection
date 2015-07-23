package selpro.frames;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import selpro.CameraReader;

public class ControlFrame extends CanvasFrame implements CameraReader.FrameListener{
	private static final long serialVersionUID = 0L;

	//Main variables
	private ProjectionFrame projectionFrame;
	private Mat texture = null;
	private Color threshMin = new Color(0, 0, 0, 0);//RGB-A
	private Color threshMax = new Color(255, 255, 255, 0);//RGB-A
	private DrawState drawState = DrawState.CHECKER;
	private List<MatOfPoint> projectionBounds = null;
	private Mat projectionMask = null;

	//Control booleans
	private boolean findProjection = false;
	private boolean saveMask = false;
	private boolean whiteBackground = false;
	private boolean saveInvertedMask = false;
	private boolean AA = false;


	public ControlFrame(String title, ProjectionFrame projectionFrame) {
		super(title);
		this.projectionFrame = projectionFrame;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640, 724);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(canvas);
		addButtons(mainPanel);
		this.setContentPane(mainPanel);
	}

	private void addButtons(JPanel panel) {
		JPanel[] panels = new JPanel[4];
		for (int i = 0; i < panels.length; i++) {
			panels[i] = new JPanel();
			panel.add(panels[i]);
		}

		JButton findProjection_btn = new JButton("Find Projection (Black)");
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

		JCheckBox background_cbx = new JCheckBox("Use White Background");
		background_cbx.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JCheckBox)e.getSource()).isSelected()){
					findProjection_btn.setText("Find Projection (White)");
					whiteBackground = true;
				}else{
					findProjection_btn.setText("Find Projection (Black)");
					whiteBackground = false;
				}
			}
		});


		panels[0].add(findProjection_btn);
		panels[0].add(background_cbx);

		//Colors row
		JButton red_btn = new JButton("RED");
		red_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.RED_FILL;
			}
		});

		panels[1].add(red_btn);

		JButton green_btn = new JButton("GREEN");
		green_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.GREEN_FILL;
			}
		});

		panels[1].add(green_btn);

		JButton blue_btn = new JButton("BLUE");
		blue_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.BLUE_FILL;
			}
		});

		panels[1].add(blue_btn);

		JButton white_btn = new JButton("WHITE");
		white_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.WHITE_FILL;
			}
		});

		panels[1].add(white_btn);

		JButton checker_btn = new JButton("CHECKER");
		checker_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.CHECKER;
			}
		});

		panels[1].add(checker_btn);

		JButton texture_btn = new JButton("TEXTURE");
		texture_btn.setEnabled(false);
		texture_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawState = DrawState.TEXTURE;
			}
		});

		panels[1].add(texture_btn);

		JFileChooser fc = new JFileChooser();
		JButton load_btn = new JButton("Load Texture");
		load_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(fc.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION){
					File file = fc.getSelectedFile();
					if(file.isFile()){
						String name = file.getName();
						if(name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg")){
							Mat img = Imgcodecs.imread(file.getAbsolutePath());
							texture = img;
							texture_btn.setText("TEXTURE (" + name + ")");
							texture_btn.setEnabled(true);
						}
					}
				}
			}
		});

		panels[1].add(load_btn);

		//Mask Row
		JButton applyMask_btn = new JButton("Apply Mask");
		applyMask_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveMask = true;
			}
		});

		panels[2].add(applyMask_btn);

		JCheckBox AA_cbx = new JCheckBox("Anti-Aliasing");
		AA_cbx.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AA = ((JCheckBox)e.getSource()).isSelected();
			}
		});

		panels[2].add(AA_cbx);

		JButton applyInvertedMask_btn = new JButton("Apply Inverted Mask");
		applyInvertedMask_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveInvertedMask  = true;
			}
		});

		panels[2].add(applyInvertedMask_btn);

		JButton clearMask_btn = new JButton("Clear Mask");
		clearMask_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized(projectionMask){
					projectionMask = null;
				}
			}
		});

		panels[2].add(clearMask_btn);

		panels[3].setLayout(new BoxLayout(panels[3], BoxLayout.Y_AXIS));

		addSliders(panels[3]);
	}

	public void addSliders(JPanel panel){
		JLabel red_lbl = new JLabel("Red: 0.0-255.0");
		RangeSlider red_sld = new RangeSlider(0, 255, 0, 255);
		red_sld.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				threshMin = new Color(threshMin.getBlue(), threshMin.getGreen(), red_sld.getValue());
				threshMax = new Color(threshMax.getBlue(), threshMax.getGreen(), red_sld.getUpperValue());
				red_lbl.setText("Red: " + threshMin.getRed() + "-" + threshMax.getRed());

			}
		});

		JLabel green_lbl = new JLabel("Green: 0.0-255.0");
		RangeSlider green_sld = new RangeSlider(0, 255, 0, 255);
		green_sld.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				threshMin = new Color(threshMin.getBlue(), green_sld.getValue(), threshMin.getRed());
				threshMax = new Color(threshMax.getBlue(), green_sld.getUpperValue(), threshMax.getRed());
				green_lbl.setText("Green: " + threshMin.getGreen() + "-" + threshMax.getGreen());
			}
		});

		JLabel blue_lbl = new JLabel("Blue: 0.0-255.0");
		RangeSlider blue_sld = new RangeSlider(0, 255, 0, 255);
		blue_sld.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				threshMin = new Color(blue_sld.getValue(), threshMin.getGreen(), threshMin.getRed());
				threshMax = new Color(blue_sld.getUpperValue(), threshMax.getGreen(), threshMax.getRed());
				blue_lbl.setText("Blue: " + threshMin.getBlue() + "-" + threshMax.getBlue());
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
	public void processFrame(Mat img) {

		if(findProjection){
			/*
			 * Projection Mapping
			 */
			Mat gray_img = new Mat();

			//Show the original frame
			this.showImage(img);

			//Change to grayscale

			Imgproc.cvtColor(img, gray_img, Imgproc.COLOR_BGR2GRAY);
			this.showImage(gray_img);

			//de-noise
			Photo.fastNlMeansDenoising(gray_img, gray_img);
			this.showImage(gray_img);

			//Smooth any jagged areas
			Imgproc.medianBlur(gray_img, gray_img, 3);
			this.showImage(gray_img);

			//Threshold Filter
			Mat mask = new Mat();
			if(whiteBackground){
				Core.inRange(gray_img, new Scalar(160), new Scalar(255), mask);
			}else{
				Core.inRange(gray_img, new Scalar(6), new Scalar(255), mask);
			}

			Core.bitwise_and(gray_img, gray_img, gray_img, mask);
			this.showImage(gray_img);

			//increase the contrast
			if(!whiteBackground){
				//Imgproc.equalizeHist(gray_img, gray_img);
				this.showImage(gray_img);
			}

			//Threshold Filter
			if(!whiteBackground){
				Core.inRange(gray_img, new Scalar(10), new Scalar(255), mask);
				this.showImage(gray_img);
			}

			//Edge Detection
			Mat bw_mat = new Mat();
			Imgproc.Canny(gray_img, bw_mat, 30, 90, 3, true);
			this.showImage(bw_mat);

			//Find the contours
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			MatOfPoint2f approx_mat = new MatOfPoint2f();
			Imgproc.findContours(bw_mat.clone(), contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

			for(int i = 0; i < contours.size(); i++){
				MatOfPoint2f tmp_mat = new MatOfPoint2f(contours.get(i).toArray());

				// Approximate contour with accuracy proportional to the contour perimeter
				Imgproc.approxPolyDP(tmp_mat, approx_mat, Imgproc.arcLength(tmp_mat, true)*0.02, true);

				// Skip small or non-convex objects 
				if (Math.abs(Imgproc.contourArea(tmp_mat)) < 100 || !Imgproc.isContourConvex(new MatOfPoint(approx_mat.toArray()))){
					continue;
				}

				// Number of vertices of polygonal curve
				if (approx_mat.height() == 4){

					MatOfPoint poly = new MatOfPoint(approx_mat.toArray());
					List<MatOfPoint> polys = new ArrayList<MatOfPoint>();
					polys.add(poly);

					projectionBounds = polys;
					findProjection = false;
				}
			}
		}

		/*
		 * Find the mask
		 */

		//apply thresholding
		Mat mask = new Mat();
		Core.inRange(img, new Scalar(threshMin.getBlue(), threshMin.getGreen(), threshMin.getRed(), 0), new Scalar(threshMax.getBlue(), threshMax.getGreen(), threshMax.getRed(), 0), mask);

		//smooth the mask
		Imgproc.medianBlur(mask, mask, 7);

		if(projectionBounds != null && (saveMask || saveInvertedMask)){
			Dimension size = projectionFrame.canvas.getSize();
			int w = size.width;
			int h = size.height;

			MatOfPoint2f src = new MatOfPoint2f(sortPoints(projectionBounds.get(0).toArray()));
			MatOfPoint2f dst = new MatOfPoint2f(new Point[]{new Point(0,0),new Point(w,0),new Point(0,h),new Point(w,h)});
			Mat lambda = Imgproc.getPerspectiveTransform(src, dst);

			Mat tmp_mask = new Mat();
			Imgproc.warpPerspective(mask, tmp_mask, lambda, new Size(w, h));
			projectionMask = tmp_mask;

			//Invert the mask if it was requested
			if(saveInvertedMask) Core.bitwise_not(projectionMask, projectionMask);

			saveMask = saveInvertedMask = false;
		}

		Mat final_mat = new Mat();
		Core.bitwise_and(img, img, final_mat, mask);

		if(projectionBounds != null){
			Imgproc.drawContours(final_mat, projectionBounds, 0, new Scalar(0,255,255,0));
		}

		this.showImage(final_mat);
		draw();
	}

	private Point[] sortPoints(Point[] in) {
		Point[] pts = new Point[4];

		double centerx = (in[0].x + in[1].x + in[2].x + in[3].x) / 4.0;
		double centery = (in[0].y + in[1].y + in[2].y + in[3].y) / 4.0;

		for (int i = 0; i < in.length; i++) {
			if(in[i].x < centerx && in[i].y < centery)pts[0] = in[i];
			else if(in[i].x > centerx && in[i].y < centery)pts[1] = in[i];
			else if(in[i].x < centerx && in[i].y > centery)pts[2] = in[i];
			else if(in[i].x > centerx && in[i].y > centery)pts[3] = in[i];
		}

		return pts;
	}

	public void draw() {
		Dimension size = projectionFrame.canvas.getSize();
		int height = size.height;
		int width = size.width;
		Mat base_mat = Mat.zeros(height, width, 16);
		Imgproc.rectangle(base_mat, new Point(0, 0), new Point(width, height), new Scalar(0,0,0,0), Core.FILLED, 8, 0);

		switch(drawState){
		case TEXTURE:
			if(texture != null){
				base_mat = texture;
				Imgproc.resize(base_mat, base_mat, new Size(width, height));
			}
			break;
		case CHECKER:
			//Color in the background to be all white
			Imgproc.rectangle(base_mat, new Point(0, 0), new Point(width, height), new Scalar(255,255,255,0), Core.FILLED, 8, 0);

			//Draw 50x50px black squares to form the checkers.
			for(int y = 0; y < height; y+=50){
				for(int x = 0; x < width; x+=50){
					if((x/10+y/10) % 2 == 1)Imgproc.rectangle(base_mat, new Point(x, y), new Point(x+50, y+50), new Scalar(0,0,0,0), Core.FILLED, 8, 0);
				}	
			}
			break;
		case RED_FILL:
			Imgproc.rectangle(base_mat, new Point(0, 0), new Point(width, height), new Scalar(0,0,255,0), Core.FILLED, 8, 0);
			break;
		case GREEN_FILL:
			Imgproc.rectangle(base_mat, new Point(0, 0), new Point(width, height), new Scalar(0,255,0,0), Core.FILLED, 8, 0);
			break;
		case BLUE_FILL:
			Imgproc.rectangle(base_mat, new Point(0, 0), new Point(width, height), new Scalar(255,0,0,0), Core.FILLED, 8, 0);
			break;
		case WHITE_FILL:
			Imgproc.rectangle(base_mat, new Point(0, 0), new Point(width, height), new Scalar(255,255,255,0), Core.FILLED, 8, 0);
			break;
		default:
			break;
		}

		Mat final_mat = Mat.zeros(height, width, 16);

		if(projectionMask != null){
			synchronized(projectionMask){
				//smooth the mask
				if(AA)Imgproc.medianBlur(projectionMask, projectionMask, 13);

				//Apply the mask to the projection area
				Core.bitwise_and(base_mat, base_mat, final_mat, projectionMask);
			}
		}else{
			//And the image with itself and don't include the mask.
			Core.bitwise_and(base_mat, base_mat, final_mat);
		}

		//Show the image on the projector
		projectionFrame.showImage(final_mat);
	}

	private enum DrawState{
		CHECKER(),
		RED_FILL(),
		GREEN_FILL(),
		BLUE_FILL(),
		WHITE_FILL(),
		TEXTURE();
	}
}
