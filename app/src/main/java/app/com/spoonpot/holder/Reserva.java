package app.com.spoonpot.holder;

/**
 * Created by USUARIO-PC on 05/09/2017.
 */

public class Reserva {

    private String useridOrigen;
    private String useridDestino;
    private String idplato;
    private int calificacion;
    private String id;
    private String aprobar;

    public Reserva() {
    }

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

    public String getIdplato() {
        return idplato;
    }

    public void setIdplato(String idplato) {
        this.idplato = idplato;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAprobar() {
        return aprobar;
    }

    public void setAprobar(String aprobar) {
        this.aprobar = aprobar;
    }

    public Reserva(String useridOrigen, String useridDestino, String idplato, int calificacion, String id, String aprobar) {
        this.useridOrigen = useridOrigen;
        this.useridDestino = useridDestino;
        this.idplato = idplato;
        this.calificacion = calificacion;
        this.id = id;
        this.aprobar = aprobar;
    }
}
