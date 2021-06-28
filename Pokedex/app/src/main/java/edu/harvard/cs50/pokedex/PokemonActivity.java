package edu.harvard.cs50.pokedex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


public class PokemonActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView numberTextView;
    private TextView description;
    private List<TextView> pokemonTypes;
    private List<ImageView> sprites;
    private RequestQueue requestQueue;
    private Button catchButton;
    private Boolean caught;
    private String pokemonName;
    private int currentSprite;
    private static final String baseURL = "https://pokeapi.co/api/v2/";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        pokemonName = getIntent().getStringExtra("name");

        currentSprite = 0;

        findUIWidgets();
        initializeTextWidgets();

        loadCaughtState();
        toggleCatch();
        load();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCaughtState();
        toggleCatch();
        Log.d("On resume Pokemon", String.valueOf(caught));
    }

    public void load() {
        String genericInformationURL = baseURL + "pokemon/" + pokemonName.toLowerCase();
        String descriptionURL = baseURL + "pokemon-species/" + pokemonName.toLowerCase();





        JsonObjectRequest requestGenericInformation = new JsonObjectRequest(Request.Method.GET,
                genericInformationURL, null, genericInformationResponse(), errorRequest());

        JsonObjectRequest requestDescription = new JsonObjectRequest(Request.Method.GET,
                descriptionURL, null, descriptionResponse(), errorRequest());

        requestQueue.add(requestGenericInformation);
        requestQueue.add(requestDescription);
    }


    @SuppressLint("CommitPrefEdits")
    public void catchPokemon(View vew) {
        caught = !caught;
        toggleCatch();
        saveCaughtState();
    }

    private void toggleCatch() {
        if (caught)
            catchButton.setText(R.string.catch_text);
        else
            catchButton.setText(R.string.release_text);
    }

    private void saveCaughtState() {
        getPreferences(Context.MODE_PRIVATE).edit().putBoolean(pokemonName, caught).commit();
        Log.d("Saving Caught", String.valueOf(caught));
        loadCaughtState();
    }

    private void loadCaughtState() {
        caught = getPreferences(Context.MODE_PRIVATE).getBoolean(pokemonName, true);
        Log.d("Loading Caught", String.valueOf(caught));
    }

    private void findUIWidgets() {
        pokemonTypes = Arrays.asList(findViewById(R.id.pokemon_type1), findViewById(R.id.pokemon_type2));
        sprites = Arrays.asList(findViewById(R.id.frontDefault), findViewById(R.id.backDefault));
        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);
        description = findViewById(R.id.pokemon_description);
        catchButton = findViewById(R.id.catchButton);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializeTextWidgets() {
        nameTextView.setText(pokemonName);
        pokemonTypes.forEach(type -> type.setText(""));
        catchButton.setText(R.string.loading_text);
    }


    private Response.Listener<JSONObject> genericInformationResponse() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {



                    JSONObject sprites = response.getJSONObject("sprites");
                    JSONArray types = response.getJSONArray("types");

                    @SuppressLint("DefaultLocale") String index = String.format("#%03d", response.getInt("id"));
                    String frontDefault = sprites.getString("front_default");
                    String backDefault = sprites.getString("back_default");


                    requestSprite(frontDefault);
                    requestSprite(backDefault);
                    numberTextView.setText(index);
                    nameTextView.setText(pokemonName);


                    for (int i = 0; i < types.length(); i++) {
                        JSONObject typeEntry = types.getJSONObject(i);
                        String type = typeEntry.getJSONObject("type").getString("name");

                        pokemonTypes.get(i).setText(type);

                    }
                } catch (JSONException e) {
                    Log.e("cs50", "Pokemon json error", e);
                    Log.e("cs50", response.toString());
                }
            }
        };
    }

    private void requestSprite(String spriteUrl) {
        new DownloadSpriteTask().execute(spriteUrl);
    }

    Response.ErrorListener errorRequest() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon details error", error);
            }
        };
    }

    private class DownloadSpriteTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                return BitmapFactory.decodeStream(url.openStream());
            } catch (IOException e) {
                Log.e("cs50", "Download sprite error", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // load the bitmap into the ImageView!
            sprites.get(currentSprite).setImageBitmap(bitmap);
            currentSprite++;
        }
    }


    private Response.Listener<JSONObject> descriptionResponse() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray flavorTextEntries = response.getJSONArray("flavor_text_entries");
                    JSONObject firstItem = flavorTextEntries.getJSONObject(0);
                    String flavorText = firstItem.getString("flavor_text");
                    String sanitizedText = flavorText.replaceAll("[^a-zA-Z0-9\\\\s+.]"," ");
                    Log.d("flavorText",sanitizedText);
                    description.setText(sanitizedText);

                } catch (JSONException e) {
                    Log.e("cs50", "Pokemon json error", e);
                }
            }
        };
    }

}
