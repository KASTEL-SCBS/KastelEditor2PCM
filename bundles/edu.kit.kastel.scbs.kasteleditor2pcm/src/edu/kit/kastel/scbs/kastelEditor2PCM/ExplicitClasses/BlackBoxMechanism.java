package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class BlackBoxMechanism extends EditorElement {

	@Expose private String primaryInterfaceId;
	@Expose private Set<InterfaceMapping> providedBBMInterfaces;
	
	public BlackBoxMechanism(String name) {
		super.setName(name);
		primaryInterfaceId = "";
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

	public void addPcmOperationSignatureIdForTargetAssetToPrimaryInterface(String pcmOperationSignatureId, Asset targetAsset) {
		
		InterfaceMapping mapping = getPrimaryInterface();
		
		if(mapping != null)
			mapping.addPcmOperationSignatureIdForTargetAsset(pcmOperationSignatureId, targetAsset);
	}
	
	public String getPcmOperationSignatureIdForTargetAsset(Asset targetAsset) {
		InterfaceMapping mapping = getPrimaryInterface();
		if(mapping != null)
			return mapping.getPcmOperationSignatureIdForTargetAsset(targetAsset);
		
		return null;
	}
	
	public String getPrimaryInterfaceId() {
		return primaryInterfaceId;
	}
	
	public Set<Asset> getTargetAssets(){
		InterfaceMapping mapping = getPrimaryInterface();
		if(mapping != null)
			return mapping.getAssets();
		
		return null;
	}
	
	private InterfaceMapping getPrimaryInterface() {
		for(InterfaceMapping mapping: providedBBMInterfaces) {
			if(mapping.getId().equals(primaryInterfaceId)){
				return mapping;
			}
		}
		return null;
	}
	
	public void addAssetToPrimaryInterface(Asset asset) {
		InterfaceMapping mapping = getPrimaryInterface();
		if(mapping != null)
			mapping.addAsset(asset);
				
	}
	
}
