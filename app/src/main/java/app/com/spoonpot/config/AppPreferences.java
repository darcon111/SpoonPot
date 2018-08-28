package app.com.spoonpot.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class AppPreferences {

    private SharedPreferences appPreferences;
    private Editor editor;

    public static final String user = "app.com.spoonpot.user";
    public static final String userImagen = "app.com.spoonpot.imagen";
    public static final String userId = "app.com.spoonpot.userId";
    public static final String firebasetoken="app.com.spoonpot.token";
    public static final String flag="app.com.spoonpot.flag";
    public static final String language="app.com.spoonpot.language";
    public static final String tour = "app.com.spoonpot.tour";
    public static final String sound = "app.com.spoonpot.sound";
    public static final String vibrate = "app.com.spoonpot.vibrate";
    public static final String light = "app.com.spoonpot.light";
    public static final String noti = "app.com.spoonpot.noti";
    public static final String mensajes = "app.com.spoonpot.mensajes";


    //explicativo
    public static final String inicio = "app.com.spoonpot.inicio";
    public static final String favorito = "app.com.spoonpot.favorito";
    public static final String plato = "app.com.spoonpot.plato";
    public static final String mensaje = "app.com.spoonpot.mensaje";
    public static final String perfil = "app.com.spoonpot.perfil";
    public static final String buscador = "app.com.spoonpot.buscador";


    public  String getFirebasetoken() {
        return appPreferences.getString(firebasetoken, "");
    }

    public AppPreferences(Context context) {

        appPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public String getUser() {
        return appPreferences.getString(user, "");
    }

    public String getImagen() {
        return appPreferences.getString(userImagen, "");
    }

    public String getFlag() {
        return appPreferences.getString(flag, "");
    }

    public String getLanguage() {
        return appPreferences.getString(language, "es");
    }

    public String getTour() {
        return appPreferences.getString(tour, "0");
    }

    public String getUserId() {
        return appPreferences.getString(userId, "0");
    }
    public String getSound() {
        return appPreferences.getString(sound, "0");
    }
    public String getVibrate() {
        return appPreferences.getString(vibrate, "0");
    }
    public String getLight() {
        return appPreferences.getString(light, "0");
    }

    public String getNoti()
    {
        return  appPreferences.getString(noti,"0");
    }

    public String getMensajes()
    {
        return  appPreferences.getString(mensajes,"0");
    }

    public void setUser(String mUser) {
        editor = appPreferences.edit();
        editor.putString(user, mUser);
        editor.commit();
    }

    public void setUserId(String mUserId) {
        editor = appPreferences.edit();
        editor.putString(userId, mUserId);
        editor.commit();
    }


    public void setImagen(String mImagen) {
        editor = appPreferences.edit();
        editor.putString(userImagen, mImagen);
        editor.commit();
    }


    public void setFirebasetoken(String token) {
        editor = appPreferences.edit();
        editor.putString(firebasetoken, token);
        editor.commit();
    }

    public void setFlag(String value) {
        editor = appPreferences.edit();
        editor.putString(flag,value);
        editor.commit();
    }

    public void setLanguage(String value) {
        editor = appPreferences.edit();
        editor.putString(language,value);
        editor.commit();
    }

    public void setTour(String value) {
        editor = appPreferences.edit();
        editor.putString(tour,value);
        editor.commit();
    }

    public void setSound(String value) {
        editor = appPreferences.edit();
        editor.putString(sound,value);
        editor.commit();
    }

    public void setVibrate(String value) {
        editor = appPreferences.edit();
        editor.putString(vibrate,value);
        editor.commit();
    }

    public void setLight(String value) {
        editor = appPreferences.edit();
        editor.putString(light,value);
        editor.commit();
    }

    public  void setNoti(int value)
    {
        editor = appPreferences.edit();
        editor.putString(noti,String.valueOf(value));
        editor.commit();
    }

    public  void setMensajes(int value)
    {
        editor = appPreferences.edit();
        editor.putString(mensajes,String.valueOf(value));
        editor.commit();
    }

    //explicativo

    public  void setInicio(int value)
    {
        editor = appPreferences.edit();
        editor.putString(inicio,String.valueOf(value));
        editor.commit();
    }

    public String getInicio()
    {
        return  appPreferences.getString(inicio,"0");
    }

    public  void setFavorito(int value)
    {
        editor = appPreferences.edit();
        editor.putString(favorito,String.valueOf(value));
        editor.commit();
    }

    public String getFavorito()
    {
        return  appPreferences.getString(favorito,"0");
    }


    public  void setPlato(int value)
    {
        editor = appPreferences.edit();
        editor.putString(plato,String.valueOf(value));
        editor.commit();
    }

    public String getPlato()
    {
        return  appPreferences.getString(plato,"0");
    }


    public  void setMensaje(int value)
    {
        editor = appPreferences.edit();
        editor.putString(mensaje,String.valueOf(value));
        editor.commit();
    }

    public String getMensaje()
    {
        return  appPreferences.getString(mensaje,"0");
    }


    public  void setPerfil(int value)
    {
        editor = appPreferences.edit();
        editor.putString(perfil,String.valueOf(value));
        editor.commit();
    }

    public String getPerfil()
    {
        return  appPreferences.getString(perfil,"0");
    }

    public  void setBuscador(int value)
    {
        editor = appPreferences.edit();
        editor.putString(buscador,String.valueOf(value));
        editor.commit();
    }

    public String getBuscador()
    {
        return  appPreferences.getString(buscador,"0");
    }



}
