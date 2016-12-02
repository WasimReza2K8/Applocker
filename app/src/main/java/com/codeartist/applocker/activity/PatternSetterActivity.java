
package com.codeartist.applocker.activity;

import com.codeartist.applocker.R;
import com.codeartist.applocker.utility.Constants;
import com.codeartist.applocker.utility.Preferences;
import com.eftimoff.patternview.PatternView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by bjit-16 on 12/2/16.
 */

public class PatternSetterActivity extends AppCompatActivity {
    //private String pattern1, pattern2;
    private String patternString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_lock_layout);
        final String packageName = getIntent().getStringExtra(Constants.KEY_PKG_NAME);
        final PatternView materialLockView = (PatternView) findViewById(R.id.pattern);
       /* materialLockView.setOnPatternListener(new MaterialLockView.OnPatternListener() {
            @Override
            public void onPatternDetected(List<MaterialLockView.Cell> pattern,
                    String SimplePattern) {
                Preferences.save(getApplicationContext(),
                        Preferences.KEY_APP_LOCKER_PASSWORD, SimplePattern);
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.KEY_PKG_NAME, packageName);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                super.onPatternDetected(pattern, SimplePattern);
            }
        });*/

        materialLockView.setOnPatternDetectedListener(new PatternView.OnPatternDetectedListener() {

            @Override
            public void onPatternDetected() {
                if (patternString == null) {
                    patternString = materialLockView.getPatternString();
                    materialLockView.clearPattern();
                    return;
                }
                if (patternString.equals(materialLockView.getPatternString())) {
                    //Toast.makeText(getApplicationContext(), "PATTERN CORRECT", Toast.LENGTH_SHORT).show();
                    Preferences.save(getApplicationContext(),
                            Preferences.KEY_APP_LOCKER_PASSWORD, patternString);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(Constants.KEY_PKG_NAME, packageName);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                    return;
                }
                Toast.makeText(getApplicationContext(), "PATTERN NOT CORRECT", Toast.LENGTH_SHORT).show();
//                patternView.clearPattern();
            }
        });
    }
}
