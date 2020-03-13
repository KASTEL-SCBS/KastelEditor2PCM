package edu.kit.kastel.scbs.kastelEditor2PCM;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.kit.kastel.scbs.kastelEditor2PCM.GoalModelToPCMElementTransformator.UpperOrLower;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Asset;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.BlackBoxMechanism;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.HardGoal;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.InterfaceMapping;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.ServiceComponent;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.SoftGoal;
import edu.kit.kastel.scbs.kastelEditor2PCM.Util.IOUtil;
import edu.kit.kastel.scbs.kastelEditor2PCM.Util.StringUtil;


public class KASTELGoalModelReader {
	
	private Set<ServiceComponent> services;
	private Set<BlackBoxMechanism> blackBoxMechanisms;
	private Set<InterfaceMapping> functionalRequirements;
	private Set<Asset> assets;
	private Collection<SoftGoal> softGoals;
	private Map<String, HardGoal> hardGoals;
	private String modelName;
	protected String goalModelStringRepresentation;

	public boolean extractKastelEditorModelFromJson(File file) {
		goalModelStringRepresentation = IOUtil.readFromFile(file);
		
		if(goalModelStringRepresentation.isEmpty()) {
			return false ;
		}
		readBaseGoalModel(goalModelStringRepresentation);
		postProcessFunctionalRequirementsAndComponents();
		
		return true;
		
	}
	
	private boolean readBaseGoalModel(String jsonString) {

	
		JsonParser parser = new JsonParser();
		JsonElement rootElement = parser.parse(jsonString);
		
		if(rootElement.isJsonObject()) {
			
			JsonObject root = rootElement.getAsJsonObject();
			extractGoalModelName(root);
			extractBaseEntities(root);
			extractRelationships(root);
			
		} else {
			System.out.println("Malformed Json String at Root Object");
			return false;
		}
		
	
		
		return true;
	}
	
	public Set<BlackBoxMechanism> getBlackBoxMechanisms(){
		return blackBoxMechanisms;
	}


	public Collection<SoftGoal> getSoftGoals() {
		return softGoals;
	}


	public Collection<HardGoal> getHardGoalsAsCollection() {
		return hardGoals.values();
	}
	
	public Map<String, HardGoal> getHardGoalsAsMap(){
		return hardGoals;
	}


	private void extractBaseEntities(JsonObject rootElement) {
		services = generateServiceObjectsFromJson(rootElement.get("Services"));
		functionalRequirements = extractFunctionalRequirements(rootElement.get("Functional Requirements"));
		assets = extractAssets(rootElement.get("Assets"));
		blackBoxMechanisms = extractBlackBoxMechanisms(rootElement.get("Black Box Mechanisms"));
	}
	
	private void extractRelationships(JsonObject rootElement) {
		appendFunctionalRequirementsToServices(rootElement.get("Functional Requirements and Services Relationships"));
		softGoals = extractSoftGoals(rootElement.get("Soft Goals"));
		hardGoals = extractHardGoals(rootElement.get("Hard Goals"));
		appendBBMsToHardgoals(rootElement.get("Hard Mechanism Relationship"));
	}
	
	private void extractGoalModelName(JsonObject rootElement) {
		modelName = rootElement.get("Project").getAsString();
	}
	
	protected Collection<String> extractStringCollection(JsonElement stringArrayElement){
		
		Collection<String> stringCollection = new ArrayList<String>();
		JsonArray stringArray;
		if(stringArrayElement.isJsonArray()) {
			stringArray = stringArrayElement.getAsJsonArray();
			
			for(JsonElement arrayEntry : stringArray) {
				if(arrayEntry.isJsonPrimitive()) {
					stringCollection.add(arrayEntry.getAsString());
				}
			}
		}
		
		return stringCollection;	
	}
	
	private Set<InterfaceMapping> extractFunctionalRequirements(JsonElement functionalRequirementsStringArrayElement){
		
		Set<InterfaceMapping> functionalRequirements = new HashSet<InterfaceMapping>();
		
		for(String functionalRequirementName : extractStringCollection(functionalRequirementsStringArrayElement)) {
			functionalRequirements.add(new InterfaceMapping(functionalRequirementName));
		}
		
		return functionalRequirements;
		
	}
	
	private Set<Asset> extractAssets(JsonElement assetStringArrayElement){
		HashSet<Asset> assets = new HashSet<Asset>();
		
		for(String assetName : extractStringCollection(assetStringArrayElement)) {
			assets.add(new Asset(assetName));
		}
		return assets;
	}
	
private HashSet<ServiceComponent> generateServiceObjectsFromJson(JsonElement serviceArrayJsonElement){
		
		HashSet<ServiceComponent> services = new HashSet<ServiceComponent>(); 
		Collection<String> serviceNames = extractStringCollection(serviceArrayJsonElement);
		
		for (String serviceName : serviceNames) {
			services.add(new ServiceComponent(serviceName));
			
		}
		
		return services;
	}
	
	private Multimap<String,String> extractRelationshipSets(JsonElement relationElement) {
		
		Multimap<String, String> relationships = ListMultimapBuilder.hashKeys().linkedListValues().build();

		if(relationElement.isJsonObject()) {
			JsonObject relationObject = relationElement.getAsJsonObject();
			
			for (String functionalRequirement : relationObject.keySet()) {
				JsonElement relatedServices = relationObject.get(functionalRequirement);
				
				if(relatedServices.isJsonArray()) {
					Collection<String> serviceNames = extractStringCollection(relatedServices.getAsJsonArray());
					
					for(String serviceName : serviceNames) {
						relationships.put(functionalRequirement, serviceName);
					}
				}
			}
		}
		return relationships;
	}
	
	private void appendFunctionalRequirementsToServices(JsonElement relationElement){
		Multimap<String, String> relationships = extractRelationshipSets(relationElement);
		
		for(String functionalRequirement : relationships.keySet()) {
			InterfaceMapping functionalRequirementObject = getFunctionalRequirementByName(functionalRequirement);
			for(String serviceName : relationships.get(functionalRequirement)) {
						getServiceByName(serviceName).addFunctionalRequirement(functionalRequirementObject);				
				}
			}
	}

	
	
	private Set<BlackBoxMechanism> extractBlackBoxMechanisms(JsonElement blackBoxMechanismsElement){
		
		Set<BlackBoxMechanism> blackBoxMechanisms = new HashSet<BlackBoxMechanism>();
		
		if(blackBoxMechanismsElement.isJsonObject()) {
			JsonObject blackBoxMechanismsObject = blackBoxMechanismsElement.getAsJsonObject();
			
			for(Entry<String,JsonElement> entry : blackBoxMechanismsObject.entrySet()) {
				JsonObject blackBoxMechanismJsonObject = entry.getValue().getAsJsonObject();
				//JsonObject baseBlackBoxMechanismInformation = blackBoxMechanismJsonObject.getAsJsonObject("base");
				
				
				BlackBoxMechanism mechanism = new BlackBoxMechanism(entry.getKey());
				
				blackBoxMechanisms.add(mechanism);
			}
		}
		return blackBoxMechanisms;
	}
	
	private Collection<SoftGoal> extractSoftGoals(JsonElement softGoalRoot){
		
		Collection<SoftGoal> softGoals = new ArrayList<SoftGoal>();
		
		if(softGoalRoot.isJsonObject()) {
			JsonObject softGoalRootObject = softGoalRoot.getAsJsonObject();
			
			for(Entry<String, JsonElement> entry : softGoalRootObject.entrySet()) {
				String cbValue = checkNullableJsonString(entry.getValue().getAsJsonObject(), "cb_value");
				Asset asset = getAssetByCBValue(cbValue);
				
				assets.add(asset);
				if(entry.getValue().getAsJsonObject().get("priority").isJsonNull()) {
					softGoals.add(new SoftGoal(entry.getKey(), 
							extractConcern(cbValue),
							asset));
				} else {
					Boolean priority = entry.getValue().getAsJsonObject().get("priority").getAsBoolean();
					softGoals.add(new SoftGoal(entry.getKey(), 
							extractConcern(cbValue),
							asset, priority));
				}
				
				
			}
		}
		return softGoals;
	}
	
	private Map<String, HardGoal> extractHardGoals(JsonElement hardGoalRoot){
		
		HashMap<String, HardGoal> hardGoals = new HashMap<String, HardGoal>();
		
		if(hardGoalRoot.isJsonObject()) {
			JsonObject hardGoalsRootObject = hardGoalRoot.getAsJsonObject();
			
			for(Entry<String,JsonElement> entry : hardGoalsRootObject.entrySet()) {
				
				if(!entry.getValue().isJsonObject()) {
					break;
				} 
				
				JsonObject hardGoalJson = entry.getValue().getAsJsonObject();
				
				
				
				
				
			
			
				
				
				String hgJsonServiceEntry = hardGoalJson.get(HardGoal.COMPONENT_ID).getAsString();
				ServiceComponent hgService = getServiceByName(hgJsonServiceEntry);
				
				
				String hgJsonFunctionalRequirementEntry = hardGoalJson.get(HardGoal.FUNCTIONAL_REQUIREMENT_ID).getAsString();
				InterfaceMapping hgFunctionalRequirement = getFunctionalRequirementByName(hgJsonFunctionalRequirementEntry);
				
				String hgJsonSoftGoalEntry = hardGoalJson.get(HardGoal.SOFTGOAL_ID).getAsString();
				SoftGoal hgSg = getSoftGoalByName(hgJsonSoftGoalEntry);
			
				String uId = hardGoalJson.get(HardGoal.UNIQUE_ID).getAsString();
				
				if(hgService == null || hgFunctionalRequirement == null || hgSg == null || uId.isEmpty()) {
					System.out.println("Error, Hard Goal could not be recreated");
					continue;
				}
				
			
			
				
				HardGoal hg = new HardGoal(entry.getKey(), hgService.getName(), hgSg, hgFunctionalRequirement, uId);
				hgFunctionalRequirement.addAsset(hgSg.getAsset());
				
				hgService.getHardGoals().add(hg);
				hgService.getProvidedFunctionalRequirements().add(hgFunctionalRequirement);
		
				hardGoals.put(hg.getName(), hg);
			}
		}
		return hardGoals;
	}

private void appendBBMsToHardgoals(JsonElement hardMechanismRelationshipRootElement){
		
	if(hardMechanismRelationshipRootElement.isJsonObject()) {
		JsonObject hardMechanismRelationshipRootObject = hardMechanismRelationshipRootElement.getAsJsonObject();
		
		
		Collection<Entry<String, JsonElement>> relationshipEntries = hardMechanismRelationshipRootObject.entrySet();
		
		for(Entry<String,JsonElement> entry : relationshipEntries) {
	
			HardGoal hardGoal = hardGoals.get(entry.getKey());
			BlackBoxMechanism blackBoxMechanism = getBlackBoxMechanism(entry.getValue().getAsString());
			
			blackBoxMechanism.addAssetToPrimaryInterface(hardGoal.getSoftGoal().getAsset());
			
			if(hardGoal == null || blackBoxMechanism == null) {
				System.out.println("HardGoal BBM Relationship not found");
				continue;
			}
			
			ServiceComponent hgRelatedService = getServiceByName(hardGoal.getServiceName());
			
			if(hgRelatedService != null) {
				hgRelatedService.getBlackBoxMechanisms().add(blackBoxMechanism);
			}
			
			hardGoal.setBlackBoxMechansims(blackBoxMechanism);
		
		}
	}
	
}
	
	private String checkNullableJsonString(JsonObject element, String key) {
		String string;
		
		if(element.get(key).isJsonNull()) {
			string = null;
		} else {
			string = element.get(key).getAsString();
		}
		
		return string;
	}
	
	public Set<ServiceComponent> getServices() {
		return services;
	}
	
	
	public Collection<InterfaceMapping> getFunctionalRequirements() {
		return functionalRequirements;
	}
	
	public String[] splitCbValue(String cbValue) {
		return cbValue.split("\u00a1");
	}
	public Asset getAssetByCBValue(String cbValue) {
		String assetName = splitCbValue(cbValue)[1];
		
		for(Asset asset : assets) {
			if(asset.getName().equals(StringUtil.removeCharAndStringSymbols(assetName))) {
				return asset;
			}
		}
		
		return null;
	}
	
	public String extractConcern(String cbValue) {
		return splitCbValue(cbValue)[0];
	}
	
	public BlackBoxMechanism getBlackBoxMechanism(String bbmName) {
		for(BlackBoxMechanism bbm : getBlackBoxMechanisms()) {
			if(bbm.getName().equals(bbmName)) {
				return bbm;
			}
		}
		return null;
	}
	
	private void postProcessFunctionalRequirementsAndComponents() {
		
		splitFunctionalRequirementsForComponentsWhenNecessary();
		postProcessExtensions();
			
	}
	
	protected void postProcessExtensions() {
		//Noting to do here due to basic reader
	}

	private void splitFunctionalRequirementsForComponentsWhenNecessary() {
		Collection<ServiceComponent> visitedServices = new ArrayList<ServiceComponent>();
		for(ServiceComponent component1 : services) {
			
			for( ServiceComponent component2 : services ) {
				if(!component1.equals(component2) && !visitedServices.contains(component1) && !visitedServices.contains(component2)) {
				modifySimilarButDifferentFunctionalRequirementsInComponent(component1, component2);
				}
			}
			
			visitedServices.add(component1);
		}
	}

	public void modifySimilarButDifferentFunctionalRequirementsInComponent(ServiceComponent component1, ServiceComponent component2) {
		
		HashSet<InterfaceMapping> fuReqsOfComponent1 = new HashSet<InterfaceMapping>();
		HashSet<InterfaceMapping> fuReqsOfComponent2 = new HashSet<InterfaceMapping>();
		for(InterfaceMapping fuReq1 : component1.getProvidedFunctionalRequirements()) {
			for(InterfaceMapping fuReq2 : component2.getProvidedFunctionalRequirements()) {
				if(fuReq1.equals(fuReq2)) {
					InterfaceMapping requirement = fuReq1;
					Collection<Asset> fuReq1ReallyUsedAssets = getAssetsReallyUsedInFunctionalRequirementForComponent(component1, requirement);
					Collection<Asset> fuReq2ReallyUsedAssets = getAssetsReallyUsedInFunctionalRequirementForComponent(component2, requirement);
					
					boolean fuReq1UsedAssetsComplete = fuReq1.assets.size() == fuReq1ReallyUsedAssets.size();
					boolean fuReq2UsedAssetsComplete = fuReq2.assets.size() == fuReq2ReallyUsedAssets.size();
					
					if(fuReq1UsedAssetsComplete && fuReq2UsedAssetsComplete) {
						fuReqsOfComponent1.add(fuReq1);
						fuReqsOfComponent2.add(fuReq2);
						break;
					}
					
					
					if(assetsEqual(fuReq1ReallyUsedAssets, fuReq2ReallyUsedAssets)) {
						InterfaceMapping fuReq = new InterfaceMapping(requirement.getName());
						fillFunctionalRequirementAssets(fuReq,fuReq1ReallyUsedAssets);
						fuReqsOfComponent1.add(fuReq);
						fuReqsOfComponent2.add(fuReq);
						
						functionalRequirements.add(fuReq);
						break;
					} else {
						InterfaceMapping fuReqForC1 = new InterfaceMapping(requirement.getName()+"_"+component1.getName());
						fillFunctionalRequirementAssets(fuReqForC1, fuReq1ReallyUsedAssets);
						
						InterfaceMapping fuReqForC2 = new InterfaceMapping(requirement.getName()+"_"+component2.getName());
						fillFunctionalRequirementAssets(fuReqForC2, fuReq2ReallyUsedAssets);
						if(fuReq1UsedAssetsComplete) {
							fuReqForC1 = requirement;
						} else if (fuReq2UsedAssetsComplete) {
							fuReqForC2 = requirement;
						} 
						fuReqsOfComponent1.add(fuReqForC1);
						fuReqsOfComponent2.add(fuReqForC2);
						
						functionalRequirements.add(fuReqForC1);
						functionalRequirements.add(fuReqForC2);
					}
				}
			}
		}
		
		component1.exchangeFunctionalRequirements(fuReqsOfComponent1);
		component2.exchangeFunctionalRequirements(fuReqsOfComponent2);
	}
	
	public Set<Asset> getAssetsReallyUsedInFunctionalRequirementForComponent(ServiceComponent component, InterfaceMapping fuReq) {
		Set<Asset> assets = new HashSet<Asset>();
		for(Asset asset : fuReq.assets) {
			for(HardGoal hg : component.getHardGoals()) {
				if(hg.getSoftGoal().getAsset().equals(asset)) {
					assets.add(asset);
				}
			}
		}
		
		return assets;
	}
	
	public boolean assetsEqual(Collection<Asset> assets1, Collection<Asset> assets2) {
		boolean assetFound = false;
		if(assets1.size() != assets2.size()) {
			return false;
		}
		
		for(Asset asset1 : assets1) {
			for(Asset asset2 : assets2) {
				if(asset1.equals(asset2)) {
					assetFound = true;
				}
			}
			if(!assetFound){
				return false;
			}
			assetFound = false;
		}
		
		return true;
	}
	
	public void fillFunctionalRequirementAssets(InterfaceMapping fuReq, Collection<Asset> assets) {
		for(Asset asset : assets) {
			fuReq.addAsset(asset);
		}
	}
	
	public InterfaceMapping getFunctionalRequirementByName(String searchedFunctionalRequirementName) {
		String modifiedSearched = StringUtil.removeCharAndStringSymbols(searchedFunctionalRequirementName);
		for(InterfaceMapping requirement : functionalRequirements) {
			if(requirement.getName().equals(modifiedSearched)) {
				return requirement;
			}
		}
		
		return null;
	}
	
	public String getModelName() {
		return modelName;
	}
	
	public ServiceComponent getServiceByName(String searchedServiceName) {
		String modifiedSearched = StringUtil.removeCharAndStringSymbols(searchedServiceName);
		
		for(ServiceComponent component : services) {
			if(component.getName().equals(modifiedSearched)) {
				return component;
			}
		}
		
		return null;
	}
	
	public SoftGoal getSoftGoalByName(String searchedSoftGoalName) {
		
		
		for(SoftGoal sg : softGoals){
			if(sg.getName().equals(searchedSoftGoalName)) {
				return sg;
			}
		}
		
		return null;
	}
}
