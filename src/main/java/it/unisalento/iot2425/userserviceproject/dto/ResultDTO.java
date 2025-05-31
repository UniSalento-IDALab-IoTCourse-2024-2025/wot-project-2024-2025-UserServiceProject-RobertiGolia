package it.unisalento.iot2425.userserviceproject.dto;

import java.util.ArrayList;

public class ResultDTO {
    public final static int PRENOTATO = 0;
    public final static int OK = 1;
    public final static int ERRORE = 2;
    public final static int ELIMINATO = 3;
    public final static int AGGIORNATO = 4;

    private UserDTO user;
    private int result;
    private String message;
    private ArrayList<String> log = new ArrayList<>();

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public ArrayList<String> getLog() {
        return log;
    }

    public void setLog(ArrayList<String> log) {
        this.log = log;
    }
}

