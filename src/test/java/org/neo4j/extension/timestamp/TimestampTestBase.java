package org.neo4j.extension.timestamp;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import static org.junit.Assert.*;

public class TimestampTestBase {

  protected final String TEST_DATA_STORE_DESTINATION = "target/neo4j-test";

  private enum TestRelationshipTypes implements RelationshipType
  {
      TEST
  }
  
  @After
  public void cleanUp() throws Exception {
    File store = new File(TEST_DATA_STORE_DESTINATION);
    if (store.exists() == true) {
      FileUtils.deleteDirectory(store);
    }
  }

  protected Node checkTimestampCreation(GraphDatabaseService graphdb){
    Transaction tx = graphdb.beginTx();
    Node node = graphdb.createNode();
    node.setProperty("test", "test");
    long id = node.getId();
    tx.success();
    tx.finish();

    tx = graphdb.beginTx();
    node = graphdb.getNodeById(id);
    node.getProperty("test");
    // New nodes should have a "modified" property
    long modified = (Long)node.getProperty("modified");
    assertTrue(modified > 0);
    tx.success();
    tx.finish();
    return node;
  }
  
  protected void checkTimestampUpdateOnPropertyAdd(GraphDatabaseService graphdb, Node node){
    long modified = (Long)node.getProperty("modified");
    Transaction tx = graphdb.beginTx();
    node.setProperty("updateTest", "update");
    tx.success();
    tx.finish();
    assertTrue(modified < (Long)node.getProperty("modified"));
  }
  
  protected void checkTimestampUpdateOnRelationshipAdd(GraphDatabaseService graphdb, Node node){
    long modified = (Long)node.getProperty("modified");
    Transaction tx = graphdb.beginTx();
    Node anotherNode = graphdb.createNode();
    anotherNode.setProperty("test2", "test2");
    Relationship relationShip = anotherNode.createRelationshipTo(node, TestRelationshipTypes.TEST);
    tx.success();
    tx.finish();

    // New node and relationship should have a "modified" property
    assertTrue((Long)anotherNode.getProperty("modified") > 0);
    assertTrue((Long)relationShip.getProperty("modified") > 0);
    
    // Original node should have a new timestamp because a relationship was created
    assertTrue(modified < (Long)node.getProperty("modified"));
  }
}
