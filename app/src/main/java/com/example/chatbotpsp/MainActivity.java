package com.example.chatbotpsp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatbotpsp.API.ChatterBot;
import com.example.chatbotpsp.API.ChatterBotFactory;
import com.example.chatbotpsp.API.ChatterBotSession;
import com.example.chatbotpsp.API.ChatterBotType;
import com.example.chatbotpsp.API.TextToSpeechActivity;
import com.example.chatbotpsp.API.Utils;
import com.example.chatbotpsp.adapter.AdapterMultiType;
import com.example.chatbotpsp.objects.FirebaseMsgData;
import com.example.chatbotpsp.objects.Mensaje;
import com.example.chatbotpsp.objects.MyFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Firebase
    private MyFirebase myFirebase;

    //Vistas
    private RecyclerView rvMessages;
    private Button btEnviar;
    private EditText etInput;
    private ImageView ivMic, ivVolume;
    private AdapterMultiType adapter;
    
    //Bot Stuff
    private ChatterBot bot;
    private ChatterBotSession botSession;
    private String cadNoTraducida = "", traduccion = "", horaMinuto="00:00:00", currentDay ="";
    private TextToSpeech voiceEsp;
    private Locale locSpanish = new Locale("spa", "MEX");

    //Variables FINALES
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private static final String TAG = "xyz";

    //Others
    private boolean volume = true, descargados = false;
    private ArrayList<Mensaje> conversacion = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFirebase();
        initComponents();
        initBot();
    }

    private void initFirebase() {
        myFirebase = new MyFirebase();
    }

    private void initComponents() {
        voiceEsp = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = voiceEsp.setLanguage(locSpanish);
                    if(result ==TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e(TAG , "Language Error");
                    }else{
                        Log.d(TAG, "ALL GOOD");
                    }
                }else{
                    Log.e(TAG , "Language Error");
                }
            }
        });

        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        btEnviar = findViewById(R.id.btSend);
        etInput = findViewById(R.id.etInput);
        ivMic = findViewById(R.id.ivMic);
        ivVolume = findViewById(R.id.ivVolume);

        rvMessages = findViewById(R.id.recyclerView);
        adapter = new AdapterMultiType(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(adapter);

        initEvents();
    }

    private void initEvents() {
        btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkEmptyMsg()){
                    cadNoTraducida = etInput.getText().toString();
                    TraduccirAIngles translateTask = new TraduccirAIngles(cadNoTraducida);
                    getActualTime();
                    adapter.mensajes.add(new Mensaje(etInput.getText().toString() + "    " + horaMinuto + "  ", true));
                    adapter.notifyDataSetChanged();
                    rvMessages.scrollToPosition(adapter.mensajes.size() - 1);
                    etInput.setText("");
                    translateTask.execute();
                }
            }
        });

        ivMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceEntry();
            }
        });
        ivVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVolume();
            }
        });

        checkTodayConversation();
    }

    //Comprobará si ya existe una conversacion este dia, si es asi la pintara
    private void checkTodayConversation() {
        myFirebase.setDatabaseReferenceFecha(myFirebase.getFecha());
        myFirebase.getDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!descargados){
                    ArrayList<HashMap<String, Object>> msgArray;
                    msgArray = myFirebase.getData(dataSnapshot);
                    adapter.setMensajes(msgArray);
                    descargados = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myFirebase = new MyFirebase();
    }

    private void changeVolume() {
        if(volume) {
            volume = false;
            ivVolume.setImageResource(R.drawable.volume_off24dp);
        }else{
            volume = true;
            ivVolume.setImageResource(R.drawable.volume_up24dp);
        }
    }

    private boolean checkEmptyMsg() {
        boolean empty = false;
        if(etInput.getText().toString().length() == 0){
            Toast.makeText(this, getResources().getText(R.string.emptyMsg), Toast.LENGTH_SHORT).show();
            empty = true;
        }
        return empty;
    }

    private void getActualTime() {
        Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        horaMinuto = sdf.format(new Date());
    }

    private void voiceEntry() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locSpanish);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getText(R.string.hableAlBot));
        try{
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }catch (ActivityNotFoundException e){
            e.getMessage();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQ_CODE_SPEECH_INPUT:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etInput.setText(result.get(0));
                }
            break;
        }
    }

    private void initBot() {
        ChatterBotFactory factory = new ChatterBotFactory();

        try {
            bot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
        } catch (Exception e) {
            e.printStackTrace();
        }

        botSession = bot.createSession();
    }

    // POST
    // https://www.bing.com/ttranslatev3

    // HEADERS
    // HEADER NAME: Content-type / application/x-www-form-urlencoded
    // User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36

    // BODY
    // fromLang=es
    // text=Hola
    // to=en

    public void doTheChat(){
        saveFirebaseMsg(false);
        new Chat().execute();
    }

    private void saveFirebaseMsg(boolean bot) {
        FirebaseMsgData msgData = new FirebaseMsgData(cadNoTraducida, traduccion, horaMinuto, bot);
        myFirebase.saveInFirebase(msgData);
    }

    private void showBotResponse(){
        adapter.mensajes.add(new Mensaje(traduccion+ "    " + horaMinuto + "  ", false));
        adapter.notifyDataSetChanged();
        if(volume){
            voiceEsp.speak(traduccion, TextToSpeech.QUEUE_FLUSH, null);
        }
        rvMessages.scrollToPosition(adapter.mensajes.size() - 1);
        saveFirebaseMsg(true);
    }

    private void chat(String msg) {
        try {
            cadNoTraducida = botSession.think(msg);
            new TraduccirAEsp(cadNoTraducida).execute();
        }catch(Exception e){
            Log.v(TAG, "Error: " + e.getMessage());
        }
    }

    public String decomposeJson(String json){
        String translationResult = "Could not get";
        try {
            JSONArray arr = new JSONArray(json);
            JSONObject jObj = arr.getJSONObject(0);
            translationResult = jObj.getString("translations");
            JSONArray arr2 = new JSONArray(translationResult);
            JSONObject jObj2 = arr2.getJSONObject(0);
            translationResult = jObj2.getString("text");
        } catch (JSONException e) {
            translationResult = e.getLocalizedMessage();
        }
        return translationResult;
    }

    private class Chat extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            chat(traduccion);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            rvMessages.scrollToPosition(adapter.mensajes.size() - 1);
        }
    }

    private class TraduccirAIngles extends AsyncTask<Void, Void, Void>{

        private final Map<String, String> headers;
        private final Map<String, String> vars;
        String s = "Error";

        private TraduccirAIngles(String message) {
            headers = new LinkedHashMap<String, String>();
            headers.put("Content-type","application/x-www-form-urlencoded");
            headers.put("User-Agent:","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36");

            vars = new HashMap<String, String>();
            vars.put("fromLang", "es");
            vars.put("text",message);
            vars.put("to","en");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                s = Utils.performPostCall("https://www.bing.com/ttranslatev3", (HashMap) vars);
            } catch (Exception e) {
                e.printStackTrace();
                Log.v(TAG, "Error: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            traduccion = decomposeJson(s);
            doTheChat();
        }
    }

    private class TraduccirAEsp extends AsyncTask<Void, Void, Void>{

        private final Map<String, String> headers;
        private final Map<String, String> vars;
        String s = "Error";

        private TraduccirAEsp(String message) {
            headers = new LinkedHashMap<String, String>();
            headers.put("Content-type","application/x-www-form-urlencoded");
            headers.put("User-Agent:","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36");

            vars = new HashMap<String, String>();
            vars.put("fromLang", "en");
            vars.put("text",message);
            vars.put("to","es");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                s = Utils.performPostCall("https://www.bing.com/ttranslatev3", (HashMap) vars);
            } catch (Exception e) {
                e.printStackTrace();
                Log.v(TAG, "Error: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            traduccion = decomposeJson(s);
            showBotResponse();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bot_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.iCerrar:
                finish();
                return true;
            case R.id.iHistorial:
                Intent i = new Intent(this, HistorialActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){}
}

