package elfakrs.mosis.vitaminc.keepitclean.data.local_data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.Semaphore;

public class SharedPrefManager {
    public static String SHARED_PREFERENCES_KEY = "KIC_UserShared";
    public static String USERNAME_KEY = "KIC_User_Username";

    private Semaphore readLock = new Semaphore(1);
    private Semaphore writeLock = new Semaphore(1);

    private static SharedPrefManager instance;

    private SharedPrefManager() {}

    public static SharedPrefManager getInstance() {
        if(instance == null)
            instance = new SharedPrefManager();
        return instance;
    }

    public void saveUsername(Context context, String username) throws Exception {
        writeLock.acquire();
        readLock.acquire();

        SharedPreferences shared = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();

        editor.putString(USERNAME_KEY, username);

        editor.apply();

        readLock.release();
        writeLock.release();
    }

    public String readUsername(Context context) throws Exception {
        readLock.acquire();
        SharedPreferences shared = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        String username = shared.getString(USERNAME_KEY, "");
        readLock.release();

        return username;
    }
}
