package ds.edu.nutriapp.ui.home;

/*
 * Author: Dhanush Venkataramu
 * Andrew ID: dhanushv
 * Last Modified: 8th April 2024
 *
 * This class represents the Home Fragment, responsible for displaying
 * the home interface.
 */

// Import statements
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import ds.edu.nutriapp.databinding.FragmentHomeBinding;

// HomeFragment class declaration
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding; // Binding object for the fragment

    // Method to create the fragment view
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewModel
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root; // Return the fragment view
    }

    // Method called when the fragment view is destroyed
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Release the binding object
    }
}
