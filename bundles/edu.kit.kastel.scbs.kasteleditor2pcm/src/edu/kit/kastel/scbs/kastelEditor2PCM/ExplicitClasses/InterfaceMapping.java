package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.annotations.Expose;

import edu.kit.kastel.scbs.kastelEditor2PCM.Util.NameIdPair;

public class InterfaceMapping extends EditorElement {
	
	@Expose public Set<Asset> assets = new HashSet<Asset>();
	@Expose public Set<NameIdPair> operationSignatures = new HashSet<>();
	@Expose public Map<Asset, String> operationSignatureAssetMap = new HashMap<Asset,String>(); 
	
	public InterfaceMapping(String name, String interfaceId) {
		super.setName(name);
		super.setId(interfaceId);
	}
	
	public InterfaceMapping(String name) {
		super.setName(name);
	}
		
	public String getName() {
		return super.getName();
	}
	
	public void setName(String name) {
		super.setName(name);
	}
	
	public String getId() {
		return super.getId();
	}
	
	public void setId(String id) {
		super.setId(id);
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

	public void addOperationSignaturePair(String name, String id) {
		for(NameIdPair pair : operationSignatures) {
			if(pair.getId().equals(id)) {
				return;
			}
		}
		
		operationSignatures.add(new NameIdPair(name, id));
	}
	

	public void addPcmOperationSignatureIdForTargetAsset(String pcmOperationSignatureId, Asset targetAsset) {
		if(assets.contains(targetAsset)) {
			operationSignatureAssetMap.put(targetAsset, pcmOperationSignatureId);
		}
	}
	
	public String getPcmOperationSignatureIdForTargetAsset(Asset targetAsset) {
		if(assets.contains(targetAsset)) {
			return operationSignatureAssetMap.get(targetAsset);
		}
		return null;
	}
}
