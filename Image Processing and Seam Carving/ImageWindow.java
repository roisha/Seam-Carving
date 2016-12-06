/*
 * This class displays an image in a new window and allows to save it as a PNG file.
 */

package edu.cg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("serial")
public class ImageWindow extends JFrame {

	private BufferedImage img;
	
	private JPanel contentPane;

	/**
	 * Create the window.
	 */
	public ImageWindow(BufferedImage img, String title) {
		this.img = img;

		setTitle(title);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JButton btnSaveAs = new JButton("Save as...");
		btnSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int ret = fileChooser.showSaveDialog(ImageWindow.this);
				if (ret == JFileChooser.APPROVE_OPTION)
					save(fileChooser.getSelectedFile());
			}
		});
		contentPane.add(btnSaveAs, BorderLayout.NORTH);
		
		JPanel panelImage = new ImagePanel();
		contentPane.add(panelImage, BorderLayout.CENTER);
		
		pack();
	}
	
	private class ImagePanel extends JPanel {
		public ImagePanel() {
			setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			g.drawImage(img, 0, 0, null);
		}
	}
	
	private void save(File file) {
		try {
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Can't save file!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
