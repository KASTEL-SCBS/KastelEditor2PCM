package edu.kit.kastel.scbs.kastelEditor2PCM;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Actor;

public class KASTELGoalModelReader_ActorAttackerExtension extends KASTELGoalModelReader {
	
	Set<String> actorRoles;
	Set<Actor> actors;
	
	public KASTELGoalModelReader_ActorAttackerExtension() {
		actorRoles = new HashSet<String>();
		actors = new HashSet<Actor>();
	}
	
	@Override
	protected void readExtensionModelInformation(String goalModelStringRepresentation) {
		JsonParser parser = new JsonParser();
		JsonElement rootElement = parser.parse(goalModelStringRepresentation);
		
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
	
	public Set<Actor> getActors() {
		return actors;
	}
	
}
