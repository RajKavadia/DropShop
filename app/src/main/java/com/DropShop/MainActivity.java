package com.DropShop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    boolean isfirsttime= false;
    ProductRecyclerview productRecyclerview ;
    Button materialButton;
    RecyclerView recyclerView;
    ArrayList<Product> list = new ArrayList<>();
    Map<String, Product> jsonObject= new HashMap<>();
    ArrayList<String> idarray = new ArrayList<>();
    DatabaseReference productData;
    @SuppressLint({"NewApi", "LocalSuppress"}) BufferedReader bufferedReader = null;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndRequestPermissions();
        findviews();
        productData= FirebaseDatabase.getInstance().getReference("productData");
        productData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null){
                    Toast.makeText(MainActivity.this, "null", Toast.LENGTH_SHORT).show();
                    String response = "";
                    try {
                        bufferedReader = new BufferedReader(new InputStreamReader( getAssets().open("test.json"), StandardCharsets.UTF_8));
                        for (String line; (line = bufferedReader.readLine()) != null; response += line);
                        Type mapOfStaff = new TypeToken<Map<String, Product>>() {}.getType();
                        jsonObject = new Gson().fromJson(response, mapOfStaff);
                        list = new ArrayList<Product>(jsonObject.values());
                        idarray = new ArrayList<>(jsonObject.keySet());
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    for(int i = 0 ; i<list.size();i++){
                        String id =idarray.get(i);
                        productData.child(id).setValue(list.get(i));
                    }
                    setuprecyclerview(recyclerView,new ArrayList<>(jsonObject.values()));
                }
                else {
                    Toast.makeText(MainActivity.this, "not null", Toast.LENGTH_SHORT).show();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Product product = postSnapshot.getValue(Product.class);
                        jsonObject.put(postSnapshot.getKey(), product);
                        list = new ArrayList<Product>(jsonObject.values());
                        idarray = new ArrayList<>(jsonObject.keySet());
                        setuprecyclerview(recyclerView, new ArrayList<>(jsonObject.values()));

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

        if(list.size()==0){

                fisrsttimer();
        }
        else {
            Toast.makeText(this, "Not Firsttime", Toast.LENGTH_SHORT).show();
        }
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             openfile();
            }
        });


    }

    private void openfile() {
        if(checkAndRequestPermissions()){
            new ChooserDialog(MainActivity.this)
                    .withFilter(false,true,"JSON")
                    .withChosenListener(new ChooserDialog.Result() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onChoosePath(String path, File pathFile) {
                            if(takeLast(path, 4).equalsIgnoreCase("json")){
                                try {
                                    bufferedReader = new BufferedReader(new FileReader(pathFile));
                                    String response= "";
                                    for (String line; (line = bufferedReader.readLine()) != null; response += line);
                                    Type mapOfStaff = new TypeToken<Map<String, Product>>() {}.getType();
                                    Map<String,Product> mapfromfile = new Gson().fromJson(response, mapOfStaff);
                                    List<String> list = new ArrayList<>(mapfromfile.keySet());
                                    Log.d("TAG",""+list.get(0));
                                    for(int i = 0;i<list.size();i++){
                                        Log.d("TAG", " "+i);
                                        if(!idarray.contains(list.get(i))){
                                            Product product = mapfromfile.get(list.get(i));
                                            jsonObject.put(list.get(i),product);
                                            productData.child(list.get(i)).setValue(product);
                                        }
                                    }
                                    setuprecyclerview(recyclerView, new ArrayList<>(jsonObject.values()));

                                }
                                catch (IOException | JsonSyntaxException |NullPointerException e) {
                                    e.printStackTrace();

                                }
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Please Select Valid File name", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .withOnCancelListener(new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            dialog.cancel(); // MUST have
                        }
                    })
                    .build()
                    .show();
        }
        else {
            Toast.makeText(MainActivity.this, "Please enable Permission", Toast.LENGTH_SHORT).show();

        }

    }
    public String getdatafromfile(File file){
        BufferedReader br = null;
        StringBuilder text = new StringBuilder();
        try {

             br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                Log.i("Test", "text : "+text+" : end");
                text.append('\n');
            } }
        catch (IOException e) {
            e.printStackTrace();

        }
        finally{
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return text.toString();


    }


    public static String takeLast(String value, int count) {
        if (value == null || value.trim().length() == 0 || count < 1) {
            return "";
        }

        if (value.length() > count) {
            return value.substring(value.length() - count);
        } else {
            return value;
        }
    }

    private Map<String,Product> getdatafromfirebase(DatabaseReference productData) {
        final Map<String,Product> products = new HashMap<>();
        productData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    products.put(postSnapshot.getKey(),product);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return products;
    }

    private void findviews() {
        recyclerView = findViewById(R.id.recyclerview);
        materialButton = findViewById(R.id.fab);
    }

    private void setuprecyclerview(RecyclerView recyclerView,List<Product> list) {
        productRecyclerview= new ProductRecyclerview(list,MainActivity.this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(productRecyclerview);
        productRecyclerview.notifyDataSetChanged();
    }

    private void fisrsttimer() {
        isfirsttime= false;
        getSharedPreferences("isfirsttime",0).edit().putBoolean("isfirsttime",isfirsttime).apply();
    }

    private boolean checkAndRequestPermissions() {

        int permissionWriteStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionReadStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionReadStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0 || grantResults == null) {
            /*If result is null*/
        } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            /*If We accept permission*/
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            /*If We Decline permission*/
        }
    }
}
