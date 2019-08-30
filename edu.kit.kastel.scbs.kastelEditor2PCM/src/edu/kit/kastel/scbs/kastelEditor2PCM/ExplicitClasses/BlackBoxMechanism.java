package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlackBoxMechanism extends EditorElement {

	private final boolean authenticity;
	private final boolean confidentiality;
	private final boolean integrity;
	private final String extraHg;
	private String pcmInterfaceId;
	private Set<Asset> targetAssets;
	private Map<Asset, String> assetToPCMOperationSignatureIdMapping;
	
	public BlackBoxMechanism(String name, boolean authenticity, boolean confidentiality, boolean integrity, String extraHg) {
		super.setName(name);
		this.authenticity = authenticity;
		this.confidentiality = confidentiality;
		this.integrity = integrity;
		this.extraHg = extraHg;
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

	public String getExtraHg() {
		return extraHg;
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
