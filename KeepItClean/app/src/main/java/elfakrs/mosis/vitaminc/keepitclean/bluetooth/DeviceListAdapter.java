package elfakrs.mosis.vitaminc.keepitclean.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;

import elfakrs.mosis.vitaminc.keepitclean.R;

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {
    LayoutInflater layoutInflater;
    ArrayList<BluetoothDevice> devices;
    int viewResourceId;

    public DeviceListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<BluetoothDevice> devices) {
        super(context, resource, devices);
        this.devices = devices;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewResourceId = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(viewResourceId, null);

        BluetoothDevice device = devices.get(position);
        if(device != null) {
            TextView tvDeviceName = (TextView) convertView.findViewById(R.id.device_tvName);
            TextView tvDeviceAddress = (TextView) convertView.findViewById(R.id.device_tvAddress);
//            AppCompatButton btnAddFriend = (AppCompatButton) convertView.findViewById(R.id.device_btnAddFriend);

            if(tvDeviceName != null)
                tvDeviceName.setText(device.getName());
            if(tvDeviceAddress != null)
                tvDeviceAddress.setText(device.getAddress());
//            if(btnAddFriend != null)
//                btnAddFriend.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // TODO: Add onClick event for firend request handle;
//                    }
//                });
        }
        return convertView;
    }
}
