package com.example.taek.seekbardialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_button:
                SmartKeyWeTimeChoiceDialog dialog = new SmartKeyWeTimeChoiceDialog(this, null, null, "Opening time");
                dialog.show();
                break;
        }
    }
}
