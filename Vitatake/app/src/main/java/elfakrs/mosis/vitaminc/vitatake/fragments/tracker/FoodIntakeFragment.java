package elfakrs.mosis.vitaminc.vitatake.fragments.tracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import elfakrs.mosis.vitaminc.vitatake.ProgressActivity;
import elfakrs.mosis.vitaminc.vitatake.enumerations.NutrientValues;
import elfakrs.mosis.vitaminc.vitatake.R;
import elfakrs.mosis.vitaminc.vitatake.UserCalibratorActivity;

public class FoodIntakeFragment extends Fragment implements View.OnClickListener {

    private final String SAVED_BUNDLE_KEY = "FoodIntakeBundle";

    private ArrayList<ProgressBar> progressBars;

    private ArrayList<TextView> progressIndicators;

    private static final double CAL_MULTIPLIER = 0.129598;

    public FoodIntakeFragment() {
        // Required empty public constructor
    }


    public static FoodIntakeFragment newInstance(String param1, String param2) {
        FoodIntakeFragment fragment = new FoodIntakeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBars = new ArrayList<ProgressBar>();
        progressIndicators = new ArrayList<TextView>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_intake, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(UserCalibratorActivity.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        int height = Integer.parseInt(sharedPreferences.getString(UserCalibratorActivity.HEIGHT_KEY, "0"));
        double height_meters = (double)height/100;
        int weight = Integer.parseInt(sharedPreferences.getString(UserCalibratorActivity.WEIGHT_KEY, "0"));

        double BMI = (double)(weight)/(height_meters * height_meters);

        Button btnRecalibrate = (Button)view.findViewById(R.id.btnRecalibrateFood);
        btnRecalibrate.setOnClickListener(this);

        TextView tvBMI = (TextView)view.findViewById(R.id.tvBMI);
        tvBMI.setText(String.format("BMI: %.2f", BMI));

        Button btnProgress = (Button)view.findViewById(R.id.btnProgressFood);
        btnProgress.setOnClickListener(this);

        progressBars.add(view.findViewById(R.id.pbCalcium));
        progressBars.add(view.findViewById(R.id.pbCholine));
        progressBars.add(view.findViewById(R.id.pbFiber));
        progressBars.add(view.findViewById(R.id.pbMagnesium));
        progressBars.add(view.findViewById(R.id.pbPotassium));
        progressBars.add(view.findViewById(R.id.pbSelenium));
        progressBars.add(view.findViewById(R.id.pbSodium));
        progressBars.add(view.findViewById(R.id.pbSugars));
        progressBars.add(view.findViewById(R.id.pbZink));
        progressBars.add(view.findViewById(R.id.pbCalorie));
        progressBars.add(view.findViewById(R.id.pbFat));
        progressBars.add(view.findViewById(R.id.pbProtein));
        progressBars.add(view.findViewById(R.id.pbCarbohydrate));

        int age = sharedPreferences.getInt(UserCalibratorActivity.AGE_KEY, 0);
        int gender = sharedPreferences.getInt(UserCalibratorActivity.GENDER_KEY, 0);
        progressBars.get(0).setMax((age >= 0 && age <= 70 && gender == 1) ? NutrientValues.CALCIUM_1_70_MAN.label :
                (age > 70 && gender == 1) ? NutrientValues.CALCIUM_71_PLUS_MAN.label :
                        (age >= 0 && age <= 50 && gender == 2) ? NutrientValues.CALCIUM_1_50_WOMAN.label : NutrientValues.CALCIUM_51_PLUS_WOMAN.label);

        progressBars.get(1).setMax((gender == 1) ? NutrientValues.CHOLINE_MAN.label : NutrientValues.CHOLINE_WOMAN.label);

        progressBars.get(2).setMax((gender == 1) ? NutrientValues.FIBER_MAN.label : NutrientValues.FIBER_WOMAN.label);

        progressBars.get(3).setMax((gender == 1) ? NutrientValues.MAGNESIUM_MAN.label : NutrientValues.MAGNESIUM_WOMAN.label);

        progressBars.get(4).setMax(NutrientValues.POTASSIUM_ADULT.label);

        progressBars.get(5).setMax(NutrientValues.SELENIUM_ADULT.label);

        progressBars.get(6).setMax(NutrientValues.SODIUM_ADULT.label);

        progressBars.get(7).setMax(NutrientValues.SUGAR_ADULT.label);

        progressBars.get(8).setMax((gender == 1) ? NutrientValues.ZINK_MAN.label : NutrientValues.ZINK_WOMAN.label);

        int neededCalories = (int)calculateCalories(weight, height, age, (gender == 1) ? true : false);

        progressBars.get(9).setMax(neededCalories);

        progressBars.get(10).setMax((int)calorieToGram(neededCalories/4));

        progressBars.get(11).setMax((int)(weight * 0.8));

        progressBars.get(12).setMax((int)calorieToGram(neededCalories / 2));

        progressIndicators.add((TextView)view.findViewById(R.id.tvCalcium));
        progressIndicators.add((TextView)view.findViewById(R.id.tvCholine));
        progressIndicators.add((TextView)view.findViewById(R.id.tvFiber));
        progressIndicators.add((TextView)view.findViewById(R.id.tvMagnesium));
        progressIndicators.add((TextView)view.findViewById(R.id.tvPotassium));
        progressIndicators.add((TextView)view.findViewById(R.id.tvSelenium));
        progressIndicators.add((TextView)view.findViewById(R.id.tvSodium));
        progressIndicators.add((TextView)view.findViewById(R.id.tvSugars));
        progressIndicators.add((TextView)view.findViewById(R.id.tvZink));
        progressIndicators.add((TextView)view.findViewById(R.id.tvCalories));
        progressIndicators.add((TextView)view.findViewById(R.id.tvFat));
        progressIndicators.add((TextView)view.findViewById(R.id.tvProtein));
        progressIndicators.add((TextView)view.findViewById(R.id.tvCarbohydrate));

        for(int i = 0 ; i < progressIndicators.size() ; i++)
        {
            ProgressBar correlatedBar = progressBars.get(i);
            String text = progressIndicators.get(i).getText().toString();
            String displayText;
            if(text.equals("Calories") || text.equals("Fat") || text.equals("Protein") || text.equals("Carbohydrate"))
                displayText = progressIndicators.get(i).getText() + System.lineSeparator() + String.valueOf(correlatedBar.getProgress()) + "/" + String.valueOf(correlatedBar.getMax());
            else
                displayText = String.valueOf(correlatedBar.getProgress()) + "/" + String.valueOf(correlatedBar.getMax());
            progressIndicators.get(i).setText(displayText);
        }

        if(savedInstanceState != null){
            ArrayList<String> savedState = savedInstanceState.getStringArrayList(SAVED_BUNDLE_KEY);
            for(int i = 0 ; i < progressBars.size() ; i++) {
                progressBars.get(i).setProgress(Integer.parseInt(savedState.get(i)));
                progressIndicators.get(i).setText(savedState.get(i + progressBars.size()));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnRecalibrateFood) {
            Intent userCal_intent = new Intent(getActivity(), UserCalibratorActivity.class);
            startActivity(userCal_intent);
        }
        else
        {
            Intent progress_intent = new Intent(getActivity(), ProgressActivity.class);
            startActivity(progress_intent);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> saveInstance = new ArrayList<String>();
        for(ProgressBar el : progressBars)
        {
            saveInstance.add(String.valueOf(el.getProgress()));
        }
        int i = 0;
        for(TextView el : progressIndicators)
        {
            ProgressBar correlatedBar = progressBars.get(i);
            String text = el.getText().toString();
            String saveText;
            if(text.equals("Calories") || text.equals("Fat") || text.equals("Protein") || text.equals("Carbohydrate"))
                saveText = el.getText() + System.lineSeparator() + String.valueOf(correlatedBar.getProgress()) + "/" + String.valueOf(correlatedBar.getMax());
            else
                saveText = String.valueOf(correlatedBar.getProgress()) + "/" + String.valueOf(correlatedBar.getMax());
            saveInstance.add(saveText);
            i++;
        }
        outState.putStringArrayList(SAVED_BUNDLE_KEY, saveInstance);
    }

    private double calculateCalories(int weight, int height, int age, boolean manFlag)
    {
        if(manFlag)
            return 66.5 + 13.8 * weight + 5 * height - 6.8 * age;
        else
            return 655.1 + 9.6 * weight + 1.9 * height - 4.7 * age;
    }

    private double calorieToGram(int calorieValue)
    {
        return calorieValue * CAL_MULTIPLIER;
    }
}