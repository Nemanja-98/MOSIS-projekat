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
import org.w3c.dom.Text;

import java.util.ArrayList;

import elfakrs.mosis.vitaminc.vitatake.ProgressActivity;
import elfakrs.mosis.vitaminc.vitatake.R;
import elfakrs.mosis.vitaminc.vitatake.UserCalibratorActivity;
import elfakrs.mosis.vitaminc.vitatake.enumerations.VitaminValues;

public class VitaminsFragment extends Fragment implements View.OnClickListener {

    private static final String SAVED_BUNDLE_KEY = "VitaminsBundle";
    private ArrayList<ProgressBar> progressBars;
    private ArrayList<TextView> progressIndicators;

    public VitaminsFragment() {
        // Required empty public constructor
    }

    public static VitaminsFragment newInstance(String param1, String param2) {
        VitaminsFragment fragment = new VitaminsFragment();
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
        return inflater.inflate(R.layout.fragment_vitamins, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(UserCalibratorActivity.SHARED_PREF_KEY, Context.MODE_PRIVATE);

        int age = sharedPreferences.getInt(UserCalibratorActivity.AGE_KEY, 0);
        int gender = sharedPreferences.getInt(UserCalibratorActivity.GENDER_KEY, 0);

        Button btnRecalibrate = (Button)view.findViewById(R.id.btnRecalibrateVitamin);
        btnRecalibrate.setOnClickListener(this);

        Button btnProgress = (Button)view.findViewById(R.id.btnProgressVitamin);
        btnProgress.setOnClickListener(this);

        progressBars.add((ProgressBar)view.findViewById(R.id.pbVitamin_A));
        progressBars.add((ProgressBar)view.findViewById(R.id.pbVitamin_C));
        progressBars.add((ProgressBar)view.findViewById(R.id.pbVitamin_D));
        progressBars.add((ProgressBar)view.findViewById(R.id.pbVitamin_E));
        progressBars.add((ProgressBar)view.findViewById(R.id.pbVitamin_K));
        progressBars.add((ProgressBar)view.findViewById(R.id.pbVitamin_B1));
        progressBars.add((ProgressBar)view.findViewById(R.id.pbVitamin_B2));
        progressBars.add((ProgressBar)view.findViewById(R.id.pbVitamin_B3));
        progressBars.add((ProgressBar)view.findViewById(R.id.pbVitamin_B5));
        progressBars.add((ProgressBar)view.findViewById(R.id.pbVitamin_B6));
        progressBars.add((ProgressBar)view.findViewById(R.id.pbVitamin_B9));
        progressBars.add((ProgressBar)view.findViewById(R.id.pbVitamin_B12));

        progressBars.get(0).setMax((gender == 1) ? VitaminValues.VITAMIN_A_MAN.value : VitaminValues.VITAMIN_A_WOMAN.value);

        progressBars.get(1).setMax(VitaminValues.VITAMIN_C_ADULT.value);

        progressBars.get(2).setMax(VitaminValues.VITAMIN_D_ADULT.value);

        progressBars.get(3).setMax((age > 0 && age < 9) ? VitaminValues.VITAMIN_E_4_8.value :
                (age > 8 && age < 14) ? VitaminValues.VITAMIN_E_9_13.value :
                        (age > 13 && age < 18) ? VitaminValues.VITAMIN_E_14_18.value : VitaminValues.VITAMIN_E_19_PLUS.value);

        progressBars.get(4).setMax((gender == 1) ? VitaminValues.VITAMIN_K_MAN.value : VitaminValues.VITAMIN_K_WOMAN.value);

        progressBars.get(5).setMax((age > 0 && age < 14) ? VitaminValues.VITAMIN_B1_9_13.value :
                (age > 13 && gender == 1) ? VitaminValues.VITAMIN_B1_14_PLUS_MAN.value : VitaminValues.VITAMIN_B1_14_PLUS_WOMAN.value);

        progressBars.get(6).setMax((gender == 1) ? VitaminValues.VITAMIN_B2_MAN.value : VitaminValues.VITAMIN_B2_WOMAN.value);

        progressBars.get(7).setMax((gender == 1) ? VitaminValues.VITAMIN_B3_MAN.value : VitaminValues.VITAMIN_B3_WOMAN.value);

        progressBars.get(8).setMax(VitaminValues.VITAMIN_B5_ADULT.value);

        progressBars.get(9).setMax((age > 0 && age < 14) ? VitaminValues.VITAMIN_B6_9_13.value :
                (age > 13 && age < 19) ? VitaminValues.VITAMIN_B6_14_18.value : VitaminValues.VITAMIN_B6_18_PLUS.value);

        progressBars.get(10).setMax(VitaminValues.VITAMIN_B9_ADULT.value);

        progressBars.get(11).setMax(VitaminValues.VITAMIN_B12_ADULT.value);

        progressIndicators.add((TextView)view.findViewById(R.id.tvVitamin_A));
        progressIndicators.add((TextView)view.findViewById(R.id.tvVitamin_C));
        progressIndicators.add((TextView)view.findViewById(R.id.tvVitamin_D));
        progressIndicators.add((TextView)view.findViewById(R.id.tvVitamin_E));
        progressIndicators.add((TextView)view.findViewById(R.id.tvVitamin_K));
        progressIndicators.add((TextView)view.findViewById(R.id.tvVitamin_B1));
        progressIndicators.add((TextView)view.findViewById(R.id.tvVitamin_B2));
        progressIndicators.add((TextView)view.findViewById(R.id.tvVitamin_B3));
        progressIndicators.add((TextView)view.findViewById(R.id.tvVitamin_B5));
        progressIndicators.add((TextView)view.findViewById(R.id.tvVitamin_B6));
        progressIndicators.add((TextView)view.findViewById(R.id.tvVitamin_B9));
        progressIndicators.add((TextView)view.findViewById(R.id.tvVitamin_B12));

        for(int i = 0 ; i < progressIndicators.size() ; i++)
        {
            ProgressBar correlatedBar = progressBars.get(i);
            String displayText;
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
            String saveText;
            saveText = String.valueOf(correlatedBar.getProgress()) + "/" + String.valueOf(correlatedBar.getMax());
            saveInstance.add(saveText);
            i++;
        }
        outState.putStringArrayList(SAVED_BUNDLE_KEY, saveInstance);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnRecalibrateVitamin) {
            Intent userCal_intent = new Intent(getActivity(), UserCalibratorActivity.class);
            startActivity(userCal_intent);
        }
        else
        {
            Intent progress_intent = new Intent(getActivity(), ProgressActivity.class);
            startActivity(progress_intent);
        }
    }
}