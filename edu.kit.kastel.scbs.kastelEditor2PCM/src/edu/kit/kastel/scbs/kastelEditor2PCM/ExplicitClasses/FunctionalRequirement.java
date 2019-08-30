package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FunctionalRequirement extends EditorElement {
	
	public Set<Asset> assets;
	public Map<Asset, String> operationSignatureAssetMap; 

	public FunctionalRequirement(String name) {
		super.setName(name);
		this.assets = new HashSet<Asset>();
		operationSignatureAssetMap = new HashMap<Asset,String>();
	}
	
	public String getEditorFunctionalRequirementName() {
		return super.getName();
	}
	
	public void addAsset(Asset asset) {
		assets.add(asset);
	}
	
	public Set<Asset> getAssets(){
		return assets;
	}

	public String getOperationSignaturePCMIdForAsset(Asset asset) {
		return operationSignatureAssetMap.get(asset);
	}

	public void includeOperationSignatureAndAssetMapping(Asset asset, String operationSignaturePCMId) {
		operationSignatureAssetMap.put(asset, operationSignaturePCMId);
	}
	
	public Map<Asset,String> getAssetOperationSignatureIdRelation(){
		return operationSignatureAssetMap;
	}
	
}
