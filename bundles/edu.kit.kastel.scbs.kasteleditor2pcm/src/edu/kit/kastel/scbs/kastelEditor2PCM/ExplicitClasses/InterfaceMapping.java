package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.annotations.Expose;


public class InterfaceMapping extends EditorElement {
	
	@Expose public Set<Asset> assets = new HashSet<Asset>();
	@Expose public Map<String, String> operationSignatures = new HashMap<String, String>();
	@Expose public Map<String, Collection<Asset>> assetsInOperationSignatures = new HashMap<String, Collection<Asset>>();
	
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
	
	public void addAsset(Asset asset) {
		assets.add(asset);
	}
	
	public Set<Asset> getAssets(){
		return assets;
	}

	public Collection<String> getOperationSignaturePCMIdsForAsset(Asset asset) {
		Collection<String> signatures = new ArrayList<String>();
		for(Entry<String, Collection<Asset>> entry : assetsInOperationSignatures.entrySet()) {
			if(entry.getValue().contains(asset)) {
				signatures.add(entry.getKey());
			}
		}
		
		return signatures;
	}
	
	public Collection<Asset> getAssetsForOperationSignature(String id){
		return assetsInOperationSignatures.get(id);
	}

	public void addAssetToOperationSignature(Asset asset, String operationSignaturePCMId) {
		if(assets.contains(asset)) {
			assetsInOperationSignatures.get(operationSignaturePCMId).add(asset);
		}
	}
	
	public Map<String, String> getOperationSignatures(){
		return operationSignatures;
	}

	public void addOperationSignature(String name, String id) {
		operationSignatures.put(id, name);
		assetsInOperationSignatures.put(id, new ArrayList<Asset>());
	}

	public boolean operationSignatureAvailable(String id) {
		return operationSignatures.containsKey(id);
	}
}
