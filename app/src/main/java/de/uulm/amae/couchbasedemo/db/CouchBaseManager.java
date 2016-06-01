package de.uulm.amae.couchbasedemo.db;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.android.AndroidContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Fabian Widmann on 01.06.2016.
 */
public class CouchBaseManager {
    public final String TAG = "CBDemoActivity>DBM";
    private Manager manager;

    public List<Document> getAllDocuments(Database database) throws CouchbaseLiteException {
        // Let's find the documents that have conflicts so we can resolve them:
        List<Document> retList=new ArrayList<>();
        Query query = database.createAllDocumentsQuery();
        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        QueryEnumerator result = query.run();
        for (Iterator<QueryRow> it = result; it.hasNext(); ) {
            QueryRow row = it.next();
            if (row.getConflictingRevisions().size() > 0) {
                Log.w(TAG, "Conflict in document:"+ row.getDocumentId());
                //beginConflictResolution(row.getDocument());
            }
            else{
               retList.add(row.getDocument());
            }
        }
        return retList;
    }

    /**
     * Retrieves the document by the ID
     *
     * @param database
     * @param documentID
     * @return
     */
    public Document retrieveDocumentByID(Database database, String documentID) {
        return database.getDocument(documentID);
    }

    /**
     * Saves the content to the open database.
     *
     * @param database
     * @param content  - a map with a string as key and object as value.
     * @return
     */
    public String createDocument(Database database, Map<String, Object> content) {
        // Create a new document and add data
        Document document = database.createDocument();
        String documentId = document.getId();
        try {
            // Save the properties to the document
            document.putProperties(content);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
        return documentId;
    }

    /**
     * Updates the given document via its id with the new content.
     *
     * @param database
     * @param documentId
     * @param updatedContent a map with a string as key and object as value.
     */
    public void updateDoc(Database database, String documentId, Map<String, Object> updatedContent) {
        Document document = database.getDocument(documentId);
        try {
            // Save to the Couchbase local Couchbase Lite DB
            document.putProperties(updatedContent);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
    }

    /**
     * Adds a binary attachement to the specified document in the database. More about attachements:
     * http://developer.couchbase.com/documentation/mobile/1.2/develop/guides/couchbase-lite/native-api/attachment/index.html
     * @param database
     * @param documentId
     * @param contentStream - ByteArrayInputStream
     */
    public void addAttachment(Database database, String documentId,ByteArrayInputStream contentStream) {
        Document document = database.getDocument(documentId);
        try {
            UnsavedRevision revision = document.getCurrentRevision().createRevision();
            revision.setAttachment("binaryData", "application/octet-stream", contentStream);
        /* Save doc & attachment to the local DB */
            revision.save();
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
    }

    /**
     * Deletes the document with the given id.
     * @param database
     * @param documentId
     */
    public void delete(Database database, String documentId){
        // delete the document
        Document document = database.getDocument(documentId);
        try {
            document.delete();
            Log.d (TAG, "Deleted document, deletion status = " + document.isDeleted());
        } catch (CouchbaseLiteException e) {
            Log.e (TAG, "Cannot delete document", e);
        }
    }
    /**
     * Provides the current DB instance
     *
     * @return database
     * @throws CouchbaseLiteException
     */
    public Database getDatabaseInstance(String dbName) throws CouchbaseLiteException {
        if ((this.manager != null)) {
            return manager.getDatabase(dbName);
        }
        else
            return null;
    }

    /**
     * Initializes the manager instance.
     * @param context
     * @throws IOException
     */
    public void initializeManagerInstance(AndroidContext context) throws IOException {
        if (manager == null) {
            manager = new Manager(context, Manager.DEFAULT_OPTIONS);
        }

    }
}
