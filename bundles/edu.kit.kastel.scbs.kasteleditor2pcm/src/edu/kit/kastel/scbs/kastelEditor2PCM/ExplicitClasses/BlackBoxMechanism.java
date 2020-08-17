package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class BlackBoxMechanism extends EditorElement {

	@Expose private String primaryInterfaceId;
	//TODO Reeeeaaally bad design, redesign necessary
	private Set<Asset> editorAssets;
	@Expose private Set<InterfaceMapping> providedBBMInterfaces;
	
	public BlackBoxMechanism(String name) {
		super.setName(name);
		primaryInterfaceId = "";
		editorAssets = new HashSet<Asset>();
		providedBBMInterfaces = new HashSet<InterfaceMapping>();
	}


	public String getBbmComponentId() {
		return super.getId();
	}

	public void setBbmComponentId(String bbmComponentId) {
		super.setId(bbmComponentId);
	}
	
	public void addPrimaryInterface(InterfaceMapping mapping) {
		primaryInterfaceId = mapping.getId();
		providedBBMInterfaces.add(mapping);
	}
	
	public String getPrimaryInterfaceId() {
		return primaryInterfaceId;
	}
	
	public void addOperationSignatureToPrimaryInterface(String name, String id) {
		InterfaceMapping mapping = getPrimaryInterface();
		if(mapping != null)
			mapping.addOperationSignature(name, id);
	}
	
	public Set<Asset> getEditorAssets(){
		return editorAssets;
	}
	
	private InterfaceMapping getPrimaryInterface() {
		for(InterfaceMapping mapping: providedBBMInterfaces) {
			if(mapping.getId().equals(primaryInterfaceId)){
				return mapping;
			}
		}
		return null;
	}
	
	public void addEditorAsset(Asset asset) {
		editorAssets.add(asset);
	}
	
	public void addAssetToPrimaryInterface(Asset asset) {
		InterfaceMapping mapping = getPrimaryInterface();
		if(mapping != null)
			mapping.addAsset(asset);
				
	}
	
	public void addAssetToOperationSignatureOfPrimaryInterface(Asset asset, String id) {
		InterfaceMapping mapping = getPrimaryInterface();
		if(mapping != null)
			mapping.addAssetToOperationSignature(asset, id);
	}
	
	public String getFirstPrimaryInterfaceSignatureId() {
		InterfaceMapping primaryInterface = getPrimaryInterface();
		for(String id : primaryInterface.getOperationSignatures().keySet()) {
			return id;
		}
		return null;
	}
}
