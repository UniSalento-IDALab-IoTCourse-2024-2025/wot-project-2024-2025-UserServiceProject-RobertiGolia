package it.unisalento.iot2425.userserviceproject.dto;

public class RegistrationResultDTO {

    public final static int OK = 0;
    public final static int EMAIL_ALREADY_EXISTS = 1;


    public final static int MISSING_NAME = 2;
    public final static int MISSING_USERNAME = 3;
    public final static int USERNAME_NOD_VALID = 4;

    //AGGIUNGERE ALTRI CODICI ERRORE, DATI_MANCANTI... VOCABOLARIO DI ERRORI
    public final static int GENERIC_ERROR = 99;

    private int result;
    private String message;
    private UserDTO user;

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
}
