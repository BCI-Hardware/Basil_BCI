package icp.gui.signals;

import java.awt.event.*;

import javax.swing.*;

/**
 * T��da vytv��ej�c� popup-menu, 
 * kter� umo��uje funkce segmentace sign�l� a ozna�en� artefakt�.
 * 
 * @author Petr - Soukal
 */
public class OptionMenu extends JPopupMenu
{
	private SignalsWindowProvider signalsWindowProvider;
	private JMenuItem selectEpoch;
	private JMenuItem setPlaybackIndicator;
	private JMenuItem unselectEpoch;
	private JMenuItem unselectAllEpochs;
	private long frame;
	
	/**
	 * Vytv��� objekt dan� t��dy a tla��tka menu.
	 * 
	 * @param signalsWindowProvider - objekt t��dy SignalsWindowProvider pro komunikaci
	 * s ostatn�mi t��dami prezenta�n� vrstvy.
	 */
	public OptionMenu(SignalsWindowProvider signalsWindowProvider)
	{
		this.signalsWindowProvider = signalsWindowProvider;
		selectEpoch = new JMenuItem("Select Epoch");
		selectEpoch.addActionListener(new FunctionSelectEpoch());
		setPlaybackIndicator = new JMenuItem("Set Playback Indicator");
		setPlaybackIndicator.addActionListener(new FunctionSetPlaybackIndicator());
		unselectEpoch = new JMenuItem("Unselect Epoch");
		unselectEpoch.addActionListener(new FunctionUnselectEpoch());
		unselectAllEpochs = new JMenuItem("Unselect All Epochs");
		unselectAllEpochs.addActionListener(new FunctionUnselectAllEpochs());
		
		this.add(setPlaybackIndicator);	
		this.add(selectEpoch);
		this.addSeparator();
		this.add(unselectEpoch);
		this.addSeparator();
		this.add(unselectAllEpochs);
	}
	
	/**
	 * Nastavuje zobrazen� popup-menu a jeho um�st�n�.
	 * 
	 * @param visualComponent - komponenta, ke kter� se menu v�e.
	 * @param xAxis - x-ov� sou�adnice zobrazen� menu.
	 * @param yAxis - y-ov� sou�adnice zobrazen� menu.
	 * @param frame - m�sto v souboru, p�epo��tan� ze sou�adnic kliku.
	 */
	public void setVisibleMenu(JComponent visualComponent, int xAxis, int yAxis, long frame)
	{
		this.frame = frame;
		this.show(visualComponent, xAxis, yAxis);
	}
	
	/**
	 * Nastavuje povolen�/zak�z�n� jednotliv�ch tla��tek.
	 * 
	 * @param enabledSelEpoch - povolen�/zak�z�n� ozna�en� epochy.
	 * @param enabledUnselEpoch - povolen�/zak�z�n� odzna�en� epochy.
	 * @param enabledUnselArtefact - povolen�/zak�z�n� odzna�en� artefaktu.
	 * @param enabledUnselAllEpochs - povolen�/zak�z�n� odzna�en� v�ech epoch.
	 * @param enabledUnselAllArtefacts - povolen�/zak�z�n� odzna�en� v�ech artefakt�.
	 * @param enabledUnselAll - povolen�/zak�z�n� odzna�en� v�eho.
	 */
	public void setEnabledItems(boolean enabledSelEpoch, boolean enabledUnselEpoch, boolean enabledUnselArtefact,
			boolean enabledUnselAllEpochs, boolean enabledUnselAllArtefacts, boolean enabledUnselAll)
	{
		selectEpoch.setEnabled(enabledSelEpoch);
		unselectEpoch.setEnabled(enabledUnselEpoch);
		unselectAllEpochs.setEnabled(enabledUnselAllEpochs);
	}
	
	/**
	 * Obsluhuje akci p�i stisknut� tla��tka ozna�en� epochy.
	 */
	private class FunctionSelectEpoch implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	signalsWindowProvider.selectEpoch(frame);
        }
    }
	
	/**
	 * Obsluhuje akci p�i stisknut� tla��tka nastaven� ukazatele p�ehr�v�n�.
	 */
	private class FunctionSetPlaybackIndicator implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	signalsWindowProvider.getDrawingComponent().setPlaybackIndicatorPosition(frame);
        }
    }
	
	/**
	 * Obsluhuje akci p�i stisknut� tla��tka odzna�en� epochy.
	 */
	private class FunctionUnselectEpoch implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	signalsWindowProvider.unselectEpoch(frame);
        }
    }
	
	/**
	 * Obsluhuje akci p�i stisknut� tla��tka odzna�en� v�ech epoch.
	 */
	private class FunctionUnselectAllEpochs implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	signalsWindowProvider.unselectAllEpochs();
        }
    }
	
	
	/**
	 * Obsluhuje akci p�i stisknut� tla��tka odzna�en� v�eho.
	 */
	private class FunctionUnselectAll implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	signalsWindowProvider.unselectAllEpochsAndArtefacts();
        }
    }
}
