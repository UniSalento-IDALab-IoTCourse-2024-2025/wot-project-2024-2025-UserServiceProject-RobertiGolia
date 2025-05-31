package it.unisalento.iot2425.userserviceproject.dto;

public class LoginDTO {

    private String email;
    private String password;
    private String ruolo;

    //stacca un token quando si fa il login

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}