package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class BlackBoxMechanism extends EditorElement {

	@Expose(serialize = false, deserialize = false) 
	private final boolean authenticity;
	@Expose(serialize = false, deserialize = false) 
	private final boolean confidentiality;
	@Expose(serialize = false, deserialize = false) 
	private final boolean integrity;
	private String pcmInterfaceId;
	private Set<Asset> targetAssets;
	private Map<Asset, String> assetToPCMOperationSignatureIdMapping;
	
	public BlackBoxMechanism(String name, boolean authenticity, boolean confidentiality, boolean integrity) {
		super.setName(name);
		this.authenticity = authenticity;
		this.confidentiality = confidentiality;
		this.integrity = integrity;
		targetAssets = new HashSet<Asset>();
		assetToPCMOperationSignatureIdMapping = new HashMap<Asset, String>();
	}

	public boolean providesAuthenticity() {
		return authenticity;
	}

	public boolean providesConfidentiality() {
		return confidentiality;
	}

	public boolean providesIntegrity() {
		return integrity;
	}

	public String getBbmComponentId() {
		return super.getId();
	}

	public void setBbmComponentId(String bbmComponentId) {
		super.setId(bbmComponentId);
	}

	public String getPcmInterfaceId() {
		return pcmInterfaceId;
	}

	public void setPcmInterfaceId(String pcmInterfaceId) {
		this.pcmInterfaceId = pcmInterfaceId;
	}

	public boolean addTargetAsset(Asset asset) {
		return targetAssets.add(asset);
	}
	
	public void addPcmOperationSignatureIdForTargetAsset(String pcmOperationSignatureId, Asset targetAsset) {
		if(targetAssets.contains(targetAsset)) {
			assetToPCMOperationSignatureIdMapping.put(targetAsset, pcmOperationSignatureId);
		}
	}
	
	public String getPcmOperationSignatureIdForTargetAsset(Asset targetAsset) {
		if(targetAssets.contains(targetAsset)) {
			return assetToPCMOperationSignatureIdMapping.get(targetAsset);
		}
		return null;
	}
	
	public Set<Asset> getTargetAssets(){
		return targetAssets;
	}
	
	
}
