package icp.gui.signals;

import icp.Const;
import icp.aplication.*;
import icp.data.Header;
import icp.gui.GuiController;

import java.awt.Color;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

/**
 * Rozhran� mezi aplika�n� a prezenta�n� vrstvou.
 * Slou�� pro komunikaci mezi oknem se zobrazen�mi sign�ly, aplika�n� vrstvou 
 * a mezi ostatn�mi pot�ebn�mi komponentami
 * 
 * @author Petr Soukal
 */
public class SignalsWindowProvider implements Observer {
	private final int EPOCH_MIN_LENGTH = 800;
    private SessionManager appCore;
    private DrawingComponent drawingComponent;
    private SignalsSegmentation signalsSegmentation;
    protected SignalsWindow signalsWindow;
    private GuiController guiController;
    private ImageIcon playIcon;
    private ImageIcon pauseIcon;
    private ImageIcon stopIcon;
    private ImageIcon selectionEpochIcon;
    private ImageIcon unselectionEpochIcon;
    private ImageIcon playbackIcon;
    private int selectedFunction;
    private int selectionStart; // bude tato promenna (+ set/gettery) jeste potreba?
    private int selectionEnd;   // bude tato promenna (+ set/gettery) jeste potreba?
    private boolean areaSelection = false;
    private boolean changeEpochInterval = false;
    private boolean showPopupMenu = false;
    private Color colorSelection;
//    private Header header;
//    private Buffer buffer;
    private int numberOfDrawableChannels; // Po�et vybran�ch sign�l� (tedy t�ch, kter� mohou, ale nemus� b�t zobrazeny)
    private int numberOfVisibleChannels;  // Po�et zobrazen�ch sign�l�
    private int firstVisibleChannel;      // Index prvn�ho vykreslovan�ho sign�lu
//    private List<Integer> visibleSignalsIndexes;
    private float paintVolume;


    /**
     * Vytv��� instance t��dy.
     * @param appCore - j�dro aplikace udr�uj�c� vztah mezi aplika�n� a prezenta�n� vrstvou.
     * @param guiController 
     */
    public SignalsWindowProvider(SessionManager appCore, GuiController guiController) {
        this.appCore = appCore;
        this.guiController = guiController;
        drawingComponent = new DrawingComponent(this);
        signalsWindow = new SignalsWindow(this);
        signalsSegmentation = this.appCore.getSignalsSegmentation();
        setFirstVisibleChannel(0);
    }

    /**
     * P�ij�m� zpr�vy pos�l�n� pomoc� guiControlleru.(Komunikace mezi providery)
     */
    public void update(Observable o, Object arg) {
        int msg;

        if (arg instanceof java.lang.Integer) {
            msg = ((Integer) arg).intValue();
        } else {
            return;
        }

        switch (msg) {
            case GuiController.MSG_PROJECT_CLOSED:
                setDrawingComponent();
                signalsWindow.verticalScrollBar.setEnabled(false);
                signalsWindow.horizontalScrollBar.setEnabled(false);
                signalsWindow.drawableSignalsCheckBoxes = null;
                signalsWindow.checkBoxesPanel.removeAll();
                setAllWindowControlsEnabled(false);
                showPopupMenu = false;
                signalsWindow.increaseNumberOfChannelsButton.setEnabled(false);
                signalsWindow.decreaseNumberOfChannelsButton.setEnabled(false);
                signalsWindow.playBT.setEnabled(false);
                signalsWindow.stopBT.setEnabled(false);
                break;                
            case GuiController.MSG_CURRENT_PROJECT_CHANGED:
            	setDrawingComponent();
            	setNumberOfVisibleChannels(appCore.getSelectedChannels().size());
            	setNumberOfVisibleChannels(1);
            	setSignalSegmentation();
                setAllWindowControlsEnabled(true);
                setFirstVisibleChannel(0);
                showPopupMenu = true;
                //recountChannels();
                averageSelectedEpochs();
//                signalsWindow.invertedSignalsButton.setSelected(appCore.getCurrentProject().isInvertedSignalsView());
               
                break;

            case GuiController.MSG_SIGNAL_PLAYBACK_START:
                setAllWindowControlsEnabled(false);
                showPopupMenu = false;
                signalsWindow.setPlayButtonIcon(getPauseIcon());
                selectedFunction = Const.SELECT_NOTHING;
                getDrawingComponent().startDrawing();
                break;

            case GuiController.MSG_SIGNAL_PLAYBACK_RESUME:
                signalsWindow.setPlayButtonIcon(getPauseIcon());
                showPopupMenu = false;
                setAllWindowControlsEnabled(false);
                selectedFunction = Const.SELECT_NOTHING;
                getDrawingComponent().togglePause();
                break;

            case GuiController.MSG_SIGNAL_PLAYBACK_PAUSE:
                signalsWindow.setPlayButtonIcon(getPlayIcon());
                setAllWindowControlsEnabled(true);
                showPopupMenu = true;
                selectedFunction = signalsWindow.getSelectedFunctionIndex();
                getDrawingComponent().togglePause();
                break;

            case GuiController.MSG_SIGNAL_PLAYBACK_STOP:
                getDrawingComponent().stopDrawing();
                showPopupMenu = true;
                signalsWindow.setPlayButtonIcon(getPlayIcon());
                signalsWindow.setHorizontalScrollbarValue(0);
                setAllWindowControlsEnabled(true);
                selectedFunction = signalsWindow.getSelectedFunctionIndex();
                break;
                
            case GuiController.MSG_CHANNEL_SELECTED:
                recountChannels();
                break;
                
            case GuiController.MSG_NEW_BUFFER:
            	
            	drawingComponent.setDrawedEpochs(signalsSegmentation.getEpochsDraw());
            	break;
                
            case GuiController.MSG_INVERTED_SIGNALS_VIEW_CHANGED:
//                signalsWindow.invertedSignalsButton.setSelected(appCore.getCurrentProject().isInvertedSignalsView());
                

        }

    }

    /**
     * Nastavuje jednotliv� p�edn� a zadn� hodnotu intervalu oblasti epoch.
     */
    void saveEpochInterval() {            	
        
        if (appCore.getBuffer() == null) {
            return;
        }
            if (drawingComponent != null) {
                int startValue = ((Integer) signalsWindow.startEpoch.getValue()).intValue();
                int endValue = ((Integer) signalsWindow.endEpoch.getValue()).intValue();
                
                if((startValue+endValue) < EPOCH_MIN_LENGTH)
                {
                	JOptionPane.showMessageDialog(null, "Epoch is short (length >= "+EPOCH_MIN_LENGTH+").", 
							"Epoch length error!", JOptionPane.ERROR_MESSAGE, 
							null);
                	return;
                }

                if (!setLeftEpochBorder(startValue)) {
                    signalsWindow.startEpoch.setValue(new Integer(appCore.getLeftEpochBorder()));
                    changeEpochInterval = true;
                }
                else {
                    appCore.setLeftEpochBorder((int) drawingComponent.timeToAbsoluteFrame(startValue));
                }
                                
                
                if (!setRightEpochBorder(endValue)) {
                    signalsWindow.endEpoch.setValue(new Integer(appCore.getRightEpochBorder()));
                    changeEpochInterval = true;
                }
                else {
                	appCore.setRightEpochBorder((int) drawingComponent.timeToAbsoluteFrame(endValue));
                }
                
                if(changeEpochInterval)
                {
                	guiController.sendMessage(GuiController.MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE);
                	changeEpochInterval = false;
                }
                
            }
    }
    
    /**
     * Zakazuje/povoluje tla��tka v okn� t��dy SignalsWindow.
     * 
     * @param enabled - hodnota zak�z�n�/povolen�.
     */
    private void setAllWindowControlsEnabled(boolean enabled) {
    			signalsWindow.saveEpochIntervalButton.setEnabled(enabled);
                signalsWindow.selectEpochTB.setEnabled(enabled);
                signalsWindow.unselectEpochTB.setEnabled(enabled);
                signalsWindow.selectPlaybackTB.setEnabled(enabled);
//                signalsWindow.invertedSignalsButton.setEnabled(enabled);
                signalsWindow.decreaseVerticalZoomButton.setEnabled(enabled);
                signalsWindow.increaseVerticalZoomButton.setEnabled(enabled);
                signalsWindow.decreaseHorizontalZoomButton.setEnabled(enabled);
                signalsWindow.increaseHorizontalZoomButton.setEnabled(enabled);
    }
    
    /**
     * Zv�t�� po�et zobrazen�ch sign�l� o jedna.<br/>
     * Hodnota mimo povolen� interval bude automaticky opravena na maximum nebo minimum intervalu.
     */
    protected synchronized void increaseNumberOfVisibleChannels() {
        setNumberOfVisibleChannels(numberOfVisibleChannels + 1);
    }
    
    /**
     * Zmen�� po�et zobrazen�ch sign�l� o jedna.
     * Hodnota mimo povolen� interval bude automaticky opravena na maximum nebo minimum intervalu.
     */
    protected synchronized void decreaseNumberOfVisibleChannels() {
        setNumberOfVisibleChannels(numberOfVisibleChannels - 1);
    }
    
    protected void decreaseVerticalZoom() {
        float zoom = getDrawingComponent().getVerticalZoom();
        float step = zoom / 5f;
        
        zoom += step;
        getDrawingComponent().setVerticalZoom(zoom);
    }

    protected void increaseVerticalZoom() {
        float zoom = getDrawingComponent().getVerticalZoom();
        float step = zoom / 5f;
        zoom -= step;
        if (zoom <= 1) {
            return;
        }
        getDrawingComponent().setVerticalZoom(zoom);
    }

    protected void increaseHorizontalZoom() {
        long time = getDrawingComponent().frameToTime(getDrawingComponent().getDrawedFrames());
        
        if (time >= 2000) {
            time -= 1000;
        }
//        time = time - (time % 1000);
        
        getDrawingComponent().setHorizontalZoom((int) getDrawingComponent().timeToAbsoluteFrame(time));
    }

    protected void decreaseHorizontalZoom() {
        long time = getDrawingComponent().frameToTime(getDrawingComponent().getDrawedFrames());
        time += 1000;
        getDrawingComponent().setHorizontalZoom((int) getDrawingComponent().timeToAbsoluteFrame(time));
    }

    /**
     * Nastavuje parametry obejtku t��dy SignalSegmentation podle
     * parametr� aktu�ln�ho projektu.
     */
    private synchronized void setSignalSegmentation()
    {
    	signalsSegmentation.setSegmentArrays();
    	
    	Integer startValue = (Integer) signalsWindow.startEpoch.getValue();
        Integer endValue = (Integer) signalsWindow.endEpoch.getValue();
        //inicializace dat p�i na��t�n� projektu
        signalsSegmentation.setEpochs(appCore.getEpochs());        
        getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
        
    }
    /**
     * Nastavuje vykreslovac� komponentu, na kterou se budou vykreslovat vybran� sign�ly.
     * TODO - tuhle prasarnu predelat - predelano, ale mozna by se dalo jeste trochu
     */
    protected synchronized void setDrawingComponent() {
        if (appCore.getBuffer() == null) {
            drawingComponent.setDataSource(null, null);
            signalsWindow.horizontalScrollBar.setEnabled(false);
        } else {
            drawingComponent.setDataSource(appCore.getBuffer(), appCore.getHeader());
            drawingComponent.setDrawableChannels(appCore.getSelectedChannels());
            resetHorizontalScrollbarMaximum();
            signalsWindow.horizontalScrollBar.setMinimum(0);
            signalsWindow.horizontalScrollBar.setEnabled(true);
            signalsWindow.verticalScrollBar.setEnabled(true);

            signalsWindow.selectEpochTB.setSelected(true);
            setSelectedFunction(signalsWindow.getSelectedFunctionIndex());
            signalsWindow.playBT.setEnabled(true);
            signalsWindow.stopBT.setEnabled(true);
        }        

    }

    protected void togglePause() {
        if (getDrawingComponent().isPaused()) {
            if (getDrawingComponent().isRunning()) {
                guiController.sendMessage(GuiController.MSG_SIGNAL_PLAYBACK_RESUME);
            } else {
                guiController.sendMessage(GuiController.MSG_SIGNAL_PLAYBACK_START);
            }
        } else {
            guiController.sendMessage(GuiController.MSG_SIGNAL_PLAYBACK_PAUSE);
        }
    }

    protected ImageIcon getIcon(String name) {
        if (name == null) {
            return null;
        }
        ImageIcon icon = guiController.loadIcon(name);
        return icon;
    }
    
    protected void stopPlayback() {
        guiController.sendMessage(GuiController.MSG_SIGNAL_PLAYBACK_STOP);
    }

    public JPanel getWindow() {
        return signalsWindow;
    }

    /**
     * @return ikonu pro p�ehr�v�n�. 
     */
    protected ImageIcon getPlayIcon() {
        if (playIcon == null) {
            playIcon = guiController.loadIcon("play24.gif");
        }
        return playIcon;
    }
    /**
     * @return ikonu pro pauzu. 
     */

    protected ImageIcon getPauseIcon() {
        if (pauseIcon == null) {
            pauseIcon = guiController.loadIcon("pause24.gif");
        }
        return pauseIcon;
    }
    
    /**
     * @return ikonu pro zastaven� p�ehr�v�n�. 
     */
    protected ImageIcon getStopIcon() {
        if (stopIcon == null) {
            stopIcon = guiController.loadIcon("stop24.gif");
        }
        return stopIcon;
    }
    
    /**
     * @return ikonu ozna�ov�n� epoch. 
     */
    protected ImageIcon getSelectionEpochIcon() {
        if (selectionEpochIcon == null) {
        	selectionEpochIcon = guiController.loadIcon("selEpochIcon.gif");
        }
        return selectionEpochIcon;
    }
    

    /**
     * @return ikonu odzna�ov�n� epoch. 
     */
    protected ImageIcon getUnselectionEpochIcon() {

        if (unselectionEpochIcon == null) {
        	unselectionEpochIcon = guiController.loadIcon("unselEpochIcon.gif");
        }
        return unselectionEpochIcon;
    }
    
       
    /**
     * @return ikonu nastaven� ukazatele p�ehr�v�n�. 
     */
    protected ImageIcon getPlaybackIcon() {
        if (playbackIcon == null) {
        	playbackIcon = guiController.loadIcon("playbackicon.png");
        }
        return playbackIcon;
    }

    protected void setVerticalZoom(int vZoom) {
        if (getDrawingComponent() != null) {
//            System.out.println("vzoom: " + vZoom);
            getDrawingComponent().setVerticalZoom(vZoom);
        }
    }

    protected void setHorizontalZoom(int hZoom) {
        if (getDrawingComponent() != null) {
            getDrawingComponent().setHorizontalZoom(hZoom);
            resetHorizontalScrollbarMaximum();
//            drawingComponent.refresh();
        }
    }

    protected void resetHorizontalScrollbarMaximum() {
        signalsWindow.horizontalScrollBar.setMaximum((int) appCore.getHeader().getNumberOfSamples() - drawingComponent.getDrawedFrames() + 20);
    }

    protected void setSelectedFunction(int value) {
        this.selectedFunction = value;
    }
    
    /**
	 * Nastavuje parametry popup-menu a jeho zobrazen�.
	 * 
	 * @param visualComponent - komponenta, ke kter� se menu v�e.
	 * @param xAxis - x-ov� sou�adnice zobrazen� menu.
	 * @param yAxis - y-ov� sou�adnice zobrazen� menu.
	 * @param frame - m�sto v souboru, p�epo��tan� ze sou�adnic kliku.
	 */
    protected void setPopupmenu(JComponent visualComponent, int xAxis, int yAxis, long frame)
    {
    	if(showPopupMenu)
    	{
	    	boolean enabledSelEpoch = signalsSegmentation.getEnabledSelEpoch(frame);
	    	boolean enabledUnselEpoch = signalsSegmentation.getEnabledUnselEpoch(frame);
	    	boolean enabledUnselArtefact = signalsSegmentation.getEnabledUnselArtefact(frame);
	    	boolean enabledUnselAllEpochs = signalsSegmentation.getEnabledUnselAllEpochs();
	    	boolean enabledUnselAllArtefacts = signalsSegmentation.getEnabledUnselAllArtefacts();
	    	boolean enabledUnselAll;
	    	
	    	if (enabledUnselAllEpochs || enabledUnselAllArtefacts) {
	    		enabledUnselAll = true;
                } else {
	    		enabledUnselAll = false;
                }
	    	
	    	signalsWindow.optionMenu.setEnabledItems(enabledSelEpoch, enabledUnselEpoch, 
	    			enabledUnselArtefact, enabledUnselAllEpochs, enabledUnselAllArtefacts, enabledUnselAll);
	    	
	    	signalsWindow.optionMenu.setVisibleMenu(visualComponent, xAxis, yAxis, frame);
    	}
    }
    
    /**
     * Pos�l� informace pro ozna�en� epochy objektu t��dy SignalSegmentation.
     * 
     * @param frame - m�sto ozna�en� epochy.
     */
    protected void selectEpoch(long frame)
    {
    	
    	signalsSegmentation.selectEpoch((int)frame);
    	getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
    	averageSelectedEpochs();
    }
    
    /**
     * Pos�l� informace pro odzna�en� epochy objektu t��dy SignalSegmentation.
     * 
     * @param frame - m�sto odzna�en� epochy.
     */
    protected void unselectEpoch(long frame)

    {
    	
    	signalsSegmentation.unselectEpoch((int)frame);
    	getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
    	averageSelectedEpochs();
    }
    
 

    /**
     * Pos�l� informace pro odzna�en� v�ech epoch objektu t��dy SignalSegmentation.
     */
    protected void unselectAllEpochs()
    {
    	
    	signalsSegmentation.unselectionAllEpochs();
    	getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
    	averageSelectedEpochs();
    }
    
    /**
     * Pos�l� informace pro odzna�en� v�eho objektu t��dy SignalSegmentation.
     */
    protected void unselectAllEpochsAndArtefacts()
    {
    	signalsSegmentation.unselectionAllArtefacts();
    	signalsSegmentation.unselectionAllEpochs();
    	getDrawingComponent().setDrawedArtefacts(signalsSegmentation.getArtefactsDraw());
    	getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
    	averageSelectedEpochs();
    }

    /**
     * Provede operaci podle aktu�ln� vybran� funkce p�i stisku tla��tka my�i.
     * 
     * @param position - pozice kursoru p�i stisku tla��tka my�i.
     */
    protected void setPressedPosition(long position) {
        int xAxis = (int) position;

        switch (selectedFunction) {
            case Const.SELECT_PLAYBACK:
                drawingComponent.setPlaybackIndicatorPosition(position);
                break;
                
            case Const.SELECT_EPOCH:// TODO - pridat znacku	 

                if (signalsSegmentation.selectEpoch(xAxis)) {
                    getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
                    averageSelectedEpochs();
                }
                break;
                
            case Const.UNSELECT_EPOCH:
                if (signalsSegmentation.unselectEpoch(xAxis)) {
                    getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
                    averageSelectedEpochs();
                }
                break;
        }
    }

    /**
     * Nastavuje levou oblast epochy.
     * 
     * @param start - po��te�n� hodnotu intervalu epochy. 
     * @return true - pokud lze tuto hodnotu ulo�it.<br>
     * false - pokud nelze tuto hodnotu ulo�it.
     */
    protected boolean setLeftEpochBorder(int start) {
    	int leftBorder = (int)drawingComponent.timeToAbsoluteFrame(start);
    	
        if (signalsSegmentation.setLeftEpochBorder(leftBorder)) {
        	getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
        	guiController.sendMessage(GuiController.MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Nastavuje pravou oblast epochy.
     * 
     * @param end - kone�nou hodnotu intervalu epochy. 
     * @return true - pokud lze tuto hodnotu ulo�it.<br>
     * false - pokud nelze tuto hodnotu ulo�it.
     */
    protected boolean setRightEpochBorder(int end) {
    	int rightBorder = (int)drawingComponent.timeToAbsoluteFrame(end);
    	
        if (signalsSegmentation.setRightEpochBorder(rightBorder)) {
        	getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
        	guiController.sendMessage(GuiController.MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return ArrayList index� epoch k pr�m�rov�n�
     */
    protected ArrayList<Integer> getIndicesEpochsForAveraging() {

        return signalsSegmentation.getIndicesEpochsForAveraging();
    }
    

    /**
     * @return (int) hodnotu aktu�ln� funkce.
     */
    protected int getSelectedFunction() {
        return selectedFunction;
    }


    /**
     * @return (boolean) hodnotu zda se ozna�uje souvisl� oblast.
     * Nap�. p�i ozna�ov�n� artefakt�.
     */
    protected boolean isAreaSelection() {
        return areaSelection;
    }

    /**
     * Nastavuje po��te�n� hodnotu vykreslovan� oblasti.
     * @param xAxis 
     */
    protected void setStartSelection(int xAxis) {
        selectionStart = xAxis;
    }


    /**
     * @return po��te�n� hodnotu vykreslovan� oblasti.
     */
    protected int getStartSelection() {
        return selectionStart;
    }

    /**
     * Nastavuje kone�nou hodnotu vykreslovan� oblasti.
     * @param xAxis 
     */
    protected void setEndSelection(int xAxis) {
        selectionEnd = xAxis - selectionStart;
    }

    /**
     * @return kone�nou hodnotu vykreslovan� oblasti.
     */
    protected int getEndSelection() {
    	return selectionEnd;
    }

    /**
     * @return nastavenou barvu vykreslovan� oblasti.
     */
    protected Color getColorSelection() {
        return colorSelection;
    }
    
    /**
     * P�epo�te hodnoty vykresliteln�ch a zobrazen�ch sign�l� a nastav� souvisej�c� parametry GUI.
     */
    private synchronized void recountChannels() {
        try {
            numberOfDrawableChannels = appCore.getSelectedChannels().size();
        } catch (NullPointerException e) {
            return;
        }
        
        if (numberOfVisibleChannels > numberOfDrawableChannels) {
            numberOfVisibleChannels = numberOfDrawableChannels;
        } else if (numberOfVisibleChannels < 1) {
            numberOfVisibleChannels = 1;
        }

        int maximalFirstVisibleChannel = numberOfDrawableChannels - numberOfVisibleChannels;
        
        if (firstVisibleChannel + numberOfVisibleChannels > numberOfDrawableChannels) {
            firstVisibleChannel = maximalFirstVisibleChannel;
        } else if (firstVisibleChannel < 0) {
            firstVisibleChannel = 0;
        }

        drawingComponent.setDrawableChannels(appCore.getSelectedChannels());
        drawingComponent.loadNumbersOfSignals();
        signalsWindow.verticalScrollBar.setEnabled(numberOfVisibleChannels < numberOfDrawableChannels);
        signalsWindow.verticalScrollBar.setMaximum(maximalFirstVisibleChannel);
        signalsWindow.verticalScrollBar.setValue(firstVisibleChannel);
        setVisibleSignals();

        signalsWindow.setNumberOfSelectedSignalsButtonsEnabled(numberOfVisibleChannels > 1, numberOfVisibleChannels < numberOfDrawableChannels);

    }

    /**
     * Nastavuje indexy epoch k pr�m�rov�n� a pos�l� o tom zpr�vu.
     */
    protected void averageSelectedEpochs() {

        //appCore.getCurrentProject().setAveragedEpochsIndexes(getIndicesEpochsForAveraging());
        guiController.sendMessage(GuiController.MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE);
    }
    

    /**
     * Nastavuje vykreslovan� sign�ly.
     */
    protected void setVisibleSignals() {
        
        Header header = appCore.getHeader();
        ChannelCheckBoxListener channelCheckBoxListener = new ChannelCheckBoxListener();
        
        List<Integer> selectedChannels = appCore.getSelectedChannels();
        
        signalsWindow.drawableSignalsCheckBoxes = new JCheckBox[selectedChannels.size()];

//        paintVolume = signalsWindow.checkBoxesPanel.getHeight() / (float) getNumberOfVisibleChannels(); // FIXME ()
        for (int i = 0; i < signalsWindow.drawableSignalsCheckBoxes.length; i++) {
            JCheckBox checkBox = new JCheckBox(header.getChannels().get(selectedChannels.get(i)).getName());
            JLabel label = new JLabel("", JLabel.RIGHT);
            
            checkBox.setToolTipText(header.getChannels().get(selectedChannels.get(i)).getName());
            checkBox.setForeground(Const.DC_SIGNALS_COLORS[i % Const.DC_SIGNALS_COLORS.length]);
            checkBox.setOpaque(false);
            

            checkBox.addActionListener(channelCheckBoxListener);
            
            label.setBackground(Color.WHITE);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createLineBorder(Const.DC_SIGNALS_COLORS[i % Const.DC_SIGNALS_COLORS.length], SignalsWindow.LABEL_LINE));
            label.setForeground(Const.DC_SIGNALS_COLORS[i % Const.DC_SIGNALS_COLORS.length]);

            signalsWindow.drawableSignalsCheckBoxes[i] = checkBox;
        }

        signalsWindow.repaintVisibleSignals();

        signalsWindow.repaint();
        signalsWindow.validate();
    }

    
    /**
     * Nastav� po�et zobrazen�ch sign�l� na zadanou hodnotu.<br/>
     * Bude-li pou�ita hodnota v�t��, ne� je po�et vykresliteln�ch sign�l�,
     * po�et zobrazen�ch sign�l� se nastav� na po�et vykresliteln�ch sign�l�.
     * @param count Po�et zobrazen�ch sign�l�.
     */
    private synchronized void setNumberOfVisibleChannels(int count) {
        numberOfVisibleChannels = count;
        recountChannels();
    }
    
    /**
     * Nastav� index prvn�ho zobrazen�ho kan�lu.<br/>
     * Hodnota mimo rozsah se p�epo�te na nejbli��� povolenou hodnotu.
     * @param index Index prvn�ho zobrazen�ho kan�lu.
     */
    protected synchronized void setFirstVisibleChannel(int index) {
        firstVisibleChannel = index;
        recountChannels();
    }
    
    /**
     * Vrac� po�et vybran�ch sign�l�, tzn. po�et sign�l�, kter� mohou b�t vykresleny.
     * @return Po�et sign�l�, kter� mohou b�t vykresleny.
     */
    protected synchronized int getNumberOfDrawableChannels() {
        return numberOfDrawableChannels;
    }

    /**
     * Vrac� po�et vykreslovan�ch sign�l�.<br/>
     * Je v�y men�� nebo roven po�tu sign�l�, kter� mohou b�t vykresleny.
     * @return Po�et vykreslovan�ch sign�l�.
     */
    protected synchronized int getNumberOfVisibleChannels() {
        return numberOfVisibleChannels;
    }

    /**
     * Vrac� index prvn�ho vykreslovan�ho sign�lu.
     * @return Index prvn�ho vykreslovan�ho sign�lu.
     */
    protected synchronized int getFirstVisibleChannel() {
        return firstVisibleChannel;
    }

    protected DrawingComponent getDrawingComponent() {
        return drawingComponent;
    }
    
    protected float getPaintVolume() {
        return paintVolume;
    }
    
    protected JPopupMenu getOptionMenu() {
        return signalsWindow.optionMenu;
    }
    
    
    /**
     * Obsluhuje funkci CheckBox� jednotliv�ch kan�l�, 
     * kter� se maj� vyu��vat p�i pr�m�rov�n�.
     */
    private class ChannelCheckBoxListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            
            ArrayList<Integer> averagedChannels = new ArrayList<Integer>();

            for (int i = 0; i < signalsWindow.drawableSignalsCheckBoxes.length; i++) {

                if (signalsWindow.drawableSignalsCheckBoxes[i].isSelected()) {
                    averagedChannels.add(appCore.getSelectedChannels().get(i));
                }
            }
            
            //project.setAveragedSignalsIndexes(averagedChannels);            
            averageSelectedEpochs();
        }
    }
    
//    protected void toggleInvertedView() {
//        appCore.getCurrentProject().setInvertedSignalsView(signalsWindow.invertedSignalsButton.isSelected());
//        guiController.sendMessage(GuiController.MSG_INVERTED_SIGNALS_VIEW_CHANGED);
//    }
}
