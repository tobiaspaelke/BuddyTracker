package com.bernd.buddytracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ProfileSettingsActivity extends ActionBarActivity {
    public final static String propNickname = "nick";

    //public static String exampleNickName1 = "TestNickName";
    //public static String exampleNickName2 = "Horst";
    private static String nickName;
    private String FILENAME = "buddySettings.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        Button btn_save = (Button) findViewById(R.id.btn_save);
        nickName = getAttributes();
        EditText edit_name = (EditText) findViewById(R.id.edit_name);
        edit_name.setText(nickName);
        //Eingegebenen Namen speichern
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit_name = (EditText) findViewById(R.id.edit_name);
                ProfileSettingsActivity.nickName = edit_name.getText().toString();
                Toast.makeText(ProfileSettingsActivity.this, getString(R.string.nameSaved), Toast.LENGTH_SHORT).show();
                saveAttributes(nickName);
            }
        });
        //Bearbeiten abbrechen
        Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileSettingsActivity.this, MainActivity.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*public static String getExampleProfilePicture(Context con) {
        Drawable d = con.getResources().getDrawable(R.drawable.example);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        return Arrays.toString(bitmapdata);
    }*/

    public static String getNickName() {
        return nickName;
    }

    private void saveAttributes(String nick){
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(nick.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAttributes(){
        try {
            StringBuilder builder = new StringBuilder();
            FileInputStream fis = openFileInput(FILENAME);
            int ch;
            while((ch = fis.read()) != -1){
                builder.append((char) ch);
            }
            return builder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
