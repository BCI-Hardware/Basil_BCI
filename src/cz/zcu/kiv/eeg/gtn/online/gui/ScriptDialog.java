package cz.zcu.kiv.eeg.gtn.online.gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;


/**
 * Created by Jamape on 16.03.2017.
 */
public class ScriptDialog extends JDialog {
    MainFrame mf;
    StimuliTableModel stm;
    JFrame scriptFrame;
    Xml xml = new Xml();
    private int id = 1;
    private int countID = 0;
    ArrayList<Stimul>stimuls = new ArrayList<>();
    ArrayList<TextField>NamesTF = new ArrayList<>();
    ArrayList<TextField>Files1TF = new ArrayList<>();
    ArrayList<TextField>Files2TF = new ArrayList<>();
    ArrayList<JButton> ChooseFile1BT = new ArrayList<>();
    ArrayList<JButton> ChooseFile2BT = new ArrayList<>();
    ArrayList<JButton> RemoveBT = new ArrayList<>();
    private JScrollPane sP;
    private JPanel scriptPanel;
    private JPanel boxPanel;


    public ScriptDialog(JFrame frame) {
        super(frame);
        this.scriptFrame = frame;
        this.setModal(true);
        this.setTitle("Script menu");
        this.getContentPane().add(createScriptPanel());
        this.setMinimumSize(new Dimension(420,280));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private JPanel createScriptPanel(){

        scriptPanel = new JPanel();
        scriptPanel.setLayout(new BorderLayout());

        boxPanel = new JPanel();
        boxPanel.setLayout(new BoxLayout(boxPanel,BoxLayout.PAGE_AXIS));
        boxPanel.add(scriptMain(),BorderLayout.CENTER);
        boxPanel.add(scriptMain(),BorderLayout.CENTER);
        sP = new JScrollPane(boxPanel);
        scriptPanel.add(sP,BorderLayout.CENTER);
        scriptPanel.add(scriptOption(),BorderLayout.PAGE_END);

       return scriptPanel;


    }


    private JPanel scriptMain(){
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints g = new GridBagConstraints();
        final JPanel itemJP = new JPanel(gb);

        String p = setID();

        final JLabel idLabel = new JLabel(p);

        final TextField nameTF = new TextField(10);

        final TextField file1TF = new TextField(15);
        final TextField file2TF = new TextField(15);

        JButton chooseFile2BT = new JButton("Choose a file");
        JButton chooseFile1BT = new JButton("Choose a file");

        JButton removeBT = new JButton("Remove");


        ChooseFile1BT.add(chooseFile1BT);
        ChooseFile2BT.add(chooseFile2BT);

        Files1TF.add(file1TF);
        Files2TF.add(file2TF);

        NamesTF.add(nameTF);

        RemoveBT.add(removeBT);


        chooseFile1BT.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser fc = new JFileChooser();
                int f = fc.showOpenDialog(ScriptDialog.this);
                if (f == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    file1TF.setText(file.getAbsolutePath());
                }
            }
        });

        chooseFile2BT.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser fc = new JFileChooser();
                int f = fc.showOpenDialog(ScriptDialog.this);
                if (f == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    file2TF.setText(file.getAbsolutePath());
                }
            }
        });

        removeBT.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                countID--;
                int dRes = JOptionPane.showConfirmDialog(null,
                        "Do you want delete this stimul? ", nameTF.getText(),
                        JOptionPane.OK_CANCEL_OPTION);
                if (JOptionPane.OK_OPTION == dRes) {
                    if (countID == 1) {
                        showMessageDialog(ScriptDialog.this,
                                "You can't delete last two stimuls", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        countID++;
                    } else {

                        itemJP.removeAll();
                        ScriptDialog.this.setSize(ScriptDialog.this.getWidth(), ScriptDialog.this.getHeight() - 100);
                    }
                }
            }
        });

        g.insets = new Insets(0,10,0,10);
        g.fill = GridBagConstraints.HORIZONTAL;
        itemJP.add(new JLabel("ID"),g);
        g.gridx = 1;
        itemJP.add(new JLabel("Name"),g);
        g.gridx = 2;
        itemJP.add(new JLabel("File 1"),g);
        g.gridx = 0;
        g.gridy = 1;
        itemJP.add(idLabel,g);
        g.gridx = 1;
        itemJP.add(nameTF,g);
        g.gridx = 2;
        itemJP.add(file1TF,g);
        g.gridx = 3;
        itemJP.add(chooseFile1BT,g);
        g.gridy = 5;
        itemJP.add(removeBT,g);
        g.gridy = 2;
        g.gridx = 2;
        itemJP.add(new JLabel("File 2"),g);
        g.gridy = 3;
        itemJP.add(file2TF,g);
        g.gridx = 3;
        itemJP.add(chooseFile2BT,g);
        g.gridwidth = 4;
        g.gridy = 6;
        g.gridx = 0;
        itemJP.add(new JSeparator(JSeparator.HORIZONTAL),g);

        scriptPanel.revalidate();

        return itemJP;
    }

    private String setID(){
        String idText = Integer.toString(id);
        id++;
        countID++;
        return idText;
    }

    private JPanel scriptOption(){
        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new BoxLayout(optionPanel,BoxLayout.LINE_AXIS));

        JButton addBT = new JButton("+");
        JButton importBT = new JButton("Import data");
        JButton applyBT = new JButton("Apply");
        JButton cancelBT = new JButton("Cancel");

        addBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boxPanel.add(scriptMain());
                if (ScriptDialog.this.getHeight() <= 500)ScriptDialog.this.setSize(ScriptDialog.this.getWidth(),ScriptDialog.this.getHeight()+100);
            }
        });
        cancelBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ScriptDialog.this.dispose();
            }
        });

        applyBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // stm = new StimuliTableModel(countID);
                xml.save(NamesTF,Files1TF,Files2TF);
                ScriptDialog.this.dispose();
            }
        });
        importBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files","xml");
                JFileChooser fc = new JFileChooser();
                fc.setFileFilter(filter);
                int f = fc.showOpenDialog(ScriptDialog.this);
                if (f == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    stimuls=xml.load(file);
                    for (int i = 0; i < NamesTF.size(); i++) {
                      NamesTF.get(i).setText(stimuls.get(i).name);
                      Files1TF.get(i).setText(stimuls.get(i).file1);
                      Files2TF.get(i).setText(stimuls.get(i).file2);
                    }
                }


            }
        });

        optionPanel.add(Box.createRigidArea(new Dimension(10,0)));
        optionPanel.add(addBT);
        optionPanel.add(Box.createRigidArea(new Dimension(10,0)));
        optionPanel.add(importBT);
        optionPanel.add(Box.createRigidArea(new Dimension(95,0)));
        optionPanel.add(applyBT);
        optionPanel.add(Box.createRigidArea(new Dimension(5,0)));
        optionPanel.add(cancelBT);

        return optionPanel;
    }
}
