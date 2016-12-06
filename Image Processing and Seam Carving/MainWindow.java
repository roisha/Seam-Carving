/*
 * This is the main application window.
 */

package img;

import java.awt.BorderLayout;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;
import javax.swing.JTextArea;


@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	private BufferedImage img;
	private String  imgTitle;
	private Vector<JButton> operationButtons = new Vector<JButton>();

	private JPanel contentPane;
	private JTextField txtFilename;
	private JFormattedTextField txtRetargetHeight;
	private JFormattedTextField txtRetargetWidth;
	private JFormattedTextField txtSamplingFactor;
	private JTextArea txtStatus;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		JFrame wnd = new MainWindow();
		wnd.setVisible(true);
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setTitle("ex1: Image Processing Application");
		//The following line makes sure that all application threads are terminated when this window is closed.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));


		// Upper panel
		JPanel panelFileSelection = new JPanel();
		panelFileSelection.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(panelFileSelection, BorderLayout.NORTH);
		panelFileSelection.setLayout(new BoxLayout(panelFileSelection, BoxLayout.X_AXIS));

		txtFilename = new JTextField();
		txtFilename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				open(txtFilename.getText());
			}
		});
		panelFileSelection.add(txtFilename);
		txtFilename.setColumns(40);

		JButton btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				int ret = fileChooser.showOpenDialog(MainWindow.this);
				if (ret == JFileChooser.APPROVE_OPTION) {
					String filename = fileChooser.getSelectedFile().getPath();
					txtFilename.setText(filename);
					open(filename);
				}
			}
		});
		panelFileSelection.add(btnBrowse);

		JButton btnReload = new JButton("Reset to original");
		btnReload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				open(txtFilename.getText());
			}
		});
		panelFileSelection.add(btnReload);

		JPanel panelOperations = new JPanel();
		contentPane.add(panelOperations, BorderLayout.CENTER);
		panelOperations.setLayout(new GridLayout(0, 1, 0, 0));


		//Desired Size panel
		JPanel panelDesiredSize = new JPanel();		
		panelDesiredSize.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelOperations.add(panelDesiredSize);
		panelDesiredSize.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panelDesiredSize.setBackground(Color.green);

		JLabel lbdesiredSize = new JLabel("Desired Size     ");
		lbdesiredSize.setFont(new Font("Serif", Font.BOLD, 14));

		panelDesiredSize.add(lbdesiredSize);

		JLabel lblRetargetWidth = new JLabel("Width:");
		panelDesiredSize.add(lblRetargetWidth);

		txtRetargetWidth = new JFormattedTextField(new Integer(640));
		txtRetargetWidth.setColumns(5);
		panelDesiredSize.add(txtRetargetWidth);



		JLabel lblRetargetHeight = new JLabel("Height:");
		panelDesiredSize.add(lblRetargetHeight);		

		txtRetargetHeight = new JFormattedTextField(new Integer(480));
		txtRetargetHeight.setColumns(5);
		panelDesiredSize.add(txtRetargetHeight);


		// Retarget 
		JPanel panelRetarget = new JPanel();
		panelRetarget.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelOperations.add(panelRetarget);
		panelRetarget.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btnRetarget = new JButton("Retarget");

		btnRetarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int width = (Integer)txtRetargetWidth.getValue();
				int height = (Integer)txtRetargetHeight.getValue();
				BufferedImage bckupimg=MainWindow.this.img;
				try {
					BufferedImage img = ImageProc.retargetSize(MainWindow.this.img, width, height);
					MainWindow.this.img = img;
					present("Retarget(w=[" + width + "]) (h=[" + height + "])");
				} catch (Exception ex) {
					String errormsg=ex.getMessage();
					if (errormsg==null)
						errormsg="Error in Retargeting, check the parameters!";										
					JOptionPane.showMessageDialog(MainWindow.this,errormsg, "Error", JOptionPane.ERROR_MESSAGE);
					MainWindow.this.img=bckupimg;
				}
			}
		});
		btnRetarget.setEnabled(false);
		panelRetarget.add(btnRetarget);
		operationButtons.add(btnRetarget);


		JButton btnShowSeams = new JButton("Show seams");
		btnShowSeams.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int width = (Integer)txtRetargetWidth.getValue();
				int height = (Integer)txtRetargetHeight.getValue();
				BufferedImage bckupimg=MainWindow.this.img;//save image without colored seams 
				try {
					BufferedImage img = ImageProc.showSeams(MainWindow.this.img, width, height);					
					MainWindow.this.img = img;
					present("ShowSeams(w=[" + width + "])(h=[" + height + "])");
					MainWindow.this.img=bckupimg;//return to image without colored seams 
				} catch (Exception ex) {
					String errormsg=ex.getMessage();
					if (errormsg==null)
						errormsg="Error Showing Seams, check the parameters!";	
					JOptionPane.showMessageDialog(MainWindow.this, errormsg, "Error", JOptionPane.ERROR_MESSAGE);
					MainWindow.this.img=bckupimg;
				}
			}
		});
		btnShowSeams.setEnabled(false);
		panelRetarget.add(btnShowSeams);
		operationButtons.add(btnShowSeams);


		// Derivatives and gradient magnitude
		JPanel panelMisc = new JPanel();
		panelMisc.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelOperations.add(panelMisc);
		panelMisc.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));		

		//Horizontal Derivative
		JButton btHorzDeriv = new JButton("HorzDeriv");
		btHorzDeriv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BufferedImage bckupimg=MainWindow.this.img;
				try {
					img = ImageProc.horizontalDerivative(img);
					present("Horizontal Derivative");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(MainWindow.this, "Error in horizontal derivative, check the parameters!", "Error", JOptionPane.ERROR_MESSAGE);
					MainWindow.this.img=bckupimg;
				}

			}
		});
		btHorzDeriv.setEnabled(false);
		panelMisc.add(btHorzDeriv);
		operationButtons.add(btHorzDeriv);


		//Vertical Derivative
		JButton btVertDeriv = new JButton("VertDeriv");
		btVertDeriv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BufferedImage bckupimg=MainWindow.this.img;
				try {
					img = ImageProc.verticalDerivative(img);
					present("Vertical Derivative");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(MainWindow.this, "Error in vertical derivative, check the parameters!", "Error", JOptionPane.ERROR_MESSAGE);
					MainWindow.this.img=bckupimg;
				}

			}
		});
		btVertDeriv.setEnabled(false);
		panelMisc.add(btVertDeriv);
		operationButtons.add(btVertDeriv);

		//Gradient magnitude
		JButton btnGradientMag = new JButton("Gradient magnitude");
		btnGradientMag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BufferedImage bckupimg=MainWindow.this.img;
				try {
					img = ImageProc.gradientMagnitude(img);
					present("GradientMag");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(MainWindow.this, "Error in gradient magnitude, check the parameters!", "Error", JOptionPane.ERROR_MESSAGE);
					MainWindow.this.img=bckupimg;
				}

			}
		});
		btnGradientMag.setEnabled(false);
		panelMisc.add(btnGradientMag);
		operationButtons.add(btnGradientMag);




		// Gray scale and Scale down
		JPanel panelGrayScaleDown = new JPanel();
		panelGrayScaleDown.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelOperations.add(panelGrayScaleDown);
		panelGrayScaleDown.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));



		//Gray scale
		JButton btnGrayScale = new JButton("Gray scale");
		btnGrayScale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BufferedImage bckupimg=MainWindow.this.img;
				try {
					img = ImageProc.grayScale(img);
					present("GrayScale");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(MainWindow.this, "Error in gray scale, check the parameters!", "Error", JOptionPane.ERROR_MESSAGE);
					MainWindow.this.img=bckupimg;
				}
			}
		});
		btnGrayScale.setEnabled(false);
		panelGrayScaleDown.add(btnGrayScale);
		operationButtons.add(btnGrayScale);

		//Scale Down
		JButton btnScaleDown = new JButton("Scale down");
		btnScaleDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int factor = (Integer)txtSamplingFactor.getValue();
				BufferedImage bckupimg=MainWindow.this.img;
				try {
					img = ImageProc.scaleDown(img, factor);
					present("ScaleDown(factor=" + factor + ")");
					txtRetargetHeight.setValue(img.getHeight());
					txtRetargetWidth.setValue(img.getWidth());
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(MainWindow.this, "Error in scale down, check the parameters!", "Error", JOptionPane.ERROR_MESSAGE);
					MainWindow.this.img=bckupimg;
				}

			}
		});
		btnScaleDown.setEnabled(false);
		panelGrayScaleDown.add(btnScaleDown);
		operationButtons.add(btnScaleDown);

		JLabel lblSamplingFactor = new JLabel("Sampling factor:");
		panelGrayScaleDown.add(lblSamplingFactor);

		txtSamplingFactor = new JFormattedTextField(new Integer(4));
		txtSamplingFactor.setColumns(5);
		panelGrayScaleDown.add(txtSamplingFactor);



		// Lower panel
		JPanel panelStatus = new JPanel();
		panelStatus.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelOperations.add(panelStatus);
		panelStatus.setLayout(new BorderLayout(0, 0));

		JLabel lblStatus = new JLabel(" Current image:   ");
		panelStatus.add(lblStatus, BorderLayout.WEST);

		txtStatus = new JTextArea(); 
		txtStatus.setEditable(false);
		txtStatus.setLineWrap(true);
		JScrollPane scrollStatus = new JScrollPane(txtStatus);
		panelStatus.add(scrollStatus);

		//organize all panels in the main window
		pack();
	}

	void open(String filename) {
		try {
			BufferedImage img = ImageIO.read(new File(filename));
			if (img == null)
				throw new NullPointerException();
			this.img = img;
			imgTitle = new String();
			present("Opened " + new File(filename).getName());
			txtRetargetHeight.setValue(img.getHeight());
			txtRetargetWidth.setValue(img.getWidth());
			for (JButton btn : operationButtons)
				btn.setEnabled(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Can't open file!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	void present(String txt) {
		imgTitle+=(txt + "; ");
		txtStatus.setText(imgTitle.toString());
		new ImageWindow(img, imgTitle.toString()).setVisible(true);
	}

}
