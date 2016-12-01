
package com.codeartist.applocker.activity;

import com.codeartist.applocker.utility.Constants;
import com.codeartist.applocker.utility.Preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import codeartist.applocker.R;

/**
 * Created by bjit-16 on 11/23/16.
 */

public class PasswordSetterActivity extends AppCompatActivity {
    private String password1, password2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_setter);
        final EditText password = (EditText) findViewById(R.id.editText_password);
        final TextView setPassword = (TextView) findViewById(R.id.textView_setPassword);
        Button check = (Button) findViewById(R.id.button_check);
        final String packageName = getIntent().getStringExtra(Constants.KEY_PKG_NAME);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = password.getEditableText().toString();
                if (temp == null) {
                    Toast.makeText(PasswordSetterActivity.this, "Please set the password.",
                            Toast.LENGTH_LONG).show();
                } else if (password1 == null) {
                    password1 = temp;
                    password.setText("");
                    setPassword.setText("Confirm the Password");
                } else if (password2 == null) {
                    password2 = temp;
                    if (password1.equals(password2)) {
                        Preferences.save(getApplicationContext(),
                                Preferences.KEY_APP_LOCKER_PASSWORD, password1);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(Constants.KEY_PKG_NAME, packageName);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(PasswordSetterActivity.this, "Wrong Password given.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
