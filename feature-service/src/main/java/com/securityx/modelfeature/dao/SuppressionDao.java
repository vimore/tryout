package com.securityx.modelfeature.dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.securityx.modelfeature.common.SuppressionEntry;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import org.bson.BSON;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SuppressionDao {
    private static final String SUPPRESSION_DB = "accounts";
    private static final String SUPPRESSION_COLLECTION = "whitelistitems";

    private static final Logger logger = LoggerFactory.getLogger(SuppressionDao.class);
    private final FeatureServiceConfiguration conf;

    private int callCount = 0;

    public SuppressionDao(FeatureServiceConfiguration conf) {
        this.conf = conf;
    }

    public int getMongoConnections() {
        MongoDatabase admin = MongoUtils.getDatabase("admin", conf);
        Document serverStatus = admin.runCommand(new Document("serverStatus", 1));
        Map connections = (Map) serverStatus.get("connections");
        int numConnections = (Integer)connections.get("current");
        return numConnections;
    }

    /**
     * Get the list of suppression info from storage
     *
     * @return the current list of suppression information
     */
    public List<SuppressionEntry> getSuppressionList() {
        MongoDatabase db = MongoUtils.getDatabase(SUPPRESSION_DB, conf);
        FindIterable<Document> documents = null;
        try {
            documents = db.getCollection(SUPPRESSION_COLLECTION).find();
        } catch (Exception e) {
            logger.warn("Could not get suppression documents from mongo database: ", e);
        }
        List<SuppressionEntry> results = createSuppressionList(documents);

        // Print some stats on Mongo
        if (callCount++ % 10 == 0) {
            try {
                MongoDatabase admin = MongoUtils.getDatabase("admin", conf);
                Document serverStatus = admin.runCommand(new Document("serverStatus", 1));
                Map connections = (Map) serverStatus.get("connections");
                int numConnections = (Integer) connections.get("current");
                Long createdConnections = (Long) connections.get("totalCreated");
                int available = (Integer) connections.get("available");
                logger.debug("MongoStats: current connections [" + numConnections + "] total created [" + createdConnections + "] available [" + available + "]");
            } catch (Exception e) {
                logger.warn("Error connecting to MongoDB admin database", e);
            }
        }

        return results;
    }

    /**
     * Check the passed in list of SuppressionEntries to see if the indicated entity should be suppressed. Returns
     * true iff the entity should be suppressed.
     *
     * The entity is indicated by the ip, hostname and username parameters.  Some of these may be null.
     *
     * Note that we do not get the list ourselves, though we could.  This is because the method will frequently be
     * called many times when the list will not change, and we don't want to query mongo every time to get the list.
     *
     * @param suppressionList list of suppressions that are in effect
     * @param ip ip of the entity to check
     * @param hostname hostname of the entity to check
     * @param username username of the entity to check
     * @param behavior behavior to check. Note that in this case behavior corresponds to the EventType
     * @param checkContains if true, the entity check should be a check that the passed in ip, hostname or username contains the suppressed entity. If
     *                      false, the check will be equalsIgnoreCase()
     * @return true iff the entity/behavior is not suppressed
     */
    public boolean shouldSuppress(List<SuppressionEntry> suppressionList, String ip, String hostname, String username, String behavior, boolean checkContains) {
        boolean suppress = false;
        for (SuppressionEntry suppEntry : suppressionList) {
            if (SuppressionEntry.SUPPRESS_ENTITY.equals(suppEntry.getSuppress())) {
                if (checkEntity(suppEntry, ip, username, hostname, checkContains)) {
                    suppress = true;
                    break;
                }
            } else if (SuppressionEntry.SUPPRESS_BEHAVIOR.equals(suppEntry.getSuppress())) {
                if (behavior != null && behavior.equalsIgnoreCase(suppEntry.getBehavior())) {
                    suppress = true;
                    break;
                }
            } else if (SuppressionEntry.SUPPRESS_ENTITY_AND_BEHAVIOR.equals(suppEntry.getSuppress())) {
                if (checkEntity(suppEntry, ip, username, hostname, checkContains) &&
                        behavior != null && behavior.equalsIgnoreCase(suppEntry.getBehavior())) {
                    suppress = true;
                    break;
                }
            } else {
                logger.warn("Ignoring invalid suppression [" + suppEntry.getSuppress() + "]");
            }
        }
        return suppress;
    }

    public boolean shouldSuppressNoBehaviorInfo(String ip, String hostname, String username) {
        return shouldSuppressNoBehaviorInfo(getSuppressionList(), ip, hostname, username, false);
    }

    /**
     * Check the suppression list to see if there is an entity suppression entry that matches the given entity.  This is for
     * use in cases where we have no behavior information, and no checks can be done against entries that include behavior checks.
     *
     * @param suppressionList list of entities to suppress
     * @param ip the ip of the entity to check
     * @param hostname the hostname of the entity to check
     * @param username the username of the entity to check
     * @param checkContains if true, the entity check should be a check that the passed in ip, hostname or username contains the suppressed entity. If
     *                      false, the check will be equalsIgnoreCase()
     * @return true iff the entity should be suppressed.
     */
    public boolean shouldSuppressNoBehaviorInfo(List<SuppressionEntry> suppressionList, String ip, String hostname, String username, boolean checkContains) {
        boolean suppress = false;
        for (SuppressionEntry suppEntry : suppressionList) {
            // We can only check against entries where a given entity is being suppressed.
            if (SuppressionEntry.SUPPRESS_ENTITY.equals(suppEntry.getSuppress())) {
                if (checkEntity(suppEntry, ip, username, hostname, false)) {
                    suppress = true;
                    break;
                }
            }
        }
        return suppress;
    }

    /**
     * Take a mongo documents and create a list of SuppressionEntries from them.  Note
     * that if the input is null or an empty set, we simply return an empty list of
     * SuppressionEntries.  This is intentional - we would prefer to fail to suppress
     * correctly but to return results than to fail because we can't get a suppression list
     * to check the results for suppression.
     *
     * @param documents Mongo object containing a set of Mongo documents
     * @return a list of SuppressionEntry objects containing information on what should be suppressed.
     */
    protected List<SuppressionEntry> createSuppressionList(FindIterable<Document> documents) {
        List<SuppressionEntry> results = new ArrayList<SuppressionEntry>();
        if (documents != null) {
            try {
                for (Document doc : documents) {
                    ObjectId objId = doc.getObjectId("_id");
                    String behavior = doc.getString("behavior");
                    String category = doc.getString("category");
                    String entity = doc.getString("entity");
                    String suppress = doc.getString("suppress");
                    String type = doc.getString("type");
                    results.add(new SuppressionEntry(objId, behavior, category, entity, suppress, type));
                }
            } catch (Exception e) {
                logger.warn("Exception while querying mongo for suppression info", e);
            }
        }
        return results;
    }

    protected boolean checkEntity(SuppressionEntry toSuppress, String ip, String user, String host, boolean checkContains) {
        String entity = toSuppress.getEntity();
        String type = toSuppress.getType();
        if (entity == null || type == null) {
            // Should never happen, would indicate a malformed SuppressionEntry
            logger.warn("Malformed SuppressionEntry[" + toSuppress.toString() + "]");
            return false;
        }
        if (SuppressionEntry.IP.equals(type)) {
            if (checkContains) {
                if (ip == null) {
                    ip = "";
                }
                return ip.contains(entity) || entity.contains(ip);
            } else {
                return entity.equalsIgnoreCase(ip);
            }
        } else if (SuppressionEntry.HOST.equalsIgnoreCase(type)) {
            if (checkContains) {
                if (host == null) {
                    host = "";
                }
                return host.contains(entity) || entity.contains(host);
            } else {
                return entity.equals(host);
            }
        } else if (SuppressionEntry.USER.equals(type)) {
            if (checkContains) {
                if (user == null) {
                    user = "";
                }
                return user.contains(entity) || entity.contains(user);
            } else {
                return entity.equalsIgnoreCase(user);
            }
        }

        return false;
    }

}
