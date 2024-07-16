/*
 * Author: Dhanush Venkataramu
 * Last Modified: 8th April 2024
 *
 * This class represents the main activity of the NutriApp application.
 * It extends AppCompatActivity and serves as the entry point for the application.
 * This activity initializes the UI components, sets up navigation using the BottomNavigationView,
 * and handles user interactions.
 */

package ds.edu.nutriapp;

// Import statements
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import org.json.JSONException;
import org.json.JSONObject;
import ds.edu.nutriapp.databinding.ActivityMainBinding;
import ds.edu.nutriapp.viewModel.SharedViewModel;

public class NutriApp extends AppCompatActivity {
    // Reference to the NutriApp instance
    NutriApp me = this;
    // Reference to the NutriApp activity
    NutriApp ma = null;
    // View binding for the main activity layout
    private ActivityMainBinding binding;
    // ViewModel for shared data between fragments
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the main activity layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize bottom navigation view
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Configure top level destinations for app bar
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        // Set up navigation controller and link with bottom navigation view
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Initialize shared view model
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        // Set reference to this activity
        ma = this;
    }

    // Method to handle submission action
    public void actionSubmit(String ingrds) {
        JSONObject request = null;
        try {
            // Create JSON request object with ingredients
            request = new JSONObject();
            request.put("ingrds", ingrds);
        } catch (JSONException e) {
            // Handle JSON exception
            System.out.println("Error in json");
        }
        // Create GetData instance and call load method
        GetData gd = new GetData();
        gd.load(request, me, ma);
    }

    // Method to display search results
    public void displayResults(JSONObject response) {
        // Get reference to ingredient list TextView
        TextView ingrdList = (TextView) findViewById(R.id.ingredients);
        // Clear previous text
        ingrdList.setText("");
        try {
            // Check if response contains error status
            if (response.getString("status").equals("error")) {
                // Display error message as hint in the ingredient list TextView
                ingrdList.setHint(response.getString("message"));
                return;
            }
            // Set nutrition data in shared view model
            sharedViewModel.setNutritionData(response);
            // Set hint indicating successful data fetch and navigation options
            ingrdList.setHint("Data Fecthed .. Check out results page ! \n Add new receipe too");
        } catch (JSONException e) {
            // Handle JSON exception
            ingrdList.setText("Application Error, try again later !");
        }
    }

    /**
     * Checks if the device is connected to a network.
     * @return true if connected to a network, false otherwise.
     */
    public boolean isNetworkAvailable() {
        // Get the application context
        Context appContext = getApplicationContext();
        // Get the system service for connectivity management
        ConnectivityManager connectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Check if the connectivity manager is not null
        if (connectivityManager != null) {
            // Get information about the active network
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            // Return true if there is an active network connection and it is connected
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        // Return false if connectivity manager is null or no active network is available
        return false;
    }

}
