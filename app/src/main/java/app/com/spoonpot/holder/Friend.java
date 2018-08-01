package app.com.spoonpot.holder;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by USUARIO-PC on 04/08/2017.
 */
@IgnoreExtraProperties
public class Friend {

    private String id;
    private String user;
    private String friend;
    private String namefriend;
    private String friendFirebaseCode;
    private String mobile;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getNamefriend() {
        return namefriend;
    }

    public void setNamefriend(String namefriend) {
        this.namefriend = namefriend;
    }

    public String getFriendFirebaseCode() {
        return friendFirebaseCode;
    }

    public void setFriendFirebaseCode(String friendFirebaseCode) {
        this.friendFirebaseCode = friendFirebaseCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Friend(String id, String user, String friend, String namefriend, String friendFirebaseCode, String mobile) {
        this.id = id;
        this.user = user;
        this.friend = friend;
        this.namefriend = namefriend;
        this.friendFirebaseCode = friendFirebaseCode;
        this.mobile = mobile;
    }

    public Friend() {
    }
}
