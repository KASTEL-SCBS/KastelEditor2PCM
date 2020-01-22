package edu.kit.kastel.scbs.kastelEditor2PCMedu.kit.kastel.scbs.kastelEditor2PCM.extensions.ActorAttacker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Actor;
import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.ExtensionInformationTrackingElement;
import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.ExtensionTracking;
import edu.kit.kastel.scbs.kastelEditor2PCMedu.kit.kastel.scbs.kastelEditor2PCM.extensions.GoalModelExtension;

public class ActorAttackerExtension extends GoalModelExtension{
	
	Set<String> actorRoles;
	Set<Actor> actors;
	
	
	public ActorAttackerExtension(String extensionName) {
		super(extensionName);
	}

	@Override
	protected void processContent(String goalModelContent) {
		JsonParser parser = new JsonParser();
		JsonElement rootElement = parser.parse(goalModelContent);
		
		if(rootElement.isJsonObject()) {
			JsonObject root = rootElement.getAsJsonObject();
			readActorRoles(root);
			readActors(root);
		} else {
			System.out.println("Malformed Json String at Root Object");
		}
	}
	
	private void readActorRoles(JsonObject root) {
		Collection<String> actorRoles = extractStringCollection(root.get("Actor Roles"));
		for(String role : actorRoles) {
			this.actorRoles.add(role);
		}
	}
	
	private void readActors(JsonObject root) {
		JsonObject actorsObject = root.getAsJsonObject("Actors");
		for(Entry<String, JsonElement> entry : actorsObject.entrySet()) {
			actors.add(new Actor(entry.getKey()));
		}
	}
	
	
	//TODO: Bad Style, copy of KASTELGoalModelReader, however, fix later
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
	
	public Set<Actor> getActors(){
		return actors;
	}

	
}
