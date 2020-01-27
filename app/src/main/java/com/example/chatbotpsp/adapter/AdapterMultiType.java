package com.example.chatbotpsp.adapter;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbotpsp.R;
import com.example.chatbotpsp.objects.FirebaseMsgData;
import com.example.chatbotpsp.objects.Mensaje;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class AdapterMultiType extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int TYPE_USER = 1;
    private static int TYPE_BOT = 2;
    private Context context;
    public ArrayList<Mensaje> mensajes;
    private TextToSpeech voiceEsp;
    private Locale locSpanish = new Locale("spa", "MEX");
    private static final String TAG = "xyz";
    private static final int LONGITUD_SIN_TIEMPO = 10;


    public AdapterMultiType(Context context) {
        this.context = context;
        this.mensajes = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == TYPE_USER) {
            view = LayoutInflater.from(context).inflate(R.layout.item_user, viewGroup, false);
            return new UserViewHolder(view);

        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_bot, viewGroup, false);
            return new BotViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mensajes.get(position).persona) {
            return TYPE_USER;
        } else {
            return TYPE_BOT;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        initVoice();
        int longitud = mensajes.get(position).mensaje.length() - LONGITUD_SIN_TIEMPO;
        final String msgTimeless = mensajes.get(position).mensaje.substring(0, longitud);
        if (getItemViewType(position) == TYPE_USER) {
            ((UserViewHolder) viewHolder).setMensajeDetails(mensajes.get(position));
            ((UserViewHolder) viewHolder).ivItemVolume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    voiceEsp.speak(msgTimeless ,TextToSpeech.QUEUE_FLUSH, null);
                }
            });
        } else {
            ((BotViewHolder) viewHolder).setMensajeDetails(mensajes.get(position));
            ((BotViewHolder) viewHolder).ivItemVolume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    voiceEsp.speak(msgTimeless ,TextToSpeech.QUEUE_FLUSH, null);
                }
            });
        }
    }

    private void initVoice() {
        voiceEsp = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
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
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public void setMensajes(ArrayList<HashMap<String, Object>> mensajes) {
        Mensaje msg;
        if(mensajes != null){
            for (int i = 0; i < mensajes.size(); i++){
                FirebaseMsgData msgData = new FirebaseMsgData();
                msgData.setCadTraducida(mensajes.get(i).get("cadTraducida").toString());
                msgData.setCadNoTraducida(mensajes.get(i).get("cadNoTraducida").toString());
                msgData.setHoraMinutos(mensajes.get(i).get("horaMinutos").toString());
                msgData.setBot(checkBot(mensajes.get(i).get("talker").toString()));

                if(msgData.isBot()){
                    msg = new Mensaje(msgData.getCadTraducida()+"    "+ msgData.getHoraMinutos()+ "  ", false);
                }else{
                    msg = new Mensaje(msgData.getCadNoTraducida()+"    "+ msgData.getHoraMinutos()+ "  ", true);
                }

                this.mensajes.add(msg);
            }
        }

        notifyDataSetChanged();
    }


    private boolean checkBot(String talker) {
        return talker.compareTo("PandoraBot") == 0;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMensaje;
        private ImageView ivItemVolume;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvHumanMessage);
            ivItemVolume = itemView.findViewById(R.id.ivItemVolume);
        }

        void setMensajeDetails(Mensaje mensaje) {
            tvMensaje.setText(mensaje.mensaje);
        }
    }

    class BotViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMensaje;
        private ImageView ivItemVolume;

        BotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvBotMessage);
            ivItemVolume = itemView.findViewById(R.id.ivItemVolume);
        }

        void setMensajeDetails(Mensaje mensaje) {
            tvMensaje.setText(mensaje.mensaje);
        }
    }

}
