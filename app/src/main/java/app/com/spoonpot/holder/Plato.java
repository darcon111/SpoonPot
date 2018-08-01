package app.com.spoonpot.holder;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by darioalarcon on 15/4/17.
 */
@IgnoreExtraProperties
public class Plato {

    private String id;
    private String tipo;
    private String title;
    private String desc;
    private String costo;
    private String raciones;
    private String ofrezco;
    private String hora;
    private String dia;
    private String opcional;
    private String image;
    private String user;
    private String username;
    private int like;
    private float lag;
    private float log;
    private int favorito;
    private float distancia;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }

    public String getRaciones() {
        return raciones;
    }

    public void setRaciones(String raciones) {
        this.raciones = raciones;
    }

    public String getOfrezco() {
        return ofrezco;
    }

    public void setOfrezco(String ofrezco) {
        this.ofrezco = ofrezco;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }


    public String getOpcional() {
        return opcional;
    }

    public void setOpcional(String opcional) {
        this.opcional = opcional;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getLag() {
        return lag;
    }

    public void setLag(float lag) {
        this.lag = lag;
    }

    public float getLog() {
        return log;
    }

    public void setLog(float log) {
        this.log = log;
    }

    public int getFavorito() {
        return favorito;
    }

    public void setFavorito(int favorito) {
        this.favorito = favorito;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("tipo", tipo);
        result.put("title", title);
        result.put("desc", desc);
        result.put("costo", costo);
        result.put("raciones", raciones);
        result.put("ofrezco", ofrezco);
        result.put("hora", hora);
        result.put("dia", dia);
        result.put("opcional", opcional);
        result.put("image", image);
        result.put("lag", lag);
        result.put("log", log);
        result.put("favorito", favorito);
        result.put("user",user);
        result.put("username",username);


        return result;
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }
}
