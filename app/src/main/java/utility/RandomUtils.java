package utility;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.WindowManager;

/**
 * Created by achelius on 16/01/2017.
 */

public class RandomUtils {

    private static final String TAG = "galileo/main";

    /** Returns the consumer friendly device name */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

//        String phrase = "";
        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
//                phrase += Character.toUpperCase(c);
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
//            phrase += c;
            phrase.append(c);
        }

        return phrase.toString();
    }

    public static WifiInfo setPerformanceIssue(Application context){
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        manager.setWifiEnabled(true);

        WifiManager.WifiLock wifiLock=manager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF,TAG);
        wifiLock.acquire();

        PowerManager powerManager = (PowerManager) context.getSystemService(context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();
        return info;
    }
}
