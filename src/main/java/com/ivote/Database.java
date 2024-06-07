package com.ivote;

import com.mongodb.ConnectionString;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class representing a MongoDB database and providing methods for database operations.
 * Generic to implement specific functions in later class
 */
public class Database {
    protected MongoClient mongoClient;
    protected MongoDatabase database;
    protected MongoCollection<Document> currentCollection;
    protected List<String> collections;
    protected String currentCollectionName;

    protected String uri;
    protected String dbname;

    /**
     * Constructs a new Database object with the specified MongoDB connection URI and database name.
     *
     * @param uri    the MongoDB connection URI
     * @param dbName the name of the database
     */
    protected Database(String uri, String dbName) {
        this.mongoClient = MongoClients.create(new ConnectionString(uri));
        this.database = mongoClient.getDatabase(dbName);
        this.collections = new ArrayList<>();
        this.currentCollectionName = null;
        this.currentCollection = null;
        this.uri = uri;
        this.dbname = dbName;
    }

    /* COLLECTIONS WORK
     * In the interface, there is a List<String> with the names of collections
     * These are sort of "visible" collections
     * There is also one string called currentCollection set as the current default
     * We can add collections to the list
     * We can create collections in the actual database
     * We can set the default collection
     */

    /**
     * Add all extant database collections to collections list.
     *
     * @return true if there are any extant collections that were added, otherwise false
     */
    protected boolean addAllCollections() {
        this.collections = new ArrayList<>();
        for (String name : database.listCollectionNames()) {
            collections.add(name);
        }
        return collections.size() > 0;
    }

    /**
     * Adds a new collection to the collections list.
     *
     * @param name the name of the collection to add
     * @return true if the collection was successfully added, otherwise false
     */
    protected boolean addCollection(String name) {
        if (collectionExists(name)) {
            return collections.add(name);
        }
        return false;
    }

    /**
     * Creates a new collection in the database and adds it to the collections list.
     *
     * @param name the name of the collection to create
     * @return true if the collection was successfully created and added, otherwise false
     */
    protected boolean createCollection(String name) {
        try {
            database.createCollection(name);
            System.out.println("Collection created successfully");
        } catch (MongoException e) {
            System.err.println("An error occurred while creating the collection: " + e.getMessage());
        }
        return addCollection(name);
    }

    /**
     * Sets the current default collection.
     *
     * @param name the name of the collection to set as default
     * @return true if the collection was successfully set as default, otherwise false
     */
    protected boolean setCurrentCollection(String name) {
        if (collectionVisible(name)) {
            currentCollectionName = name;
            currentCollection = database.getCollection(currentCollectionName);
            return true;
        }
        return false;
    }

    /**
     * Creates a new collection in the database, adds it to the collections list, and sets it as the default collection.
     *
     * @param name the name of the collection to create, add, and set
     * @return true if the collection was successfully created, added, and set, otherwise false
     */
    protected boolean createAddSetCollection(String name) {
        boolean creation = createCollection(name);
        boolean addition = addCollection(name);
        boolean setting = setCurrentCollection(name);
        return creation && addition && setting;
    }

    /**
     * Checks if a collection exists in the database.
     *
     * @param collectionName the name of the collection to check
     * @return true if the collection exists in the database, otherwise false
     */
    protected boolean collectionExists(String collectionName) {
        for (String name : database.listCollectionNames()) {
            if (name.equals(collectionName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a collection is visible (present in the collections list).
     *
     * @param name the name of the collection to check visibility for
     * @return true if the collection is visible, otherwise false
     */
    public boolean collectionVisible(String name) {
        return collections.contains(name);
    }

    /**
     * Retrieves the IDs of all documents in the current collection.
     *
     * @return a list of ObjectIds representing the IDs of all documents in the current collection
     */
    public List<ObjectId> getAllObjectIds() {
        List<ObjectId> ids = new ArrayList<>();

        // Check if the current collection is set
        if (currentCollection != null) {
            // Iterate through documents in the collection and retrieve their _id fields
            for (Document doc : currentCollection.find()) {
                ObjectId id = doc.getObjectId("_id");
                if (id != null) {
                    ids.add(id);
                }
            }
        } else {
            System.err.println("No current collection set. Please set the current collection before retrieving document IDs.");
        }

        return ids;
    }

    /**
     * Removes non-existent collections from the collections list.
     */
    protected void cleanupCollections() {
        Iterator<String> iterator = collections.iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            if (!collectionExists(name)) {
                iterator.remove();
            }
        }
    }

    /* DOCUMENT WORK
     * Here we define documents from parameters and add them
    */

    /**
     * Adds a document to the current collection with the specified key-value pairs.
     *
     * @param keyValues the key-value pairs representing the fields and values of the document
     */
    protected ObjectId addDocument(Document keyValues) {
        if (currentCollection != null) {
            try {
                currentCollection.insertOne(keyValues);
                System.out.println("Document added successfully to collection: " + currentCollection);
                return keyValues.getObjectId("_id");
            } catch (MongoException e) {
                System.err.println("An error occurred while adding the document: " + e.getMessage());
            }
        } else {
            System.err.println("No current collection set. Please set the current collection before adding a document.");
        }
        return null;
    }

    /**
     * Retrieves the document with the specified ID from the current collection.
     *
     * @param id the ObjectId of the document to retrieve
     * @return the Document object representing the retrieved document, or null if the document does not exist
     */
    protected Document getDocument(ObjectId id) {
        Document query = new Document("_id", id);
        return currentCollection.find(query).first();
    }

    /**
     * Removes the document with the specified ID from the current collection.
     *
     * @param id the ObjectId of the document to remove
     * @return true if the document was successfully removed, false otherwise
     */
    protected boolean removeDocument(ObjectId id) {
        // Create a query to find the document by its _id
        Document query = new Document("_id", id);
        
        // Retrieve the current collection and delete the document matching the query
        try {
            currentCollection.deleteOne(query);
            System.out.println("Document removed successfully from collection: " + currentCollection);
            return true;
        } catch (MongoException e) {
            System.err.println("An error occurred while removing the document: " + e.getMessage());
            return false;
        }
    }

    public int getCollectionSize() {
        return (int) currentCollection.countDocuments();
    }

    /**
     * Closes the MongoDB client.
     *
     * @return true if the client was active and successfully closed, otherwise false
     */
    public boolean close() {
        if (mongoClient != null) {
            mongoClient.close();
            return true;
        }
        return false;
    }
}
