/*
 * Author: Dhanush Venkataramu
 * Last Modified: 8th April 2024
 *
 * This class handles the retrieval of data from a remote server.
 */
package ds.edu.nutriapp;
// Import statements
import android.app.Activity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

// GetData class declaration
public class GetData {
    NutriApp np = null; // Reference to the main activity
    JSONObject request = null; // JSON object for request data
    JSONObject response; // JSON object for response data

    // Method to initiate data retrieval process
    public void load(JSONObject request, NutriApp activity, NutriApp np) {
        this.np = np;
        this.request = request;
        new BackgroundTask(activity).execute(); // Execute background task
    }

    // Inner class for background task handling
    // Code logic taken from course Lab 8 url - https://github.com/CMU-Heinz-95702/AndroidInterestingPicture
    private class BackgroundTask {
        private Activity activity; // Reference to the activity

        // Constructor
        public BackgroundTask(Activity activity) {
            this.activity = activity;
        }

        // Method to start background task
        private void startBackground() {
            new Thread(new Runnable() {
                public void run() {
                    doInBackground(); // Execute background task
                    // Execute post execution task on UI thread
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            onPostExecute(); // Handle post execution
                        }
                    });
                }
            }).start();
        }

        // Method to handle post execution task
        private void onPostExecute() {
            np.displayResults(response); // Display results in main activity
        }

        // Method to execute background task
        private void execute() {
            startBackground(); // Start background task
        }

        // Background task method to execute in background thread
        private void doInBackground() {
            response = search(request); // Perform data retrieval
        }
    }

    // Method to search for data from remote server
    private JSONObject search(JSONObject request) {
        JSONObject response = null;
        try {
            // Check if the device is connected to the internet
            boolean isConnected = np.isNetworkAvailable();
            // If not connected, create an error response object
            if (!isConnected) {
                response = new JSONObject();
                response.put("status", "error"); // Add status to response
                response.put("message", "Please Check Internet Connection !"); // Add error message
                return response; // Return the error response
            }
            // If connected, proceed with the API call
            String apiUrl = "https://supreme-robot-x55x79pr944vhpxwp-8080.app.github.dev/api/nutrients";
            URL url = new URL(apiUrl); // Create URL object
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // Open Connection
            connection.setRequestMethod("POST"); // Set request method
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8"); // Set content type
            connection.setDoOutput(true); // Enable output
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream()); // Get output stream
            out.write(request.toString()); // Write request data
            out.flush(); // Flush output stream
            out.close(); // Close output stream
            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);// Get response code
            if (responseCode == HttpURLConnection.HTTP_OK) { // If response is successful
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())); // Get input stream
                String inputLine;
                StringBuffer buffer = new StringBuffer(); // Create buffer to store response data
                while ((inputLine = in.readLine()) != null) { // Read response data
                    buffer.append(inputLine); // Append to buffer
                }
                in.close(); // Close input stream
                String responseData = buffer.toString(); // Convert buffer to string
                response = new JSONObject(responseData); // Create JSON object from response data
                response.put("status", "success"); // Add status to response
            } else if (responseCode == 555 || responseCode == 422) { // If response code indicates error
                response = new JSONObject(); // Create error response object
                response.put("status", "error"); // Add status to response
                response.put("message", "Please enter correct ingredients");
                System.out.println("here");// Add error message
            } else { // If internal server error
                response = new JSONObject(); // Create error response object
                response.put("status", "error"); // Add status to response
                response.put("message", "Internal Error! Please try again later with different recipe.. "); // Add error message
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace if IO exception occurs
        } catch (JSONException e) {
            throw new RuntimeException(e); // Throw runtime exception if JSON exception occurs
        }
        return response; // Return response object
    }
}
