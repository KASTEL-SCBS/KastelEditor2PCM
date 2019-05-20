package edu.kit.kastel.scbs.kastelEditor2PCM;


import javax.swing.*;


public class Main {
	
	

	public static void main(String[] args) {
		KASTELEditorImportFileChooser chooser = new KASTELEditorImportFileChooser(new KastelEditorJsonReader(), new PCMElementGenerator(), new ConfidentialitySpecificationGenerator());
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				KASTELEditorImportFileChooser.createAndShowGUI(chooser);
			}
		});
		
	}
}
