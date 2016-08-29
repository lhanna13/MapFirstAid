package com.example.lhila.mapfirstaid;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * Emergency Info class stores users personal emergency contact information.
 * Created by Lauren Hiland on 5/5/2016.
 */
public class EmergencyInfo extends Activity {

    private final static String TEXT="storetext.txt";
    private EditText txtEditor;

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergencyinfolayout);
        txtEditor=(EditText)findViewById(R.id.textbox);

        readFileInEditor();

    }

    public void saveClicked(View v) {

        try {

            OutputStreamWriter out=

                    new OutputStreamWriter(openFileOutput(TEXT, 0));

            out.write(txtEditor.getText().toString());

            out.close();

            Toast

                    .makeText(this, "Emergency Contact Information Saved", Toast.LENGTH_LONG)

                    .show();

        }

        catch (Throwable t) {

            Toast

                    .makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG)

                    .show();

        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void readFileInEditor()

    {

        try {

            InputStream in = openFileInput(TEXT);

            if (in != null) {

                InputStreamReader tmp=new InputStreamReader(in);

                BufferedReader reader=new BufferedReader(tmp);

                String str;

                StringBuilder buf=new StringBuilder();

                while ((str = reader.readLine()) != null) {

                    buf.append(str+" " + System.lineSeparator());
                    // System.out.println();

                }

                in.close();

                txtEditor.setText(buf.toString());

            }
        }

        catch (java.io.FileNotFoundException e) {
            Toast
                    .makeText(this, "Exception: " + e.toString(), Toast.LENGTH_LONG)
                    .show();
        }

        catch (Throwable t) {
            Toast
                    .makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG)
                    .show();

        }

    }

/*
When pressed returns user to home page, class MapsActivity.
 */
    public void GoHome(View view) {
        String button_text;
        button_text = ((Button) view).getText().toString();
        if (button_text.equals("Return Home")) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
    }

    /*
When pressed returns user to WebMD.
 */
    public void GoToWebMD(View view) {
        String button_text;
        button_text = ((Button) view).getText().toString();
        if (button_text.equals("Go to WebMD")) {
            Intent intent = new Intent(this, WebViewClass.class);
            startActivity(intent);
        }
    }
}
