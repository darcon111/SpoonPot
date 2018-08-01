package app.com.spoonpot.holder;

/**
 * Created by USUARIO-PC on 05/09/2017.
 */

public class Notificacion {

    private String useridOrigen;
    private String useridDestino;
    private String mensaje;
    private String tipo;
    private String  id;
    private String referencia;
    private int cantidad;

    public String getUseridOrigen() {
        return useridOrigen;
    }

    public void setUseridOrigen(String useridOrigen) {
        this.useridOrigen = useridOrigen;
    }

    public String getUseridDestino() {
        return useridDestino;
    }

    public void setUseridDestino(String useridDestino) {
        this.useridDestino = useridDestino;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Notificacion() {
    }


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Notificacion(String useridOrigen, String useridDestino, String mensaje, String tipo, String id, String referencia, int cantidad) {
        this.useridOrigen = useridOrigen;
        this.useridDestino = useridDestino;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.id = id;
        this.referencia = referencia;
        this.cantidad = cantidad;
    }
}
