package com.pishtaz.mylastnews.ui.fragments.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pishtaz.mylastnews.MainActivity;
import com.pishtaz.mylastnews.R;


public class AlertActivateByEmail extends Activity {


    TextView textViewConfirmActivation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alert_activate_by_email);

        setFinishOnTouchOutside(false);

        textViewConfirmActivation = (TextView) findViewById(R.id.textViewConfirmActivation);
        textViewConfirmActivation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AlertActivateByEmail.this, MainActivity.class));
                //   AlertActivateByEmail.this.finish();
            }
        });
    }


}
