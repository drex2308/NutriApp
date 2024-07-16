// Define the package for the NutriApp
package ds.nutriapp;

// Import the necessary MongoDB client classes
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
// Import servlet classes and annotations for web integration
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// Import BSON document classes for MongoDB document handling
import org.bson.Document;
import org.bson.conversions.Bson;
// Import Java IO and utility classes
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Author: Dhanush Venkataramu
 * Last Modified: 8th April 2024
 *
 * Description:
 * Servlet implementation for displaying dashboard analytics.
 * This servlet handles GET requests to aggregate and display data from MongoDB,
 * including operational analytics and logs. It showcases average delays, top ingredients,
 * and measure usage, as well as detailed log entries for API interactions.
 */
// Annotate the servlet for web deployment
@WebServlet(name = "DashboardServlet", value = "/api/dashboard")
public class DashboardServlet extends HttpServlet {
    static String uri = "mongodb://dhanushv:dhanush0303@ac-7oqob2x-shard-00-00.y9dfw4k.mongodb.net:27017,ac-7oqob2x-shard-00-01.y9dfw4k.mongodb.net:27017,ac-7oqob2x-shard-00-02.y9dfw4k.mongodb.net:27017/DSCluster?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1";
    // Create a MongoDB client
    static MongoClient mongoClient = MongoClients.create(uri);
    // Select the NutriApp database
    static MongoDatabase database = mongoClient.getDatabase("NutriApp");
    // Override the doGet method to serve HTTP GET requests
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        // Exception handling block
        int count = 0;
        long delay = 0;
        // Get the 'delays' collection
        MongoCollection<Document> collection = database.getCollection("delays");
        // Iterate over documents in the 'delays' collection
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            Document curDoc = cursor.next();
            // Sum up the delays from the documents
            delay += curDoc.getLong("item");
            // Increment the counter for each document
            count++;
        }
        // Get the 'ingrdList' collection
        collection = database.getCollection("ingrdList");
        // Define aggregation pipeline for top 3 ingredients
        // Code to aggregate data from mongodb, taken from LLM. url-chat.openai.com
        List<Bson> aggregationPipeline = Arrays.asList(
                Aggregates.group("$item", Accumulators.sum("count", 1)),
                Aggregates.sort(Sorts.descending("count")),
                Aggregates.limit(3)
        );
        // Execute the aggregation pipeline
        // Code to aggregate data from mongodb, taken from LLM. url-chat.openai.com
        List<Document> topIngredients = collection.aggregate(aggregationPipeline).into(new ArrayList<>());
        // Get the 'measureList' collection
        collection = database.getCollection("measureList");
        // Define aggregation pipeline for top 3 measures
        // Code to aggregate data from mongodb, taken from LLM. url-chat.openai.com
        List<Bson> aggregationPipeline1 = Arrays.asList(
                Aggregates.group("$item", Accumulators.sum("count", 1)),
                Aggregates.sort(Sorts.descending("count")),
                Aggregates.limit(3)
        );
        // Execute the aggregation pipeline
        // Code to aggregate data from mongodb, taken from LLM. url-chat.openai.com
        List<Document> topMeasures = collection.aggregate(aggregationPipeline1).into(new ArrayList<>());
        // Reconnect to MongoDB and retrieve logs
        // Code to read from mongodb, taken from mongodb docs. url-https://www.mongodb.com/docs/guides/crud/read/
        collection = database.getCollection("AppLogs");
        // Find all documents in the AppLogs collection
        List<Document> logEntries = collection.find().into(new ArrayList<>());
        // Set attributes in request scope
        request.setAttribute("averageDelay", delay/count);
        request.setAttribute("topIngredients", topIngredients);
        request.setAttribute("topMeasures", topMeasures);
        request.setAttribute("logEntries", logEntries);
        try {
            // Forward to the JSP
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // Static method to insert a document into a specified MongoDB collection
    // Code to insert data taken from mongodb docs. url-https://www.mongodb.com/docs/guides/crud/insert/
    public static void insertMongo(Document doc, String col) {
        // MongoDB connection URI with credentials
        // Get the specified collection
        MongoCollection<Document> collection = database.getCollection(col);
        // Insert the document into the collection
        collection.insertOne(doc);
    }
}
