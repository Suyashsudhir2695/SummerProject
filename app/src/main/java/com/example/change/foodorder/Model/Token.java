package com.example.change.foodorder.Model;

public class Token {
    private boolean isSereverToken;
    private String token;

    public Token(String token,boolean isSereverToken) {
        this.isSereverToken = isSereverToken;
        this.token = token;
    }

    public Token() {
    }

    public boolean isSereverToken() {
        return isSereverToken;
    }

    public void setSereverToken(boolean sereverToken) {
        isSereverToken = sereverToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
