package org.neo4j.extension.timestamp;

import static org.junit.Assert.assertTrue;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

public class TimestampTestBase {

  protected final String TEST_DATA_STORE_DESTINATION = "target/neo4j-test";

  private enum TestRelationshipTypes implements RelationshipType
  {
      TEST
  }

  protected long checkTimestampCreation(GraphDatabaseService graphdb){
    long id = 0;
    try(Transaction tx = graphdb.beginTx()){
      Node node = graphdb.createNode();
      node.setProperty("test", "test");
      id = node.getId();
      tx.success();
    }
    try(Transaction tx = graphdb.beginTx()){
      Node node = graphdb.getNodeById(id);
      node.getProperty("test");
      // New nodes should have a "modified" property
      long modified = (Long)node.getProperty("modified");
      assertTrue(modified > 0);
      tx.success();
      return node.getId();
    }
  }
  
  protected void checkTimestampUpdateOnPropertyAdd(GraphDatabaseService graphdb, long id){
    long modified = 0;
    try(Transaction tx = graphdb.beginTx()){
      Node node = graphdb.getNodeById(id);
      modified = (Long)node.getProperty("modified");
      tx.success();
    }
    try(Transaction tx = graphdb.beginTx()){
      Node node = graphdb.getNodeById(id);
      node.setProperty("updateTest", "update");
      tx.success();
    }
    try(Transaction tx = graphdb.beginTx()){
      Node node = graphdb.getNodeById(id);
      assertTrue(modified < (Long)node.getProperty("modified"));
      tx.success();
    }
  }
  
  protected void checkTimestampUpdateOnRelationshipAdd(GraphDatabaseService graphdb, long id){
    long modified = 0;
    Node node = null;
    Node anotherNode = null;
    Relationship relationShip = null;
    try(Transaction tx = graphdb.beginTx()){
      node = graphdb.getNodeById(id);
      modified = (Long)node.getProperty("modified");
    }
    try(Transaction tx = graphdb.beginTx()){
      anotherNode = graphdb.createNode();
      anotherNode.setProperty("test2", "test2");
      relationShip = anotherNode.createRelationshipTo(node, TestRelationshipTypes.TEST);
      tx.success();
    }
    try(Transaction tx = graphdb.beginTx()){
      // New node and relationship should have a "modified" property
      assertTrue((Long)anotherNode.getProperty("modified") > 0);
      assertTrue((Long)relationShip.getProperty("modified") > 0);
      
      // Original node should have a new timestamp because a relationship was created
      assertTrue(modified < (Long)node.getProperty("modified"));
    }
  }
}
