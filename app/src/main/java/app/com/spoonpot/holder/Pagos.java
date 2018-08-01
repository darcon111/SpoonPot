package app.com.spoonpot.holder;

/**
 * Created by USUARIO-PC on 19/09/2017.
 */

public class Pagos {

    private String userOrigen;
    private String userDestino;
    private String mensaje;
    private String id;
    private String referencia_valor;

    public String getUserOrigen() {
        return userOrigen;
    }

    public void setUserOrigen(String userOrigen) {
        this.userOrigen = userOrigen;
    }

    public String getUserDestino() {
        return userDestino;
    }

    public void setUserDestino(String userDestino) {
        this.userDestino = userDestino;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferencia_valor() {
        return referencia_valor;
    }

    public void setReferencia_valor(String referencia_valor) {
        this.referencia_valor = referencia_valor;
    }

    public Pagos(String userOrigen, String userDestino, String mensaje, String id, String referencia_valor) {
        this.userOrigen = userOrigen;
        this.userDestino = userDestino;
        this.mensaje = mensaje;
        this.id = id;
        this.referencia_valor = referencia_valor;
    }

    public Pagos() {
    }
}
