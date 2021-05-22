package com.moko.lw001.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.moko.lw001.BuildConfig;
import com.moko.lw001.R;
import com.moko.lw001.R2;
import com.moko.lw001.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AboutActivity extends BaseActivity {
    @BindView(R2.id.app_version)
    TextView appVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw001_activity_about);
        ButterKnife.bind(this);
        if (!BuildConfig.IS_LIBRARY) {
            appVersion.setText(String.format("APP Version:V%s", Utils.getVersionInfo(this)));
        }
    }

    public void onBack(View view) {
        finish();
    }

    public void onCompanyWebsite(View view) {
        if (isWindowLocked())
            return;
        Uri uri = Uri.parse("https://" + getString(R.string.company_website));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
