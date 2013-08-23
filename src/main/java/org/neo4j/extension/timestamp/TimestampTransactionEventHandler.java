package org.neo4j.extension.timestamp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.event.PropertyEntry;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;

/**
 * a {@see TransactionEventHandler} that
 * <ul>
 * <li>generates and updates "modified" properties for each new node and
 * relationship</li>
 * </ul>
 */
public class TimestampTransactionEventHandler<T> implements
    TransactionEventHandler<T> {

  public static final String TIMESTAMP_PROPERTY_NAME = "modified";

  @Override
  public T beforeCommit(TransactionData data) throws Exception {
    long currentTime = System.currentTimeMillis();
    updateParentTimestampsFor(data.assignedRelationshipProperties(), currentTime);
    updateParentTimestampsFor(data.assignedNodeProperties(), currentTime);

    // With removed properties, don't update when node is being deleted
    updateRemoveTimestampsFor(data.removedRelationshipProperties(), data.deletedRelationships(), currentTime);
    updateRemoveTimestampsFor(data.removedNodeProperties(), data.deletedNodes(), currentTime);
    
    updateTimestampsFor(data.createdNodes(), currentTime);

    // For created relationships, update both start and end node, and relationship itself
    Iterable<Relationship> createdRelationships = data.createdRelationships();
    Set<PropertyContainer> updatedPropertyContainers = null;
    for (Relationship relationship : createdRelationships) {
      if (updatedPropertyContainers == null)
        updatedPropertyContainers = new HashSet<PropertyContainer>();
      updatedPropertyContainers.add(relationship.getEndNode());
      updatedPropertyContainers.add(relationship.getStartNode());
    }
    updateTimestampsFor(updatedPropertyContainers, currentTime);
    updateTimestampsFor(createdRelationships, currentTime);
    return null;
  }

  @Override
  public void afterCommit(TransactionData data, java.lang.Object state) {
  }

  @Override
  public void afterRollback(TransactionData data, java.lang.Object state) {
  }

  private void updateParentTimestampsFor(Iterable<? extends PropertyEntry<?>> propertyEntries, long currentTime) {
    if (propertyEntries == null) return;
    Set<PropertyContainer> updatedPropertyContainers = null;
    for (PropertyEntry<?> propertyEntry : propertyEntries) {
      if (updatedPropertyContainers == null)
        updatedPropertyContainers = new HashSet<PropertyContainer>();
      updatedPropertyContainers.add(propertyEntry.entity());
    }
    if (updatedPropertyContainers != null)
      updateTimestampsFor(updatedPropertyContainers, currentTime);
  }

  private void updateRemoveTimestampsFor(Iterable<? extends PropertyEntry<?>> propertyEntries, Iterable<? extends PropertyContainer> deletedPropertyContainers, long currentTime) {
    if (propertyEntries == null) return;
    Set<PropertyContainer> updatedPropertyContainers = null;
    for (PropertyEntry<?> propertyEntry : propertyEntries) {
      Set<?> deletedPropertyContainerSet = propertyContainersToSet(deletedPropertyContainers);
      if (deletedPropertyContainerSet == null || !deletedPropertyContainerSet.contains(propertyEntry.entity())){
        if (updatedPropertyContainers == null)
          updatedPropertyContainers = new HashSet<PropertyContainer>();
        updatedPropertyContainers.add(propertyEntry.entity());        
      }
    }
    if (updatedPropertyContainers != null)
      updateTimestampsFor(updatedPropertyContainers, currentTime);
  }
  
  private Set<?> propertyContainersToSet(Iterable<? extends PropertyContainer> propertyContainers){
    if (propertyContainers == null) return null;
    Set<PropertyContainer> propertyContainerSet = null;    
    for (PropertyContainer propertyContainer : propertyContainers){
      if (propertyContainerSet == null)
        propertyContainerSet = new HashSet<PropertyContainer>();
      propertyContainerSet.add(propertyContainer);
    }
    return propertyContainerSet;
  }
  
  private void updateTimestampsFor(Iterable<? extends PropertyContainer> propertyContainers, long currentTime) {
    if (propertyContainers == null) return;
    for (PropertyContainer propertyContainer : propertyContainers) {
      propertyContainer.setProperty(TIMESTAMP_PROPERTY_NAME, currentTime);
    }
  }
}
