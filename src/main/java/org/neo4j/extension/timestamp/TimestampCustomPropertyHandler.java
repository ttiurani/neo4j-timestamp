package org.neo4j.extension.timestamp;

import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.RelationshipType;

public class TimestampCustomPropertyHandler {
  	private String customPropertyName;
	private List<RelationshipType> modifiedRelationshipTypes;
	private Direction direction;
	
	public TimestampCustomPropertyHandler(String customPropertyName,
			List<RelationshipType> modifiedRelationshipTypes, Direction direction) {
		this.customPropertyName = customPropertyName;
		this.modifiedRelationshipTypes = modifiedRelationshipTypes;
		this.direction = direction;
	}
  	public String getCustomPropertyName() {
		return customPropertyName;
	}
	public List<RelationshipType> getModifiedRelationshipTypes() {
		return modifiedRelationshipTypes;
	}
	public Direction getDirection() {
		return direction;
	}
}
