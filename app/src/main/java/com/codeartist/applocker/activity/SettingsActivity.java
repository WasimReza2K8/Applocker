
package com.codeartist.applocker.activity;

import com.codeartist.applocker.R;
import com.codeartist.applocker.utility.Constants;
import com.codeartist.applocker.utility.Preferences;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

/**
 * Created by bjit-16 on 12/29/16.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle(getString(R.string.settings));
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_very_high:
                Log.e("pressed", "very high");
                if (checked)
                    // Pirates are the best
                    Preferences.save(this, Constants.KEY_LOCKER_ACCURACY, 150);
                    break;
            case R.id.radio_high:
                if (checked)
                    // Ninjas rule
                    Preferences.save(this, Constants.KEY_LOCKER_ACCURACY, 200);
                break;

            case R.id.radio_low:
                if (checked)
                    // Pirates are the best
                    Preferences.save(this, Constants.KEY_LOCKER_ACCURACY, 250);
                break;

            case R.id.radio_patternLock:
                if (checked)
                    // Pirates are the best
                    Preferences.save(this, Constants.KEY_LOCKER_TYPE, 1);
                break;

            case R.id.radio_numberLock:
                if (checked)
                    Preferences.save(this, Constants.KEY_LOCKER_TYPE, 2);
                    // Pirates are the best
                    break;
        }

    }
}
