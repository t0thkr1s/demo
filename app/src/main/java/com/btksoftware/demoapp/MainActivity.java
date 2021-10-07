package com.btksoftware.demoapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.btksoftware.demoapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.btksoftware.demoapp.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        listView = findViewById(R.id.listView);

        list = new ArrayList<>();
        list.add("Flóraszept");
        list.add("Hypo");
        list.add("Higany");
        list.add("Sósav");
        list.add("Szavó");
        list.add("Ajax");
        list.add("Bref");
        list.add("Frosch");
        list.add("Domestos");
        list.add("Cillit");
        list.add("Dymosept");
        list.add("Clorox");


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(MainActivity.this, DetailsActivity.class);
            i.putExtra("item", adapter.getItem(position));
            startActivity(i);
        });

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap image = (Bitmap) Objects.requireNonNull(result.getData()).getExtras().get("data");
                        Log.d("APP", "Processing image...");
                        // process image, result = Flóraszept
                        String s = "Flóraszept";
                        Intent details = new Intent(MainActivity.this, DetailsActivity.class);
                        details.putExtra("item", s);
                        startActivity(details);
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Toast.makeText(MainActivity.this, "Image capture cancelled!", Toast.LENGTH_LONG).show();
                    }
                });

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        binding.fab.setOnClickListener(view -> {
            Intent camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            someActivityResultLauncher.launch(camera);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("APP", "Camera permission granted!");
            } else {
                Log.d("APP", "Camera permission denied!");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem search = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setFocusable(false);
        searchView.setIconifiedByDefault(false);
        searchView.clearFocus();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if (list.contains(query)) {
                    adapter.getFilter().filter(query);
                } else {
                    Toast.makeText(MainActivity.this, "No Match found", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}