package ds.edu.nutriapp.viewModel;

/*
 * Author: Dhanush Venkataramu
 * Andrew ID: dhanushv
 * Last Modified: 8th April 2024
 *
 * This class represents the SharedViewModel, which is responsible for sharing
 * and managing nutrition data between fragments using LiveData.
 *
 * This part of aapplication code is generated from LLM url - chat.openai.com
 */

// Import statements
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import org.json.JSONObject;

// SharedViewModel class declaration
public class SharedViewModel extends ViewModel {
    // MutableLiveData object for storing and updating nutrition data
    private final MutableLiveData<JSONObject> nutritionData = new MutableLiveData<>();

    // Method to set the nutrition data
    public void setNutritionData(JSONObject data) {
        nutritionData.setValue(data);
    }

    // Method to get the LiveData of the nutrition data
    public LiveData<JSONObject> getNutritionData() {
        return nutritionData;
    }
}
