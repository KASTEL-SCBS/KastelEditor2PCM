package edu.kit.kastel.scbs.kastelEditor2PCM;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Asset;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.BlackBoxMechanism;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.HardGoal;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Component;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.FunctionalRequirement;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.SoftGoal;
import edu.kit.kastel.scbs.kastelEditor2PCM.Util.IOUtil;
import edu.kit.kastel.scbs.kastelEditor2PCM.Util.StringUtil;


public class KASTELGoalModelReader {
	
	private Set<Component> services;
	private Set<BlackBoxMechanism> blackBoxMechanisms;
	private Set<FunctionalRequirement> functionalRequirements;
	private Set<Asset> assets;
	private Collection<SoftGoal> softGoals;
	private Map<String, HardGoal> hardGoals;

	public boolean extractKastelEditorModelFromJson(File file) {
		String goalModelStringRepresentation = IOUtil.readFromFile(file);
		
		if(goalModelStringRepresentation == "") {
			return false ;
		}
		return readGoalModel(goalModelStringRepresentation);
	}
	
	public boolean saveTrackingFile(File f) {
		
		File json;
		
		if(!f.getAbsolutePath().endsWith(".json")) {
			json = new File(f.getAbsolutePath() + ".json");
			try {
				json.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			json = f;
		}
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String s = gson.toJson(services);
		return IOUtil.writeToFile( json, s);
	}
	
	private boolean readGoalModel(String jsonString) {

	
		JsonParser parser = new JsonParser();
		JsonElement rootElement = parser.parse(jsonString);
		
		if(rootElement.isJsonObject()) {
			
			JsonObject root = rootElement.getAsJsonObject();
			
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


//	public Multimap<Component, BlackBoxMechanism> getServiceBBMMap() {
//		return serviceBBMMap;
//	}


	private void extractBaseEntities(JsonObject rootElement) {
		services = generateServiceObjectsFromJson(rootElement.get("Services"));
		functionalRequirements = extractFunctionalRequirements(rootElement.get("Functional Requirements"));
		assets = extractAssets(rootElement.get("Assets"));
		blackBoxMechanisms = extractBlackBoxMechanisms(rootElement.get("Black Box Mechanisms"));
	}
	
	private void extractRelationships(JsonObject rootElement) {
//		serviceFuReqRelationships = extractServiceFunctionalReqSets(rootElement.get("Functional Requirements and Services Relationships"));
		appendFunctionalRequirementsToServices(rootElement.get("Functional Requirements and Services Relationships"));
		softGoals = extractSoftGoals(rootElement.get("Soft Goals"));
		hardGoals = extractHardGoals(rootElement.get("Hard Goals"));
		//hardMechanismRelationship = extractHardGoalBBMRelationship(rootElement.get("Hard Mechanism Relationship"));
		appendBBMsToHardgoals(rootElement.get("Hard Mechanism Relationship"));
	}
	
	
	//Required because collection elements in the KASTEL-Editor Json are saved as Strings general 
	//Avoid redundand specifications.
	private Collection<String> extractStringCollection(JsonElement stringArrayElement){
		
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
	
	private Set<FunctionalRequirement> extractFunctionalRequirements(JsonElement functionalRequirementsStringArrayElement){
		
		Set<FunctionalRequirement> functionalRequirements = new HashSet<FunctionalRequirement>();
		
		for(String functionalRequirementName : extractStringCollection(functionalRequirementsStringArrayElement)) {
			functionalRequirements.add(new FunctionalRequirement(functionalRequirementName));
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
	
private HashSet<Component> generateServiceObjectsFromJson(JsonElement serviceArrayJsonElement){
		
		HashSet<Component> services = new HashSet<Component>(); 
		Collection<String> serviceNames = extractStringCollection(serviceArrayJsonElement);
		
		for (String serviceName : serviceNames) {
			services.add(new Component(serviceName));
			
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
	
	
	private Multimap<Component, FunctionalRequirement> extractServiceFunctionalReqSets(JsonElement relationElement){
		Multimap<Component, FunctionalRequirement> serviceFuReqRelationships = ListMultimapBuilder.hashKeys().linkedListValues().build();
		
		Multimap<String, String> relationships = extractRelationshipSets(relationElement);
		
		for(String functionalRequirement : relationships.keySet()) {
			FunctionalRequirement functionalRequirementObject = getFunctionalRequirement(functionalRequirement);
			for(String serviceName : relationships.get(functionalRequirement)) {
						getService(serviceName).addFunctionalRequirement(functionalRequirementObject);
//						serviceFuReqRelationships.put(getService(serviceName), functionalRequirementObject);
				}
			}
		
		return serviceFuReqRelationships;
	}
	
	private void appendFunctionalRequirementsToServices(JsonElement relationElement){
		Multimap<Component, FunctionalRequirement> serviceFuReqRelationships = ListMultimapBuilder.hashKeys().linkedListValues().build();
		
		Multimap<String, String> relationships = extractRelationshipSets(relationElement);
		
		for(String functionalRequirement : relationships.keySet()) {
			FunctionalRequirement functionalRequirementObject = getFunctionalRequirement(functionalRequirement);
			for(String serviceName : relationships.get(functionalRequirement)) {
						getService(serviceName).addFunctionalRequirement(functionalRequirementObject);				
				}
			}
	}
	
	private FunctionalRequirement getFunctionalRequirement(String requirementName) {		
		for(FunctionalRequirement requirement : functionalRequirements) {
			if(requirement.getName().equals(requirementName)) {
				return requirement;
			}
		}
		return null;
	}
	
	
	
	private Set<BlackBoxMechanism> extractBlackBoxMechanisms(JsonElement blackBoxMechanismsElement){
		
		Set<BlackBoxMechanism> blackBoxMechanisms = new HashSet<BlackBoxMechanism>();
		
		if(blackBoxMechanismsElement.isJsonObject()) {
			JsonObject blackBoxMechanismsObject = blackBoxMechanismsElement.getAsJsonObject();
			
			for(Entry<String,JsonElement> entry : blackBoxMechanismsObject.entrySet()) {
				JsonObject blackBoxMechanismJsonObject = entry.getValue().getAsJsonObject();
				
				String extraHg = checkNullableJsonString(blackBoxMechanismJsonObject, "extra_hg");
				
				BlackBoxMechanism mechanism = new BlackBoxMechanism(entry.getKey(), blackBoxMechanismJsonObject.get("authenticity").getAsBoolean(), 
						blackBoxMechanismJsonObject.get("confidentiality").getAsBoolean(), 
						blackBoxMechanismJsonObject.get("integrity").getAsBoolean(), 
						extraHg);
				
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
				
				softGoals.add(new SoftGoal(entry.getKey(), 
						extractConcern(cbValue),
						asset,
						entry.getValue().getAsJsonObject().get("priority").getAsBoolean()));
			}
		}
		return softGoals;
	}
	
	private Map<String, HardGoal> extractHardGoals(JsonElement hardGoalRoot){
		
		HashMap<String, HardGoal> hardGoals = new HashMap<String, HardGoal>();
		
		if(hardGoalRoot.isJsonObject()) {
			JsonObject hardGoalsRootObject = hardGoalRoot.getAsJsonObject();
			
			for(Entry<String,JsonElement> entry : hardGoalsRootObject.entrySet()) {
				
				Component hgService = null;
				FunctionalRequirement hgFunctionalRequirement = null;
				SoftGoal hgSg = null;
				
				for(Component service : services) {
					if(entry.getKey().contains(service.getName())) {
						hgService = service;
						break;
					} 
				}
				
				for(FunctionalRequirement functionalRequirement : functionalRequirements) {
					if(entry.getKey().contains(functionalRequirement.getName())) {
							hgFunctionalRequirement = functionalRequirement;
							break;
					}
				}
				
				for(SoftGoal sg : softGoals) {
					if(entry.getKey().contains(sg.getName())) {
						hgSg = sg;
						break;
					}
				}
				
				if(hgService == null || hgFunctionalRequirement == null || hgSg == null) {
					System.out.println("Error, Hard Goal could not be recreated");
					continue;
				}
				HardGoal hg = new HardGoal(entry.getKey(), hgService.getName(), hgSg, hgFunctionalRequirement);
				hgFunctionalRequirement.addAsset(hgSg.getAsset());
				
				hgService.getHardGoals().add(hg);
				hgService.getProvidedFunctionalRequirements().add(hgFunctionalRequirement);
		
				hardGoals.put(hg.getName(), hg);
			}
		}
		return hardGoals;
	}

//private Multimap<HardGoal, BlackBoxMechanism> extractHardGoalBBMRelationship(JsonElement hardMechanismRelationshipRootElement){
//	
//	Multimap<HardGoal, BlackBoxMechanism> hardMechanismRelationship = ListMultimapBuilder.hashKeys().linkedListValues().build();
////	serviceBBMMap = ListMultimapBuilder.hashKeys().linkedListValues().build();
//	
//	if(hardMechanismRelationshipRootElement.isJsonObject()) {
//		JsonObject hardMechanismRelationshipRootObject = hardMechanismRelationshipRootElement.getAsJsonObject();
//		
//		
//		Collection<Entry<String, JsonElement>> relationshipEntries = hardMechanismRelationshipRootObject.entrySet();
//		
//		for(Entry<String,JsonElement> entry : relationshipEntries) {
//	
//			HardGoal hardGoal = hardGoals.get(entry.getKey());
//			
//			BlackBoxMechanism blackBoxMechanism = getBlackBoxMechanism(entry.getValue().getAsString());
//			if(hardGoal == null || blackBoxMechanism == null) {
//				System.out.println("HardGoal BBM Relationship not found");
//				continue;
//			}
//			
//			Component hgRelatedService = getService(hardGoal.getServiceName());
//			
//			if(hgRelatedService != null) {
////				serviceBBMMap.put(hgRelatedService, blackBoxMechanism);
//				hgRelatedService.getBlackBoxMechanisms().add(blackBoxMechanism);
//			}
//			
//			hardGoal.addBlackBoxMechanism(blackBoxMechanism);
//			
//			hardMechanismRelationship.put(hardGoal, blackBoxMechanism);
//		}
//	}
//	return hardMechanismRelationship; 
//}

private void appendBBMsToHardgoals(JsonElement hardMechanismRelationshipRootElement){
	
	
//	serviceBBMMap = ListMultimapBuilder.hashKeys().linkedListValues().build();
	
	if(hardMechanismRelationshipRootElement.isJsonObject()) {
		JsonObject hardMechanismRelationshipRootObject = hardMechanismRelationshipRootElement.getAsJsonObject();
		
		
		Collection<Entry<String, JsonElement>> relationshipEntries = hardMechanismRelationshipRootObject.entrySet();
		
		for(Entry<String,JsonElement> entry : relationshipEntries) {
	
			HardGoal hardGoal = hardGoals.get(entry.getKey());
			BlackBoxMechanism blackBoxMechanism = getBlackBoxMechanism(entry.getValue().getAsString());
			
			if(hardGoal == null || blackBoxMechanism == null) {
				System.out.println("HardGoal BBM Relationship not found");
				continue;
			}
			
			Component hgRelatedService = getService(hardGoal.getServiceName());
			
			if(hgRelatedService != null) {
//				serviceBBMMap.put(hgRelatedService, blackBoxMechanism);
				hgRelatedService.getBlackBoxMechanisms().add(blackBoxMechanism);
			}
			
			hardGoal.addBlackBoxMechanism(blackBoxMechanism);
		
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
	
//	public Multimap<Component, FunctionalRequirement> getServiceFuReqRelationships() {
//		return serviceFuReqRelationships;
//	}

//	public Multimap<HardGoal, BlackBoxMechanism> getHardMechanismRelationship() {
//		return hardMechanismRelationship;
//	}

	public Set<Component> getServices() {
		return services;
	}
	
	public Component getService(String serviceName) {
		for(Component component : services) {
			if(component.getName().equals(serviceName))
				return component;
		}
		
		return null;
	}
	
	public Collection<FunctionalRequirement> getFunctionalRequirements() {
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
}
