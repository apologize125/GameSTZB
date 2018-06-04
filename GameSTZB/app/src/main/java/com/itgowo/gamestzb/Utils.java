package com.itgowo.gamestzb;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.itgowo.gamestzb.Base.BaseApp;
import com.itgowo.gamestzb.Main.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Utils {
    public static void setupShortcuts() {
        ShortcutManager mShortcutManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            mShortcutManager = BaseApp.app.getSystemService(ShortcutManager.class);
            List<ShortcutInfo> infos = new ArrayList<>();
            for (int i = 0; i < mShortcutManager.getMaxShortcutCountPerActivity(); i++) {
                Intent intent = new Intent(BaseApp.app, MainActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("msg", "我和" + i + "的对话");

                ShortcutInfo info = new ShortcutInfo.Builder(BaseApp.app, "id" + i)
                        .setShortLabel(i + "aaa")
                        .setLongLabel("联系人:" + i)
                        .setIcon(Icon.createWithResource(BaseApp.app, R.mipmap.zhugeliang))
                        .setIntent(intent)
                        .build();
                infos.add(info);
//            manager.addDynamicShortcuts(Arrays.asList(info));
            }

            mShortcutManager.setDynamicShortcuts(infos);
        }
    }

    public static String ReadFile2String(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String temp = reader.readLine();
        return temp;
    }


    public static void checkPermission(Context context, PermissionCallback callback) {
        HiPermission hiPermission = HiPermission.create(context);
        List<PermissionItem> permissionItems = new ArrayList<>();
        permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "外部存储", R.drawable.permission_ic_storage));
        permissionItems.add(new PermissionItem(Manifest.permission.ACCESS_FINE_LOCATION, "定位", R.drawable.permission_ic_location));
        permissionItems.add(new PermissionItem(Manifest.permission.READ_PHONE_STATE, "手机IMEI", R.drawable.permission_ic_phone));
        hiPermission.permissions(permissionItems).msg("为了APP能提供更好的服务，需要以下权限").title("枫林提示")
                .style(R.style.PermissionDefaultNormalStyle).checkMutiPermission(callback);
    }

    public static boolean isWifiConnect() {
        ConnectivityManager connManager = (ConnectivityManager) BaseApp.app.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public static boolean hasNotificationPermission(Context context) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        boolean isOpened = manager.areNotificationsEnabled();
        return isOpened;
    }

    public static void intentDetailsSettingActivity(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static void showToastShort(String msg) {
        if (Looper.myLooper() == null) {
            Looper.prepare();
            Toast.makeText(BaseApp.app, msg, Toast.LENGTH_SHORT);
            Looper.loop();
        } else {
            Toast.makeText(BaseApp.app, msg, Toast.LENGTH_SHORT);
        }

    }
}
