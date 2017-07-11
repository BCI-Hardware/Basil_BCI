	package cz.zcu.kiv.eeg.gtn.gui;
	import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import cz.zcu.kiv.eeg.gtn.data.processing.math.FirFilter;

	/**
	 * Class providing the GUI dialog for the creation of the FIR filter.
	 * @author Anezka Jachymova
	 * @version 1.01
	 */
	public class FirFilterDialog extends JDialog {

		private static final long serialVersionUID = 1L;
		/**
		 * Instance of MainFrame, owner of the dialog.
		 */
		private MainFrame mainFrame;
		
		/*
		 * These attributes are here only because of 
		 * referencing from inner classes.
		 * They have no other use here.
		 */
		private JPanel genPN;
		private JPanel helpPN;
		private JTextArea impulsTA;
		private JTextField upperTF;
		private JTextField lowerTF;
		private JTextField nSampleTF;
		private JTextField sampleRateTF;
		private JTextField sumTF;
		private JButton okBT;
		
		/**
		 * Creates the dialog and sets its owner.
		 * @param frame Owner of the JDialog.
		 */
		public FirFilterDialog(MainFrame frame){
			super(frame);
			this.mainFrame = frame;
			this.setModal(true);
			this.setTitle("FIR filter");	
			this.getContentPane().add(createMainPanel());
			this.pack();
			this.setLocationRelativeTo(null);
			this.setVisible(true);
			
		}
		
		
		
		/**
		 * Creates the JPanel of the Generate tab.
		 * @return JPanel with all needed components.
		 */
		public JPanel createGeneratePanel(){
			genPN = new JPanel(new BorderLayout());
			GridBagLayout gbl = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			JPanel paramPN = new JPanel(gbl);
			
			lowerTF = new JTextField(12);
			lowerTF.setText("0.1");
			lowerTF.setToolTipText("Interval: 0 - Higher frequency");
			upperTF = new JTextField(12);
			upperTF.setText("8");
			upperTF.setToolTipText("Interval: 0 - Sample rate/2");
			sampleRateTF = new JTextField(12);
			sampleRateTF.setText("1024");
			sampleRateTF.setToolTipText("Interval: > 0");
			nSampleTF = new JTextField(12);
			nSampleTF.setText("30");
			nSampleTF.setToolTipText("Interval: 2 - 100 (100 is maximal recomended value. Greater number of samples can alter the function of the filter!)");
			sumTF = new JTextField(12);
			sumTF.setText("0");
			sumTF.setToolTipText("Interval: > 0");
			
			c.insets = new Insets(5,12,5,12);
			c.fill = GridBagConstraints.HORIZONTAL;
			paramPN.add(new JLabel("Lower frequency: *"),c);
			c.gridx = 1;
			paramPN.add(lowerTF,c);
			c.gridx = 0;
			c.gridy = 1;
			paramPN.add(new JLabel("Higher frequency: *"),c);
			c.gridx = 1;
			paramPN.add(upperTF,c);
			c.gridx = 0;
			c.gridy = 2;
			paramPN.add(new JLabel("Sample rate: *"),c);
			c.gridx = 1;
			paramPN.add(sampleRateTF,c);
			c.gridx = 0;
			c.gridy = 3;
			paramPN.add(new JLabel("Number of samples:"),c);
			c.gridx = 1;
			paramPN.add(nSampleTF,c);
			c.gridx = 0;
			c.gridy = 4;
			paramPN.add(new JLabel("Ripple:"),c);
			c.gridx = 1;
			paramPN.add(sumTF,c);
			genPN.add(paramPN, BorderLayout.CENTER);
			
			impulsTA = new JTextArea(12,20);
			impulsTA.setText("Impulse response\r\n");
			impulsTA.setEditable(false);
			impulsTA.setFont(new Font("Calibri",12,12));
			JScrollPane jsc = new JScrollPane(impulsTA);
			genPN.add(jsc, BorderLayout.EAST);
			
			JButton genBT = new JButton("Generate");
			genBT.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					double ripple = 0, lower, upper;
					int sampleRate, nSample = 30;
					try {
						lower = Double.parseDouble(lowerTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(FirFilterDialog.this,"Lower threshold must be a real number greater than 0.","Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						upper = Double.parseDouble(upperTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(FirFilterDialog.this,"Higher threshold must be a real number greater than 0\r\n"
							+ "and lesser than half of sample rate!","Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						sampleRate = Integer.parseInt(sampleRateTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(FirFilterDialog.this,"Sample rate must be an integer number.","Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						String nS = nSampleTF.getText();
						if (!nS.equals("")) {
							nSample = Integer.parseInt(nS);
						}
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(FirFilterDialog.this,"Number of samples must be an integer number.","Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						String s = sumTF.getText();
						if (!s.equals("")) {
							ripple = Double.parseDouble(s);
						}
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(FirFilterDialog.this,"Ripple value must be a real number.","Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					if(checkValues(lower, upper, sampleRate, nSample, ripple)) {
						impulsTA.setText(null);
						String temp = Arrays.toString(FirFilter.calculateImpulseResponse(lower, upper, sampleRate, nSample, ripple));
						String[] pole = temp.substring(1, temp.length() - 1).split(",");
						impulsTA.append("Impulse response\r\nfor values: " + lower + " " + upper + " " + sampleRate + " " + nSample + " " + ripple);
						for(String cislo : pole)
							impulsTA.append("\r\n"+cislo);
					}
					else {
						JOptionPane.showMessageDialog(FirFilterDialog.this,"Some of the values are outside the interval!\r\n"
								+ "You can find info about intervals in the tooltips of parametres."
								,"Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			});
			
			JPanel bottomPN = new JPanel();
			okBT = new JButton("OK");
			okBT.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					double ripple = 0, lower, upper;
					int sampleRate, nSample = 30;
					try {
						lower = Double.parseDouble(lowerTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(FirFilterDialog.this,"Lower threshold must be a real number greater than 0.","Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						upper = Double.parseDouble(upperTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(FirFilterDialog.this,"Higher threshold must be a real number greater than 0\r\n"
							+ "and lesser than half of sample rate!","Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						sampleRate = Integer.parseInt(sampleRateTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(FirFilterDialog.this,"Sample rate must be an integer number.","Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						String nS = nSampleTF.getText();
						if (!nS.equals("")) {
							nSample = Integer.parseInt(nS);
						}
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(FirFilterDialog.this,"Number of samples must be an integer number.","Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						String s = sumTF.getText();
						if (!s.equals("")) {
							ripple = Double.parseDouble(s);
						}
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(FirFilterDialog.this,"Ripple value must be a real number.","Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					if(checkValues(lower, upper, sampleRate, nSample, ripple))
						MainFrame.dataFilter = new FirFilter(lower, upper, sampleRate, nSample, ripple);
					else {
						JOptionPane.showMessageDialog(FirFilterDialog.this,"Some of the values are outside the interval!\r\n"
								+ "You can find info about intervals in the tooltips of parametres."
								,"Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					FirFilterDialog.this.dispose();
				}
					
			});
			bottomPN.add(genBT);
			bottomPN.add(okBT);
			genPN.add(bottomPN, BorderLayout.SOUTH);
			return genPN;
		}
		
		/**
		 * Creates JPanel of the tab Help.
		 * @return JPanel - the panel with Help
		 */
		public JPanel createHelpPanel(){
			JPanel helpPN = new JPanel();
			JTextArea help = new JTextArea(20,50);
			JScrollPane jsc = new JScrollPane(help);
			help.setLineWrap(true);
			help.setWrapStyleWord(true);
			help.setEditable(false);
			help.setFont(new Font("Calibri",12,12));
			help.setText("Finite Impulse Response Filter is used to remove unwanted components from EEG signal."
					+ " This filter is defined by its Impuse Response. This value determines its filtering atributes."
					+ " Response is Finite because it is of finite duration, because it settles to zero in finite time."
					+ " FIR filter is more dificult to implement and requries more computation memory and time than IIR."
					+ " However it is more versatile with its parameters.\r\n"
					+ "This filter can be modified with 5 parameters. Only 3 of them (signed with *) are required for easy use.\r\n"
					+ "Lower frequency[Hz] - The lower threshold of chosen band. Required parameter.\r\n"
					+ "Higher frequency[Hz] - The higher threshold of chosen band. Required parameter.\r\n"
					+ "Sample rate[Hz] - Determines how many samples are taken each second. Required parameter.\r\n"
					+ "Numbers of samples - Determines the function of filter. Higher number of impulse samples can improve"
					+ " filter function. Too many samples delays the filter too much and damages the function. Recomended values are 3 - 100.\r\n"
					+ "Ripple[dB] - Filter can conpensate for signal ripple. Thresholds for compensation are 21dB and 50dB");
			helpPN.add(jsc);
			return helpPN;
		}
		
		/**
		 * Creates the main panel of the dialog window with two tabs: Generovani and Napoveda.
		 * @return JTabbedPane - the main pane of the dialog window
		 */
		public JTabbedPane createMainPanel(){
			JTabbedPane tabbedPN = new JTabbedPane();
			this.genPN = createGeneratePanel();
			this.helpPN = createHelpPanel();
			tabbedPN.addTab("Generate", genPN);
			tabbedPN.addTab("Help", helpPN);
			
			return tabbedPN;
		}
		
		private boolean checkValues(double lower, double upper, int sampleRate, int nSample, double ripple) {
			if(lower <= 0 || lower >= upper) return false;
			if(upper <= 0 || upper*2 >= sampleRate) return false;
			if(sampleRate <= 0) return false;
			if(nSample <= 2) return false;
            return !(ripple < 0);
        }
	}


