package com.example.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private String info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button fab = (Button)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareInfo();
            }
        });

        Map<String, Object> constants = getConstants();
        this.info = toJSON(constants);

        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(info);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

    private String toJSON(Map<String, Object> constants) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        return gson.toJson(constants);
    }

    private Map<String, Object> getConstants() {
        Field[] declaredFields = Build.class.getDeclaredFields();
        Map<String, Object> constants = new HashMap<>();
        for (Field field : declaredFields) {
            if (Modifier.isStatic(field.getModifiers())) {
                String name = field.getName();
                Object value;
                try {
                    value = field.get(Build.class);

                } catch (IllegalAccessException e) {
                    value = e.toString();
                }
                constants.put(name, value);
            }
        }
        return constants;
    }

    private void shareInfo() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, info);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}