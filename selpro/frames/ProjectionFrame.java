package selpro.frames;

import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import org.bytedeco.javacv.CanvasFrame;

public class ProjectionFrame extends CanvasFrame{
	private static final long serialVersionUID = 0L;
	boolean maximized = false;
	private Rectangle bounds;

	public ProjectionFrame(String title) {
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ProjectionFrame frame = this;
		setSize(640,480);
		setFocusTraversalKeysEnabled(false);

		canvas.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ESCAPE){
					if(maximized){
						frame.dispose();
						frame.setExtendedState(JFrame.NORMAL);
						frame.setUndecorated(false);
						frame.setBounds(bounds);
						frame.setVisible(true);
						canvas.requestFocus();
						maximized = false;
					}else{
						frame.dispose();
						frame.setUndecorated(true);
						bounds = frame.getBounds();
						frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
						frame.setVisible(true);
						setFocusTraversalKeysEnabled(false);
						canvas.requestFocus();
						maximized = true;
					}
				}
			}
		});
	}
}
