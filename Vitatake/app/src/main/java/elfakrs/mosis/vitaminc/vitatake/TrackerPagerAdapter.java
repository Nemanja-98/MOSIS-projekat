package elfakrs.mosis.vitaminc.vitatake;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import elfakrs.mosis.vitaminc.vitatake.fragments.tracker.FoodIntakeFragment;
import elfakrs.mosis.vitaminc.vitatake.fragments.tracker.VitaminsFragment;

public class TrackerPagerAdapter extends FragmentStateAdapter {

    public TrackerPagerAdapter(@NonNull FragmentActivity fragmentActivity)
    {
        super(fragmentActivity);
    }

    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 1:
                return new FoodIntakeFragment();
            default:
                return new VitaminsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
