package selpro.frames;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ProjectionFrame extends JFrame{
	private static final long serialVersionUID = 0L;
	boolean listenForEscape = false;
	private Rectangle bounds;
	private Drawable drawer;
	
	public ProjectionFrame(String title) {
		super(title);
		ProjectionFrame frame = this;
		setSize(640,480);
        setFocusTraversalKeysEnabled(false);

		JPanel surface = new JPanel(){
			private static final long serialVersionUID = 0L;

			@Override
			public void paintComponent(Graphics g) {
			    super.paintComponent(g);
			    if(drawer != null){
			    	drawer.draw(g, this.getWidth(), this.getHeight());
			    }
			}
		};
		
		add(surface);
        
		JButton btn_maximize = new JButton("Fullscreen");
		btn_maximize.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				surface.remove(btn_maximize);
				listenForEscape = true;
				frame.dispose();
				frame.setUndecorated(true);
				bounds = frame.getBounds();
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				frame.setVisible(true);
		        setFocusTraversalKeysEnabled(false);
			}
			
		});
		btn_maximize.setFocusable(false);
		surface.add(btn_maximize);
		
		addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ESCAPE && listenForEscape){
					listenForEscape = false; 
					surface.add(btn_maximize);
					frame.dispose();
					frame.setExtendedState(JFrame.NORMAL);
					frame.setUndecorated(false);
					frame.setBounds(bounds);
					frame.setVisible(true);
				}
			}
		});
	}
	
	public void setDrawer(Drawable drawer){ 
		this.drawer = drawer;
	}
}
