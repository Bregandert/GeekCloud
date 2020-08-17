package common;

public class AuthOk extends AbstractMessage {
    private String nickname;

    public String getNickname() {
        return nickname;
    }
    public AuthOk (String a){
        this.nickname=a;

    }}
