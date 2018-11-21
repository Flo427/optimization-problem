package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class DrawInstance extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int n = 10;
	private static final int l = 100;
	private static int frameWidth;
	private static int frameHeight;
	private Instance instance;

	public DrawInstance() {
		instance = new Instance(n, l);
		//instance = new Instance(n, l, 1, 50);
		instance.generateCoordinatesByPermutation();
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				RuleBasedNeighborhood neighborhood = new RuleBasedNeighborhood();
				instance = neighborhood.getBestNeighbor(neighborhood.getNeighbors(instance));
				repaint();
			}
		});
	}

	public void paintComponent(Graphics g) {
		instance.plot(g, frameWidth, frameHeight);
	}

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		JFrame frame = new JFrame("Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frameWidth = (int) screenSize.getWidth();
		frameHeight = (int) screenSize.getHeight();
		frame.setSize(frameWidth, frameHeight);
		frame.add(new DrawInstance());
		frame.setVisible(true);
	}
}