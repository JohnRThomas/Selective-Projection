package selpro.frames;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.bytedeco.javacv.CanvasFrame;

public class DisplayFrame extends CanvasFrame{
	private static final long serialVersionUID = 0L;
		public DisplayFrame(String title) {
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
		setSize(640, 480);
	}	
}
