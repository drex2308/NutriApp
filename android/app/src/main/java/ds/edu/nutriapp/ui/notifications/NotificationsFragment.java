package ds.edu.nutriapp.ui.notifications;

/*
 * Author: Dhanush Venkataramu
 * Andrew ID: dhanushv
 * Last Modified: 8th April 2024
 *
 * This class represents the Notifications Fragment, responsible for displaying
 * notifications related to nutrition results.
 */

// Import statements
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import ds.edu.nutriapp.R;
import ds.edu.nutriapp.databinding.FragmentResultsBinding;
import ds.edu.nutriapp.viewModel.SharedViewModel;
import org.json.JSONException;
import org.json.JSONObject;

// NotificationsFragment class declaration
public class NotificationsFragment extends Fragment {
    private FragmentResultsBinding binding; // Binding object for the fragment
    private TextView calories; // TextView for displaying calories
    private TextView fat; // TextView for displaying fat
    private TextView water; // TextView for displaying water
    private TextView sugar; // TextView for displaying sugar
    private TextView fiber; // TextView for displaying fiber
    private TextView calcium; // TextView for displaying calcium
    private TextView sodium; // TextView for displaying sodium
    private TextView iron; // TextView for displaying iron
    private TextView potassium; // TextView for displaying potassium
    private TextView vitc; // TextView for displaying vitamin C
    private TextView vitd; // TextView for displaying vitamin D
    private TextView labels; // TextView for displaying health labels
    private TextView mealInfo; // TextView for displaying meal information

    // Method to create the fragment view
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewModel
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        // Inflate the layout for this fragment
        binding = FragmentResultsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root; // Return the fragment view
    }

    // Method called after the view is created
    // Code logic taken from LLM url - chat.openai.com
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observe changes in nutrition data
        viewModel.getNutritionData().observe(getViewLifecycleOwner(), this::updateContent);

        // Initialize TextViews
        calories = view.findViewById(R.id.calories_value);
        water = view.findViewById(R.id.water_value);
        fat = view.findViewById(R.id.fat_value);
        sugar = view.findViewById(R.id.sugar_value);
        fiber = view.findViewById(R.id.fiber_value);
        calcium = view.findViewById(R.id.calcium_value);
        sodium = view.findViewById(R.id.sodium_value);
        iron = view.findViewById(R.id.iron_value);
        potassium = view.findViewById(R.id.potassium_value);
        vitc = view.findViewById(R.id.vitc_value);
        vitd = view.findViewById(R.id.vitd_value);
        labels = view.findViewById(R.id.labels);
        mealInfo = view.findViewById(R.id.mealInfo);
    }

    // Method called when the fragment view is destroyed
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Release the binding object
    }

    // Method to update the content based on the received JSON response
    public void updateContent(JSONObject response) {
        try {
            // Set values for each TextView
            calories.setText(response.getString("calories"));
            water.setText(response.getString("water"));
            fat.setText(response.getString("fat"));
            sugar.setText(response.getString("sugar"));
            fiber.setText(response.getString("fiber"));
            calcium.setText(response.getString("calcium"));
            sodium.setText(response.getString("sodium"));
            iron.setText(response.getString("iron"));
            potassium.setText(response.getString("potassium"));
            vitc.setText(response.getString("vitaminc"));
            vitd.setText(response.getString("vitamind"));
            String labelText = "\n    Health Labels:\n " + response.getString("labels");
            labels.setText(labelText);
            String mealText = "Meal Type: " + response.getString("meal") + "\nCuisine: " + response.getString("cuisine");
            mealInfo.setText(mealText);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
