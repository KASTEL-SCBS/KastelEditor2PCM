package edu.kit.kastel.scbs.kastelEditor2PCM.Tracking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import edu.kit.kastel.scbs.kastelEditor2PCM.KASTELGoalModelReader;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.ServiceComponent;
import edu.kit.kastel.scbs.kastelEditor2PCM.Util.IOUtil;

public class TrackingGenerator {
	private Collection<ExtensionTracking> toTrack;

	public TrackingGenerator() {
		toTrack = new ArrayList<ExtensionTracking>();
	}
	
	public TrackingGenerator(Collection<ExtensionTracking> toTrack) {
		this.toTrack = toTrack;
	}
	
	public void addExtensionTracking(ExtensionTracking tracking) {
		this.toTrack.add(tracking);
	}
	
	public void writeTracking(KASTELGoalModelReader reader, String destinationPath) {
		TrackingElement element = new TrackingElement(reader.getModelName(), reader.getServices());
		
		for(ExtensionTracking extensionTracking : toTrack) {
			element.addExtensionElements(extensionTracking.getExtensionTrackingElement());
		}
		
		saveTrackingFile(destinationPath, element);
	}
	
	private boolean saveTrackingFile(String destinationPath, TrackingElement element) {
		
	
		
		if(!destinationPath.endsWith(".json")) {
			destinationPath += ".json";
		}
		File json = new File(destinationPath);
		
		if(!json.exists()) {
		try {
			json.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
		
		
		Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
		String s = gson.toJson(element);
		return IOUtil.writeToFile(json, s);
	}
	
	private class TrackingElement {
		
		
		@Expose private String project;
		@Expose private Collection<ServiceComponent> services;
		@Expose private Collection<ExtensionInformationTrackingElement> extensionTrackingElements;
		
		public TrackingElement(String projectName, Collection<ServiceComponent> services) {
			this.project = projectName + "Tracking";
			this.services = services;
			extensionTrackingElements = new ArrayList<ExtensionInformationTrackingElement>();
		}
		
		public TrackingElement(String projectName, Collection<ServiceComponent> services, Collection<ExtensionInformationTrackingElement> extensionElements) {
			this.project = projectName + "Tracking";
			this.services = services;
			this.extensionTrackingElements = extensionElements;
		} 
		
		public void addExtensionElement(ExtensionInformationTrackingElement element) {
			this.extensionTrackingElements.add(element);
		}
		
		public void addExtensionElements(Collection<ExtensionInformationTrackingElement> elements) {
			extensionTrackingElements.addAll(elements);
		}
		
		
	}
	
}
