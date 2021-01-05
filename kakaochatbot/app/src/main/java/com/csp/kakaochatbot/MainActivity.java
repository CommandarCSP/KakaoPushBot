package com.csp.kakaochatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.content.Intent;
import android.os.Bundle;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isPermissionAllowed = isNotiPermissionAllowed();

        if(!isPermissionAllowed) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }
    }

    private boolean isNotiPermissionAllowed() {
        Set<String> notiListenerSet = NotificationManagerCompat.getEnabledListenerPackages(this);
        String myPackageName = getPackageName();

        for(String packageName : notiListenerSet) {
            if(packageName == null) {
                continue;
            }
            if(packageName.equals(myPackageName)) {
                return true;
            }
        }

        return false;
    }
}