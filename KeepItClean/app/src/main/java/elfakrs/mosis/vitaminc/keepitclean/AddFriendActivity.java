package elfakrs.mosis.vitaminc.keepitclean;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

import elfakrs.mosis.vitaminc.keepitclean.bluetooth.BluetoothConnectionService;
import elfakrs.mosis.vitaminc.keepitclean.bluetooth.DeviceListAdapter;

public class AddFriendActivity extends AppCompatActivity {
    private static final int BLUETOOTH_ENABLE_DISABLE = 1;
    private static final int DISCOVERABLE_ENABLE_DISABLE = 2;
    private static final int PERMISSION_REQUEST = 3;
    BluetoothAdapter bluetoothAdapter;
    TextView tvDiscoverable;
    SwitchCompat switchDiscoverable;
    AppCompatButton btnDiscover;
    AppCompatButton btnAddFriend;
    AppCompatButton btnConnect;
    public ArrayList<BluetoothDevice> btDevices;
    public DeviceListAdapter deviceAdapter;
    ListView lvDevices;

    private BluetoothConnectionService btConnectionService;
    private UUID insecureUUID = UUID.fromString("77724a5d-6508-40f6-8d22-b1fdcf67ca00");
    private BluetoothDevice connectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        Toolbar toolbar = (Toolbar) findViewById(R.id.bluetooth_toolbar);
        ImageView ivBluetooth = (ImageView) findViewById(R.id.bluetooth_ivBluetooth);
        SwitchCompat switchOnOff = (SwitchCompat) findViewById(R.id.bluetooth_switchBluetooth);
        tvDiscoverable = (TextView) findViewById(R.id.bluetooth_tvDiscoverable);
        switchDiscoverable  = (SwitchCompat) findViewById(R.id.bluetooth_switchDiscoverable);
        lvDevices = (ListView) findViewById(R.id.bluetooth_lvDevices);
        btDevices = new ArrayList<BluetoothDevice>();
        btnDiscover = (AppCompatButton) findViewById(R.id.bluetooth_btnDiscover);
        btnAddFriend = (AppCompatButton) findViewById(R.id.bluetooth_btnAddFriend);
        btnConnect = (AppCompatButton) findViewById(R.id.bluetooth_btnConnect);
        btConnectionService = new BluetoothConnectionService(this);

        toolbar.setNavigationIcon(R.drawable.outline_chevron_left_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopDiscovery();
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        switchOnOff.setOnClickListener(new View.OnClickListener() {
            private boolean onOff = false;
            @Override
            public void onClick(View v) {
                Drawable img = getResources().getDrawable((onOff) ? R.drawable.ic_baseline_bluetooth_off_24 : R.drawable.ic_baseline_bluetooth_on_24, null);
                ivBluetooth.setImageDrawable(img);
                onOff = !onOff;
                enableDisableBT();
            }
        });

        switchDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableDisableDiscoverable();
            }
        });

        IntentFilter bondFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(bondReceiver, bondFilter);

        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothAdapter.cancelDiscovery();

                Log.d("My logs: ", "Requested bond with " + btDevices.get(position).getName());

                connectedDevice = btDevices.get(position);
                connectedDevice.createBond();

                btnAddFriend.setVisibility(View.GONE);
                btnConnect.setVisibility(View.VISIBLE);
            }
        });

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] message = "Moshi moshi friend request desu".getBytes(Charset.defaultCharset());
                btConnectionService.write(message);
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConnection();
                btnConnect.setVisibility(View.GONE);
                btnAddFriend.setVisibility(View.VISIBLE);
            }
        });
    }

    private final BroadcastReceiver bondReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d("My logs: ", "Bonded to device " + device.getName());
                    connectedDevice = device;
                    return;
                }
                if(device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d("My logs: ", "Bonding to device " + device.getName());
                    return;
                }
                if(device.getBondState() == BluetoothDevice.BOND_NONE) {

                }
            }
        }
    };

    private void enableDisableBT() {
        if(bluetoothAdapter == null){
            Toast.makeText(this, "Your device does not support bluetooth.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!bluetoothAdapter.isEnabled()){
            Intent intentEnableDisableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intentEnableDisableBluetooth, BLUETOOTH_ENABLE_DISABLE);
            return;
        }
        if(bluetoothAdapter.isEnabled())
            bluetoothAdapter.disable();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case BLUETOOTH_ENABLE_DISABLE:
                if(resultCode == RESULT_OK) {
                    switchDiscoverable.setVisibility(View.VISIBLE);
                    tvDiscoverable.setVisibility(View.VISIBLE);
                    btnDiscover.setVisibility(View.VISIBLE);
                }
                return;
            case DISCOVERABLE_ENABLE_DISABLE:
                if(resultCode == RESULT_OK) {
                    Toast.makeText(this, "You are discoverable to other devices.", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    private void enableDisableDiscoverable() {
        Intent intentEnableDisableDiscoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(intentEnableDisableDiscoverable, DISCOVERABLE_ENABLE_DISABLE);
    }

    private void startStopDiscovery() {
        if(bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            return;
        }

        checkBTPermissions();

        bluetoothAdapter.startDiscovery();

        IntentFilter ifDiscoverDevices = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoverReceaver, ifDiscoverDevices);
    }

    private final BroadcastReceiver discoverReceaver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btDevices.add(device);
                Log.d("My log:", "Device: " + device.getName() + " " + device.getAddress());
                deviceAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, btDevices);
                lvDevices.setAdapter(deviceAdapter);
            }
        }
    };

    public void startBTConnection(BluetoothDevice device, UUID id) {
        btConnectionService.startClient(device, id);
    }

    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if(permissionCheck != 0)
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST);
        }
    }

    private void startConnection() {
        startBTConnection(connectedDevice, insecureUUID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(discoverReceaver);
        unregisterReceiver(bondReceiver);
    }
}