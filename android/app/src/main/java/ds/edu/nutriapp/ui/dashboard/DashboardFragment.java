package ds.edu.nutriapp.ui.dashboard;
/*
 * Author: Dhanush Venkataramu
 * Andrew ID: dhanushv
 * Last Modified: 8th April 2024
 *
 * This class represents the Dashboard Fragment, responsible for displaying
 * and handling user interactions with the dashboard interface.
 */

// Import statements
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import ds.edu.nutriapp.NutriApp;
import ds.edu.nutriapp.R;
import ds.edu.nutriapp.databinding.FragmentSearchBinding;

// DashboardFragment class declaration
public class DashboardFragment extends Fragment {
    private FragmentSearchBinding binding; // Binding object for the fragment

    // Method to create the fragment view
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewModel
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // Set up spinner for measure options
        // Code taken from LLM url - chat.openai.com
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = rootView.findViewById(R.id.mesaure);
        spinner.setAdapter(adapter);

        // Set up submit button click listener
        Button submitButton = rootView.findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewParam) {
                // Get ingredients list
                TextView ingrdList = rootView.findViewById(R.id.ingredients);
                // Validate ingredients input
                if (ingrdList.getText().toString().isEmpty()) {
                    ingrdList.setError("Please add some ingredients");
                    return;
                }
                // Retrieve ingredients and submit action
                String ingrds = ingrdList.getText().toString();
                ((NutriApp) getActivity()).actionSubmit(ingrds);
            }
        });

        // Set up add button click listener
        Button add = rootView.findViewById(R.id.listAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewParam) {
                // Get user input for weight
                EditText weightView = rootView.findViewById(R.id.weight);
                String weight = weightView.getText().toString();
                // Validate weight input
                if (weight.isEmpty()) {
                    weightView.setError("Please enter some weight");
                    return;
                }
                // Get user input for ingredient
                EditText ingrdView = rootView.findViewById(R.id.ingrd);
                String ingrd = ingrdView.getText().toString();
                // Validate ingredient input
                if (ingrd.isEmpty()) {
                    ingrdView.setError("Please enter some ingredient");
                    return;
                }
                // Get selected measure
                String measure = spinner.getSelectedItem().toString();
                // Create final ingredient entry
                String finalIngrd = weight + " " + measure + " " + ingrd + "\n";
                // Display the ingredient in the list
                TextView ingrdList = rootView.findViewById(R.id.ingredients);
                ingrdList.append(finalIngrd);
                // Clear input fields
                weightView.setText("");
                ingrdView.setText("");
            }
        });

        return rootView; // Return the fragment view
    }

    // Method called when the fragment view is destroyed
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Release the binding object
    }
}
