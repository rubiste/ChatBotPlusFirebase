package com.example.chatbotpsp.objects;

import java.util.HashMap;
import java.util.Map;

public class FirebaseMsgData {
    private String cadNoTraducida;
    private String cadTraducida;
    private String horaMinutos;
    private boolean bot;

    public FirebaseMsgData() {
    }

    public FirebaseMsgData(String cadNoTraducida, String cadTraducida, String horaMinutos, boolean bot) {
        this.cadNoTraducida = cadNoTraducida;
        this.cadTraducida = cadTraducida;
        this.horaMinutos = horaMinutos;
        this.bot = bot;
    }

    public String getCadNoTraducida() {
        return cadNoTraducida;
    }

    public void setCadNoTraducida(String cadNoTraducida) {
        this.cadNoTraducida = cadNoTraducida;
    }

    public String getCadTraducida() {
        return cadTraducida;
    }

    public void setCadTraducida(String cadTraducida) {
        this.cadTraducida = cadTraducida;
    }

    public String getHoraMinutos() {
        return horaMinutos;
    }

    public void setHoraMinutos(String horaMinutos) {
        this.horaMinutos = horaMinutos;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("cadNoTraducida", cadNoTraducida);
        result.put("cadTraducida", cadTraducida);
        if (!bot){
            result.put("talker", "User");
        }else{
            result.put("talker", "PandoraBot");
        }
        result.put("horaMinutos", horaMinutos);
        return result;
    }

    @Override
    public String toString() {
        return "FirebaseMsgData{" +
                "cadNoTraducida='" + cadNoTraducida + '\'' +
                ", cadTraducida='" + cadTraducida + '\'' +
                ", horaMinutos='" + horaMinutos + '\'' +
                ", bot=" + bot +
                '}';
    }
}
