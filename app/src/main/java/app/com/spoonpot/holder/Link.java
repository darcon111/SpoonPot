package app.com.spoonpot.holder;

/**
 * Created by USUARIO-PC on 31/10/2017.
 */

public class Link {

    private String url;
    private int mobile;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMobile() {
        return mobile;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    public Link(String url, int mobile) {
        this.url = url;
        this.mobile = mobile;
    }

    public Link() {
    }
}
