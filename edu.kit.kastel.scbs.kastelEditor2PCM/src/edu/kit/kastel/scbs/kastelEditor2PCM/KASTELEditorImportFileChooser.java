package edu.kit.kastel.scbs.kastelEditor2PCM;



import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class KASTELEditorImportFileChooser extends JPanel implements ActionListener{

	static private final String newline = "\n";
	JButton jsonOpenButton, savePCMModelButton, saveConfidentialityButton ,saveTrackingJsonButton;
	JTextArea log;
	JFileChooser fc;
	KastelEditorJsonReader reader;
	PCMElementGenerator gen;
	ConfidentialitySpecificationGenerator confidentialityGenerator;
	
	public KASTELEditorImportFileChooser(KastelEditorJsonReader reader, PCMElementGenerator gen, ConfidentialitySpecificationGenerator generator){
		super(new BorderLayout());
		this.reader = reader;
		this.gen = gen;
		this.confidentialityGenerator = generator;
		
		 log = new JTextArea(5,20);
	        log.setMargin(new Insets(5,5,5,5));
	        log.setEditable(false);
	        JScrollPane logScrollPane = new JScrollPane(log);
	 
	
	        fc = new JFileChooser();
	 
	        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	 
	        //Create the open button.  We use the image from the JLF
	        //Graphics Repository (but we extracted it from the jar).
	        jsonOpenButton = new JButton("Open KASTEL-Editor Json");
	        jsonOpenButton.addActionListener(this);
	 
	        //Create the save button.  We use the image from the JLF
	        //Graphics Repository (but we extracted it from the jar).
	        savePCMModelButton = new JButton("Save PCM-Model");
	        savePCMModelButton.addActionListener(this);
	        
		    saveConfidentialityButton = new JButton("Generate and Save Confidentiality-Model");
		    saveConfidentialityButton.addActionListener(this);
	        
	        saveTrackingJsonButton = new JButton("Save Tracking-Information");
	        saveTrackingJsonButton.addActionListener(this);
	 
	        //For layout purposes, put the buttons in a separate panel
	        JPanel buttonPanel = new JPanel(); //use FlowLayout
	        buttonPanel.add(jsonOpenButton);
	        buttonPanel.add(savePCMModelButton);
	        buttonPanel.add(saveConfidentialityButton);
	        buttonPanel.add(saveTrackingJsonButton);
	        
	        jsonOpenButton.setEnabled(true);
	        savePCMModelButton.setEnabled(false);
	        saveConfidentialityButton.setEnabled(false);
	        saveTrackingJsonButton.setEnabled(false);
	        
	        //Add the buttons and the log to this panel.
	        add(buttonPanel, BorderLayout.PAGE_START);
	        add(logScrollPane, BorderLayout.CENTER);

	}
	
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == jsonOpenButton) {
			 handleOpenJsonButtonAction();
		} else if (e.getSource() == saveConfidentialityButton) {
			handleSaveConfidentialityInformationButtonAction();
		} else if (e.getSource() == savePCMModelButton) {
			handleSavePCMButtonAction();
		} else if (e.getSource() == saveTrackingJsonButton) {
			handleSaveTrackingButtonAction();
		}
	}
	
	static void createAndShowGUI(KASTELEditorImportFileChooser chooser) {
		JFrame frame = new JFrame("KASTEL-Editor to PCM Importer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(chooser);
		frame.pack();
		frame.setVisible(true);
	}
	
	void handleSaveTrackingButtonAction() {
		int returnVal = fc.showOpenDialog(KASTELEditorImportFileChooser.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            log.append("Starting writing Editor-to-Model Tracking Information in JSON-Format to " + file.getName() + "." + newline);
            reader.save(file);
            log.append("Tracking written" + newline);
		} else {
            log.append("Open command cancelled by user." + newline);
        }
        log.setCaretPosition(log.getDocument().getLength());
	}
	
	void handleOpenJsonButtonAction() {
		int returnVal = fc.showOpenDialog(KASTELEditorImportFileChooser.this);
		 
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	File file = fc.getSelectedFile();
        	log.append("Starting generating PCM-Model out of KASTELEditor JSON " + newline);
        	reader.extractKastelEditorModelFromJson(file);
        	log.append("Generating successful" + newline);
        	savePCMModelButton.setEnabled(true);
        } else {
            log.append("Open command cancelled by user." + newline);
        }
        log.setCaretPosition(log.getDocument().getLength());
	}
	
	void handleSavePCMButtonAction() {
		int returnVal = fc.showOpenDialog(KASTELEditorImportFileChooser.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            log.append("Starting writing PCM Model to " + file.getName() + "." + newline);
            gen.generateRepositoryModel(reader, file);
            gen.savePCMModel();
            log.append("PCM Model written" + newline);
            saveTrackingJsonButton.setEnabled(true);
		} else {
            log.append("Open command cancelled by user." + newline);
        }
        log.setCaretPosition(log.getDocument().getLength());
	}
	
	void handleSaveConfidentialityInformationButtonAction() {
		int returnVal = fc.showOpenDialog(KASTELEditorImportFileChooser.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            log.append("Starting writing Confidentiality Model to " + file.getName() + "." + newline);
            confidentialityGenerator.generateConfidentialitySpecification(reader, file);
            confidentialityGenerator.saveConfidentialityModel();
            log.append("Confidentiality Model written" + newline);
		} else {
            log.append("Open command cancelled by user." + newline);
        }
        log.setCaretPosition(log.getDocument().getLength());
	}

}
