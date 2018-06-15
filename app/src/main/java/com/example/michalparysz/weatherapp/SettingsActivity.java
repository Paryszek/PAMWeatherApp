package com.example.michalparysz.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {
    @BindView(R.id.latitudeInput)
    TextInputLayout latiduteInput;
    @BindView(R.id.longitudeInput)
    TextInputLayout longitudeInput;
    @BindView(R.id.refreshInput)
    TextInputLayout refreshInput;
    @BindView(R.id.cityInput)
    TextInputLayout cityInput;
    @BindView(R.id.reloadWeatherCheckBox)
    CheckBox reloadWeatherCheckBox;
    @BindView(R.id.cities)
    ListView citiesList;
    @BindView(R.id.imunitsCheckBox)
    CheckBox imunitsCheckBox;

    private String latitude = "0";
    private String longitude = "0";
    private String refreshPeriod = "60000";
    private String currentCity = "Warsaw";
    private String reloadWeather = "false";
    ArrayList<String> citiesArray = new ArrayList<>();
    ArrayAdapter<String> citiesAdapter;
    private String filename = "cities.data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ButterKnife.bind(this);
        initListView();
        checkForValuesFromParentActivity();
        setupToolBar();
        initValues();
    }

    private void initListView() {
        try {
            citiesArray = getCitiesFromFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        citiesAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_activated_1, citiesArray);
        citiesList.setAdapter(citiesAdapter);

        citiesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Objects.requireNonNull(cityInput.getEditText()).setText(citiesArray.get(i));
                cityInput.getEditText().setText(citiesArray.get(i));
            }
        });

    }

    private void checkForValuesFromParentActivity() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                currentCity = extras.getString("currentCity");
                if (noDuplicates(currentCity, citiesArray)) {
                    citiesArray.add(currentCity);
                    citiesAdapter.notifyDataSetChanged();
                    saveCitiesToFile();
                }
                latitude = extras.getString("latitude");
                longitude = extras.getString("longitude");
                if(extras.getBoolean("imUnits", false)) {
                    imunitsCheckBox.setChecked(true);
                } else {
                    imunitsCheckBox.setChecked(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //The key argument here must match that used in the other activity
        }
    }

    private void initValues() {
        try {
            Objects.requireNonNull(latiduteInput.getEditText()).setText(latitude);
            Objects.requireNonNull(longitudeInput.getEditText()).setText(longitude);
            Objects.requireNonNull(refreshInput.getEditText()).setText(refreshPeriod);
            Objects.requireNonNull(cityInput.getEditText()).setText(currentCity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent= new Intent();
                intent.putExtra("currentCity", currentCity);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("refreshPeriod" ,refreshPeriod);
                if (reloadWeatherCheckBox.isChecked()) {
                    intent.putExtra("reloadWeather", true);
                }
                if (imunitsCheckBox.isChecked()) {
                    intent.putExtra("imUnits", true);
                } else {
                    intent.putExtra("imUnits", false);
                }
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @OnClick(R.id.resetList)
    public void OnClickResetList() {
        try {
            citiesArray.clear();
            citiesAdapter.notifyDataSetChanged();
            saveCitiesToFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @OnClick(R.id.zatwierdz)
    public void OnClickZatwierdz() {
        try {
            int latV = Integer.parseInt(Objects.requireNonNull(latiduteInput.getEditText()).getText().toString());
            int longV = Integer.parseInt(Objects.requireNonNull(longitudeInput.getEditText()).getText().toString());
            int refreshV = Integer.parseInt(Objects.requireNonNull(refreshInput.getEditText()).getText().toString());
            String cityName = Objects.requireNonNull(cityInput.getEditText()).getText().toString();
            if (validation(latV, "latidute") && validation(longV, "longitude") && validation(refreshV, "refreshPeriod") && !cityName.endsWith(" ")) {
                latitude = String.valueOf(latV);
                longitude = String.valueOf(longV);
                refreshPeriod = String.valueOf(refreshV);
                currentCity = cityName;
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        android.widget.Toast.makeText(getBaseContext(), "Wrong inputs, please try again", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private boolean noDuplicates(String cityName, ArrayList<String> cities) {
        for (String city : cities) {
            if(city.toLowerCase().equals(cityName.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    private void setupToolBar() {
        setSupportActionBar((Toolbar)findViewById(R.id.settings_bar));
        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).setDisplayHomeAsUpEnabled(true);
    }

    private boolean validation(int value, String type) {
        switch (type) {
            case "latidute":
                if (-90 <= value && value <= 90) return true;
                break;
            case "longitude":
                if (-180 <= value && value <= 180) return true;
                break;
            case "refreshPeriod":
                if (value >= 30000) return true;
                break;
        }
        return false;
    }

    private ArrayList<String> getCitiesFromFile() throws FileNotFoundException, IOException, ClassNotFoundException, EOFException, NullPointerException {
        ArrayList<String> citiesFromFile;
        File file = new File(getBaseContext().getFilesDir(), filename);
        if (!file.exists() || file.length() == 0) {
            if(citiesArray != null) {
                citiesArray.clear();
                citiesArray = new ArrayList<>();
            }
            file.createNewFile();
            citiesArray.add("Warsaw");
            saveCitiesToFile();
        } else {
            citiesArray.add("Warsaw");
            saveCitiesToFile();
        }
        FileInputStream fin = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fin);
        citiesFromFile = (ArrayList<String>)ois.readObject();
        fin.close();
        ois.close();
        return citiesFromFile;
    }

    private void saveCitiesToFile() throws IOException{
        File file = new File(getBaseContext().getFilesDir(), filename);
        FileOutputStream fout = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(citiesArray);
        fout.close();
        oos.close();
    }
}
