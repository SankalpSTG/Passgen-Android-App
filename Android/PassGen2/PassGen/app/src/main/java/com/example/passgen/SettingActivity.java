package com.example.passgen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_setting);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainSettingsFragement()).commit();

    }

    public static class MainSettingsFragement extends PreferenceFragment {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

            //sendfeedback listner
            Preference myPrefFeedback = findPreference("key_send_feedback");
            myPrefFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });

        }

        private void sendFeedback(Context context) {
            String body = null;
            try {
                body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
                body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                        Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                        "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
            } catch (PackageManager.NameNotFoundException e) {
            }
            Intent emailIntent= new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","passgen@gmail.com", null));
            //intent.setType("message/rfc822");
            //intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@androidhive.info"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Query from android app");
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
            context.startActivity(Intent.createChooser(emailIntent,"Sending mail"));

        }
    }

}
