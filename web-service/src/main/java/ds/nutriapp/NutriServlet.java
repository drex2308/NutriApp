// Package declaration for NutriApp
package ds.nutriapp;

// Import statements for required Java and external libraries
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

/**
 * Author: Dhanush Venkataramu
 * Last Modified: 8th April 2024
 *
 * Description:
 * Servlet implementation for processing nutrient information requests.
 * This servlet handles POST requests to analyze food ingredients and return nutritional information.
 */
// Declaration of NutriServlet class extending HttpServlet for web requests handling
@WebServlet(name = "NutriServlet", value = "/api/nutrients")
public class NutriServlet extends HttpServlet {
    // Initialization method of the servlet
    public void init() {
        // Servlet initialization logic can be added here
    }
    // Method to handle POST requests sent to the servlet
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Start time of the request processing for performance tracking
        long start = System.currentTimeMillis();
        // Current timestamp for logging
        Instant cur = Instant.now();
        // Setting character encoding for the request
        request.setCharacterEncoding("UTF-8");
        // Setting the response type to JSON
        response.setContentType("application/json; charset=UTF-8");
        // StringBuilder to accumulate the request payload
        StringBuilder buffer = new StringBuilder();
        // Reading the request body line by line
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        }
        // Parsing the request body to a JSON object
        JSONObject req = new JSONObject(buffer.toString());
        // Extracting the ingredients data from the request
        String ingrds = req.getString("ingrds");
        String[] ingrdList = ingrds.split("\n");
        // Creating a JSON object to send to the external API
        JSONObject requestToApi = new JSONObject();
        requestToApi.put("ingr", ingrdList);
        // Creating a document to log the request details in MongoDB
        Document doc = new Document("origin", "Mobile")
                .append("dest", "Servlet")
                .append("api_endpoint", "/api/nutrients")
                .append("method", "POST")
                .append("body", ingrds.replaceAll("\n", ", "))
                .append("delay", (System.currentTimeMillis() - start))
                .append("device", request.getHeader("User-Agent"))
                .append("timestamp", cur);
        // Inserting the log document into MongoDB
        DashboardServlet.insertMongo(doc, "AppLogs");
        // Looping through the ingredients list for processing
        for (String ingrd : ingrdList) {
            // Creating and inserting item documents into MongoDB
            Document item = new Document("item", ingrd.split(" ")[2]);
            Document measure = new Document("item", ingrd.split(" ")[1]);
            DashboardServlet.insertMongo(item, "ingrdList");
            DashboardServlet.insertMongo(measure, "measureList");
        }
        // Configuring the URL for the external nutrition API
        String apiUrl = "https://api.edamam.com/api/nutrition-details?app_key=af817de4747c855b542ad31d033f2759&app_id=c108572b";
        URL url = new URL(apiUrl);
        // Opening a connection to the external API
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // Setting the request method to POST
        connection.setRequestMethod("POST");
        // Setting the request content type to JSON
        connection.setRequestProperty("Content-Type", "application/json");
        // Enabling output for the connection
        connection.setDoOutput(true);
        // Writing the JSON payload to the API request
        try (OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream())) {
            out.write(requestToApi.toString());
            out.flush();
        }
        // Creating a document to log the API request details
        Document doctoApi = new Document("origin", "Servlet")
                .append("dest", "API")
                .append("api_endpoint", "api.edamam.com/api/nutrition-details")
                .append("method", "POST")
                .append("body", requestToApi.toString())
                .append("delay", (System.currentTimeMillis() - start))
                .append("device", "")
                .append("timestamp", Instant.now());
        // Inserting the API request log into MongoDB
        DashboardServlet.insertMongo(doctoApi, "AppLogs");
        // Time tracking for the API response delay
        long ApiDelay = System.currentTimeMillis();
        long PostApiDelay = 0;
        // Getting the response code from the API
        int responseCode = connection.getResponseCode();
        // Updating the current timestamp after the API response
        cur = Instant.now();
        // Checking if the API response is OK
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Calculating the delay in receiving the API response
            PostApiDelay = System.currentTimeMillis() - ApiDelay;
            // Reading the API response
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuffer res = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    res.append(inputLine);
                }
                // Converting the response buffer to a string
                String responseData = res.toString();
                // Creating a document to log the response from the API
                Document docFromApi = new Document("origin", "API")
                        .append("dest", "Servlet")
                        .append("api_endpoint", "api.edamam.com/api/nutrition-details")
                        .append("method", "POST")
                        .append("body", responseData)
                        .append("delay", (System.currentTimeMillis() - start))
                        .append("device", "")
                        .append("timestamp", cur);
                // Inserting the API response log into MongoDB
                DashboardServlet.insertMongo(docFromApi, "AppLogs");
                // Parsing the API response data
                JSONObject resFromApi = new JSONObject(responseData);
                JSONObject responseToApp = new JSONObject();
                // Processing and adding nutritional information to the response object
                // Extracting health labels from the API response
                JSONArray arrayLabel = (JSONArray) resFromApi.get("healthLabels");
                // Concatenating the first three health labels for the response
                responseToApp.put("labels", arrayLabel.get(0) + " " + arrayLabel.get(1) + " " + arrayLabel.get(2));
                // Extracting meal type from the API response
                JSONArray meal = (JSONArray) resFromApi.get("mealType");
                // Adding meal type to the response object
                responseToApp.put("meal", meal.get(0));
                // Extracting cuisine type from the API response
                JSONArray cuisine = (JSONArray) resFromApi.get("cuisineType");
                // Adding cuisine type to the response object
                responseToApp.put("cuisine", cuisine.get(0));
                // Extracting the nutrients object from the API response
                JSONObject nutrients = (JSONObject) resFromApi.get("totalNutrients");
                // Extracting calorie information and rounding its value
                JSONObject calories = (JSONObject) nutrients.get("ENERC_KCAL");
                responseToApp.put("calories", round(calories.getDouble("quantity")) + " " + calories.getString("unit"));
                // Extracting water content information and rounding its value
                JSONObject water = (JSONObject) nutrients.get("WATER");
                responseToApp.put("water", round(water.getDouble("quantity")) + " " + water.getString("unit"));
                // Extracting sugar content information and rounding its value
                JSONObject sugar = (JSONObject) nutrients.get("SUGAR");
                responseToApp.put("sugar", round(sugar.getDouble("quantity")) + " " + sugar.getString("unit"));
                // Extracting fat content information and rounding its value
                JSONObject fat = (JSONObject) nutrients.get("FAT");
                responseToApp.put("fat", round(fat.getDouble("quantity")) + " " + fat.getString("unit"));
                // Extracting fiber content information and rounding its value
                JSONObject fiber = (JSONObject) nutrients.get("FIBTG");
                responseToApp.put("fiber", round(fiber.getDouble("quantity")) + " " + fiber.getString("unit"));
                // Extracting calcium content information and rounding its value
                JSONObject calcium = (JSONObject) nutrients.get("CA");
                responseToApp.put("calcium", round(calcium.getDouble("quantity")) + " " + calcium.getString("unit"));
                // Extracting sodium content information and rounding its value
                JSONObject sodium = (JSONObject) nutrients.get("NA");
                responseToApp.put("sodium", round(sodium.getDouble("quantity")) + " " + sodium.getString("unit"));
                // Extracting potassium content information and rounding its value
                JSONObject potassium = (JSONObject) nutrients.get("K");
                responseToApp.put("potassium", round(potassium.getDouble("quantity")) + " " + potassium.getString("unit"));
                // Extracting iron content information and rounding its value
                JSONObject iron = (JSONObject) nutrients.get("FE");
                responseToApp.put("iron", round(iron.getDouble("quantity")) + " " + iron.getString("unit"));
                // Extracting vitamin C content information and rounding its value
                JSONObject vitaminc = (JSONObject) nutrients.get("VITC");
                responseToApp.put("vitaminc", round(vitaminc.getDouble("quantity")) + " " + vitaminc.getString("unit"));
                // Extracting vitamin D content information and rounding its value
                JSONObject vitamind = (JSONObject) nutrients.get("VITD");
                responseToApp.put("vitamind", round(vitamind.getDouble("quantity")) + " " + vitamind.getString("unit"));
                // Creating a document to log the data sent to the mobile app
                Document doctoMob = new Document("origin", "Servlet")
                        .append("dest", "Mobile")
                        .append("api_endpoint", "/api/nutrients")
                        .append("method", "POST")
                        .append("body", responseToApp.toString())
                        .append("delay", (System.currentTimeMillis() - start))
                        .append("device", request.getHeader("User-Agent"))
                        .append("timestamp", Instant.now());
                // Inserting the log document into MongoDB
                DashboardServlet.insertMongo(doctoMob, "AppLogs");
                // Creating a document to measure the delay of API response
                Document delayMeasure = new Document("item", PostApiDelay);
                // Inserting the delay document into MongoDB
                DashboardServlet.insertMongo(delayMeasure, "delays");
                // Setting the response content type to JSON
                response.setContentType("application/json");
                // Writing the final response back to the client
                response.getWriter().write(responseToApp.toString());
            }
        } else {
            // Handling non-OK responses from the API
            Document docFromApi = new Document("origin", "API")
                    .append("dest", "Servlet")
                    .append("api_endpoint", "api.edamam.com/api/nutrition-details")
                    .append("method", "POST")
                    .append("body", (responseCode + ""))
                    .append("delay", (System.currentTimeMillis() - start))
                    .append("device", "")
                    .append("timestamp", cur);
            // Logging the failed API response
            DashboardServlet.insertMongo(docFromApi, "AppLogs");
            Document doctoMob = new Document("origin", "Servlet")
                    .append("dest", "Mobile")
                    .append("api_endpoint", "/api/nutrients")
                    .append("method", "POST")
                    .append("body", (responseCode + ""))
                    .append("delay", (System.currentTimeMillis() - start))
                    .append("device", request.getHeader("User-Agent"))
                    .append("timestamp", Instant.now());
            // Logging the response to be sent to the mobile application
            DashboardServlet.insertMongo(doctoMob, "AppLogs");
            Document delayMeasure = new Document("item", PostApiDelay);
            // Logging the API response delay
            DashboardServlet.insertMongo(delayMeasure, "delays");
            // Sending an error response to the client
            response.sendError(responseCode);
        }
    }
    // Utility method for rounding double values
    // code from stackoverflow, url - https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
    public static double round(double value) {
        int places = 2; // Decimal places for rounding
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue(); // Returning the rounded value
    }
    // Cleanup method of the servlet called upon its destruction
    public void destroy() {
        // Cleanup logic can be added here
    }
}
