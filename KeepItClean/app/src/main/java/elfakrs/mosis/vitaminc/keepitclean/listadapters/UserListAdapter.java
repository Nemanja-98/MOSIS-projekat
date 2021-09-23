package elfakrs.mosis.vitaminc.keepitclean.listadapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

import elfakrs.mosis.vitaminc.keepitclean.R;
import elfakrs.mosis.vitaminc.keepitclean.data.database.IImageUpdater;
import elfakrs.mosis.vitaminc.keepitclean.data.database.ImageManager;
import elfakrs.mosis.vitaminc.keepitclean.models.User;

public class UserListAdapter extends ArrayAdapter<User> {
    LayoutInflater layoutInflater;
    ArrayList<User> users;
    int viewResourceId;
    ImageView ivAvatar;
    ImageManager imgManager = null;
    private static final String COLOR_GOLD = "#D4AF37";
    private static final String COLOR_SILVER = "#C0C0C0";
    private static final String COLOR_BRONZE = "#CD7F32";


    public UserListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> users) {
        super(context, resource, users);
        this.users = users;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewResourceId = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(viewResourceId, null);

        User user = users.get(position);
        if(user != null) {
            TextView tvUsername = (TextView) convertView.findViewById(R.id.user_tvUsername);
            TextView tvPoints = (TextView) convertView.findViewById(R.id.user_tvPoints);
            ivAvatar = (ImageView) convertView.findViewById(R.id.user_ivAvatar);
            TextView tvPlace = (TextView) convertView.findViewById(R.id.user_tvPlace);

            if (tvUsername != null)
                tvUsername.setText(user.getUsername());
            if (tvPoints != null) {
                tvPoints.setText(user.getPoints() + "pts");
                switch (position) {
                    case 0:
                        tvPoints.setTextColor(Color.parseColor(COLOR_GOLD));
                        break;
                    case 1:
                        tvPoints.setTextColor(Color.parseColor(COLOR_SILVER));
                        break;
                    case 2:
                        tvPoints.setTextColor(Color.parseColor(COLOR_BRONZE));
                        break;
                }
            }
            if (tvPlace != null) {
                tvPlace.setText(String.valueOf(position + 1) + ". ");
            }
        }
        return convertView;
    }
}
