package edu.pitt.lrdc.cs.revision.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
 
public class FilePicker extends JPanel {
    private String textFieldLabel;
    private String buttonLabel;
     
    private JLabel label;
    private JTextField textField;
    private JButton button;
     
    private JFileChooser fileChooser;
     
    private int mode = MODE_SAVE;
    public static final int MODE_OPEN = 1;
    public static final int MODE_SAVE = 2;
    
    public void disable() {
    	this.button.setEnabled(false);
    	this.textField.setEnabled(false);
    }
    
    public void enable() {
    	this.button.setEnabled(true);
    	this.textField.setEnabled(true);
    }
    
    public void setFileSelectionMode(int mode) {
        this.fileChooser.setFileSelectionMode(mode);
    }
     
    public void setChooseFolder() {
    	this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }
    
    public void setChooseFile() {
    	this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }
    
    public void setChooseBoth() {
    	this.fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    }
    
    public FilePicker(String textFieldLabel, String buttonLabel) {
        this.textFieldLabel = textFieldLabel;
        this.buttonLabel = buttonLabel;
         
        fileChooser = new JFileChooser();
         
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
 
        // creates the GUI
        label = new JLabel(textFieldLabel);
         
        textField = new JTextField(30);
        button = new JButton(buttonLabel);
         
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                buttonActionPerformed(evt);            
            }
        });
         
        add(label);
        add(textField);
        add(button);
         
    }
     
    private void buttonActionPerformed(ActionEvent evt) {
        if (mode == MODE_OPEN) {
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        } else if (mode == MODE_SAVE) {
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        }
    }
 
    public void setMode(int mode) {
        this.mode = mode;
    }
     
    public String getSelectedFilePath() {
        return textField.getText();
    }
     
    public JFileChooser getFileChooser() {
        return this.fileChooser;
    }
}