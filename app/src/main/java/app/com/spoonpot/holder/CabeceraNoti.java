package app.com.spoonpot.holder;

/**
 * Created by USUARIO-PC on 29/08/2017.
 */

public class CabeceraNoti {

    private String userorigen;
    private String nombreorigen;
    private String nombredestino;
    private String userdestino;
    private String fecha;
    private String mensaje;

    public String getUserorigen() {
        return userorigen;
    }

    public void setUserorigen(String userorigen) {
        this.userorigen = userorigen;
    }

    public String getUserdestino() {
        return userdestino;
    }

    public void setUserdestino(String userdestino) {
        this.userdestino = userdestino;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombreorigen() {
        return nombreorigen;
    }

    public void setNombreorigen(String nombreorigen) {
        this.nombreorigen = nombreorigen;
    }

    public String getNombredestino() {
        return nombredestino;
    }

    public void setNombredestino(String nombredestino) {
        this.nombredestino = nombredestino;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public CabeceraNoti() {
    }

    public CabeceraNoti(String userorigen, String nombreorigen, String nombredestino, String userdestino, String fecha, String mensaje) {
        this.userorigen = userorigen;
        this.nombreorigen = nombreorigen;
        this.nombredestino = nombredestino;
        this.userdestino = userdestino;
        this.fecha = fecha;
        this.mensaje = mensaje;
    }
}
