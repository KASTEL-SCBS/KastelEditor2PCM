package edu.kit.kastel.scbs.kastelEditor2PCM.extensions.ActorAttacker;

import java.util.Set;

import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Actor;

public class ActorAttackerExtensionTrackingElement {
	Set<String> actorRoles;
	Set<Actor> actors;
	
	public ActorAttackerExtensionTrackingElement(Set<String> actorRoles, Set<Actor> actors) {
		this.actorRoles = actorRoles;
		this.actors = actors;
	}
	
}
