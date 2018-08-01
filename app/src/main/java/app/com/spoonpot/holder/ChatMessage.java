package app.com.spoonpot.holder;

import java.util.Date;

/**
 * Created by USUARIO-PC on 29/08/2017.
 */

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private String messageTime;
    private String useridOrigen;
    private String useridDestino;
    private int leido;
    private int orden;

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
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

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public ChatMessage(String messageText, String messageUser, String useridOrigen, String useridDestino) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.useridOrigen = useridOrigen;
        this.useridDestino = useridDestino;
        this.messageTime  = String.valueOf(new Date().getTime());
    }

    public ChatMessage(String messageText, String messageUser, String messageTime, String useridOrigen, String useridDestino) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageTime = messageTime;
        this.useridOrigen = useridOrigen;
        this.useridDestino = useridDestino;
    }

    public ChatMessage() {
    }

    public int getLeido() {
        return leido;
    }

    public void setLeido(int leido) {
        this.leido = leido;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
}
