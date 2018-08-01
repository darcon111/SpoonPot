package app.com.spoonpot.holder;

/**
 * Created by USUARIO-PC on 11/09/2017.
 */

public class Valoraciones {

    private String userOrigen;
    private String userDestino;
    private String fecha;
    private float valoracionPlato;
    private float valoracionCocinero;
    private String comentarioPlato;
    private String comentarioCocinero;
    private int tipo;
    private String id;


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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public float getValoracionPlato() {
        return valoracionPlato;
    }

    public void setValoracionPlato(float valoracionPlato) {
        this.valoracionPlato = valoracionPlato;
    }

    public float getValoracionCocinero() {
        return valoracionCocinero;
    }

    public void setValoracionCocinero(float valoracionCocinero) {
        this.valoracionCocinero = valoracionCocinero;
    }

    public String getComentarioPlato() {
        return comentarioPlato;
    }

    public void setComentarioPlato(String comentarioPlato) {
        this.comentarioPlato = comentarioPlato;
    }

    public String getComentarioCocinero() {
        return comentarioCocinero;
    }

    public void setComentarioCocinero(String comentarioCocinero) {
        this.comentarioCocinero = comentarioCocinero;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public Valoraciones() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Valoraciones(String userOrigen, String userDestino, String fecha, float valoracionPlato, float valoracionCocinero, String comentarioPlato, String comentarioCocinero, int tipo, String id) {
        this.userOrigen = userOrigen;
        this.userDestino = userDestino;
        this.fecha = fecha;
        this.valoracionPlato = valoracionPlato;
        this.valoracionCocinero = valoracionCocinero;
        this.comentarioPlato = comentarioPlato;
        this.comentarioCocinero = comentarioCocinero;
        this.tipo = tipo;
        this.id = id;
    }
}
