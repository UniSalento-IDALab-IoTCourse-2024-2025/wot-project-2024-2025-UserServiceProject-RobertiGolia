package it.unisalento.iot2425.userserviceproject.dto;

import java.util.ArrayList;

public class UserDTO {
    private String id;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String ruolo;
    private String email_parente;
    private boolean disponibile;
    private ArrayList<String> backlog = new ArrayList<>();

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }


    public String getEmail_parente() {
        return email_parente;
    }

    public void setEmail_parente(String email_parente) {
        this.email_parente = email_parente;
    }

    public boolean isDisponibile() {
        return disponibile;
    }

    public void setDisponibile(boolean disponibile) {
        this.disponibile = disponibile;
    }

    public ArrayList<String> getBacklog() {
        return backlog;
    }

    public void setBacklog(ArrayList<String> backlog) {
        this.backlog = backlog;
    }
}
