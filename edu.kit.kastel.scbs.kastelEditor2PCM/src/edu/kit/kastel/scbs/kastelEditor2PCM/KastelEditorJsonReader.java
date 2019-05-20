package edu.kit.kastel.scbs.kastelEditor2PCM;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.BlackBoxMechanism;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.HardGoal;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Component;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.SoftGoal;


public class KastelEditorJsonReader {
	
	private Map<String, Component> services;
	private Map<String, BlackBoxMechanism> blackBoxMechanisms;
	private Collection<String> functionalRequirements;
	private Collection<String> assets;
	private Collection<SoftGoal> softGoals;
	private Map<String, HardGoal> hardGoals;

	private Multimap<Component, String> serviceFuReqRelationships;
	private Multimap<HardGoal, BlackBoxMechanism> hardMechanismRelationship;
	private Multimap<Component, BlackBoxMechanism> serviceBBMMap;
	

	public boolean extractKastelEditorModelFromJson(File file) {
		String jsonString = IOUtility.readFromFile(file);
		
		if(jsonString == "") {
			return false;
		}
				return startKASTELJsonParsing(jsonString);
	}
	
	public boolean save(File f) {
		
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
		String s = gson.toJson(getServicesAsCollection());
		return IOUtility.writeToFile( json, s);
	}
	
	private boolean startKASTELJsonParsing(String jsonString) {

	
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
	
	
	public Collection<BlackBoxMechanism> getBlackBoxMechanismsAsList() {
		return blackBoxMechanisms.values();
	}
	
	public Map<String, BlackBoxMechanism> getBlackBoxMechanismsMap(){
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


	public Multimap<Component, BlackBoxMechanism> getServiceBBMMap() {
		return serviceBBMMap;
	}


	private void extractBaseEntities(JsonObject rootElement) {
		services = generateServiceObjectsFromJson(rootElement.get("Services"));
		functionalRequirements = extractStringCollection(rootElement.get("Functional Requirements"));
		assets = extractStringCollection(rootElement.get("Assets"));
		blackBoxMechanisms = extractBlackBoxMechanisms(rootElement.get("Black Box Mechanisms"));
	}
	
	private void extractRelationships(JsonObject rootElement) {
		serviceFuReqRelationships = extractServiceFunctionalReqSets(rootElement.get("Functional Requirements and Services Relationships"));
		softGoals = extractSoftGoals(rootElement.get("Soft Goals"));
		hardGoals = extractHardGoals(rootElement.get("Hard Goals"));
		hardMechanismRelationship = extractHardGoalBBMRelationship(rootElement.get("Hard Mechanism Relationship"));
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

	
private Map<String, Component> generateServiceObjectsFromJson(JsonElement serviceArrayJsonElement){
		
		HashMap<String, Component> services = new HashMap<String, Component>(); 
		Collection<String> serviceNames = extractStringCollection(serviceArrayJsonElement);
		
		for (String serviceName : serviceNames) {
			services.put(serviceName, new Component(serviceName));
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
	
	
	private Multimap<Component, String> extractServiceFunctionalReqSets(JsonElement relationElement){
		Multimap<Component, String> serviceFuReqRelationships = ListMultimapBuilder.hashKeys().linkedListValues().build();
		
		Multimap<String, String> relationships = extractRelationshipSets(relationElement);
		
		for(String functionalRequirement : relationships.keySet()) {
			for(String serviceName : relationships.get(functionalRequirement)) {
						serviceFuReqRelationships.put(services.get(serviceName), functionalRequirement);
						break;
				}
			}
		
		return serviceFuReqRelationships;
	}
	
	
	
	private Map<String, BlackBoxMechanism> extractBlackBoxMechanisms(JsonElement blackBoxMechanismsElement){
		
		HashMap<String, BlackBoxMechanism> blackBoxMechanisms = new HashMap<String, BlackBoxMechanism>();
		
		if(blackBoxMechanismsElement.isJsonObject()) {
			JsonObject blackBoxMechanismsObject = blackBoxMechanismsElement.getAsJsonObject();
			
			for(Entry<String,JsonElement> entry : blackBoxMechanismsObject.entrySet()) {
				JsonObject blackBoxMechanismJsonObject = entry.getValue().getAsJsonObject();
				
				String extraHg = checkNullableJsonString(blackBoxMechanismJsonObject, "extra_hg");
				
				BlackBoxMechanism mechanism = new BlackBoxMechanism(entry.getKey(), blackBoxMechanismJsonObject.get("authenticity").getAsBoolean(), 
						blackBoxMechanismJsonObject.get("confidentiality").getAsBoolean(), 
						blackBoxMechanismJsonObject.get("integrity").getAsBoolean(), 
						extraHg);
				
				blackBoxMechanisms.put(mechanism.getName(), mechanism);
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
				
				softGoals.add(new SoftGoal(entry.getKey(), 
						cbValue,
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
				String hgFunctionalRequirement = "";
				SoftGoal hgSg = null;
				
				for(Component service : getServicesAsCollection()) {
					if(entry.getKey().contains(service.getName())) {
						hgService = service;
						break;
					} 
				}
				
				for(String functionalRequirement : functionalRequirements) {
					if(entry.getKey().contains(functionalRequirement)) {
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
				
				if(hgService == null || hgFunctionalRequirement == "" || hgSg == null) {
					System.out.println("Error, Hard Goal could not be recreated");
					continue;
				}
				
				String extraHg = checkNullableJsonString(entry.getValue().getAsJsonObject(), "extra_hg");
				String extraHgUsed = checkNullableJsonString(entry.getValue().getAsJsonObject(), "extra_hg_used");
				String originalHg = checkNullableJsonString(entry.getValue().getAsJsonObject(), "original_hg");
				HardGoal hg = new HardGoal(entry.getKey(), hgService.getName(), hgSg, hgFunctionalRequirement, 
						extraHg,
						extraHgUsed,
						originalHg);
				
		
				hardGoals.put(hg.getName(), hg);
			}
		}
		return hardGoals;
	}

private Multimap<HardGoal, BlackBoxMechanism> extractHardGoalBBMRelationship(JsonElement hardMechanismRelationshipRootElement){
	
	Multimap<HardGoal, BlackBoxMechanism> hardMechanismRelationship = ListMultimapBuilder.hashKeys().linkedListValues().build();
	serviceBBMMap = ListMultimapBuilder.hashKeys().linkedListValues().build();
	
	if(hardMechanismRelationshipRootElement.isJsonObject()) {
		JsonObject hardMechanismRelationshipRootObject = hardMechanismRelationshipRootElement.getAsJsonObject();
		
		
		Collection<Entry<String, JsonElement>> relationshipEntries = hardMechanismRelationshipRootObject.entrySet();
		
		for(Entry<String,JsonElement> entry : relationshipEntries) {
	
			HardGoal hardGoal = hardGoals.get(entry.getKey());
			BlackBoxMechanism blackBoxMechanism = blackBoxMechanisms.get(entry.getValue().getAsString());
			
			if(hardGoal == null || blackBoxMechanism == null) {
				System.out.println("HardGoal BBM Relationship not found");
				continue;
			}
			
			Component hgRelatedService = services.get(hardGoal.getServiceName());
			
			if(hgRelatedService != null && !serviceBBMMap.containsEntry(hgRelatedService, blackBoxMechanism)) {
				serviceBBMMap.put(hgRelatedService, blackBoxMechanism);
			}
			
			hardMechanismRelationship.put(hardGoal, blackBoxMechanism);
		}
	}
	return hardMechanismRelationship; 
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
	
	public Multimap<Component, String> getServiceFuReqRelationships() {
		return serviceFuReqRelationships;
	}

	public Multimap<HardGoal, BlackBoxMechanism> getHardMechanismRelationship() {
		return hardMechanismRelationship;
	}

	public Collection<Component> getServicesAsCollection() {
		return services.values();
	}
	
	public Map<String, Component> getServicesAsMap(){
		return services;
	}

	public Collection<String> getFunctionalRequirements() {
		return functionalRequirements;
	}
}
