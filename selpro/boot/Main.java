package selpro.boot;

import java.awt.Color;
import java.awt.Graphics;

import selpro.frames.DisplayFrame;
import selpro.frames.Drawable;
import selpro.frames.ProjectionFrame;

public class Main {
	public static Drawable drawer = new Drawable(){
		@Override
		public void draw(Graphics g, int width, int height) {
			g.setColor(Color.RED);
			g.fillRect(0, 0, width, height);
		}
		
	};
	
	public static void main(String args[]){
		ProjectionFrame projectionFrame = new ProjectionFrame("Projector Output");
		projectionFrame.setVisible(true);
		projectionFrame.setDrawer(drawer);
		
		DisplayFrame displayFrame = new DisplayFrame("Camera");
		displayFrame.setLocation(projectionFrame.getWidth()+projectionFrame.getX(), projectionFrame.getY());
		displayFrame.setVisible(true);
		new Thread(displayFrame).start();
	}
}
