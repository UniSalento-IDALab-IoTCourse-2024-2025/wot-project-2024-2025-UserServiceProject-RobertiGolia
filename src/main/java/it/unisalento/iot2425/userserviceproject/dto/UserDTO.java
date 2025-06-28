package it.unisalento.iot2425.userserviceproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class UserDTO {
    private String id;
    private String username;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String ruolo;
    private String email_parente;
    private boolean disponibile;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate data; //data e orario per gestire le prenotazioni disponibili per l'autista
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nascita;
    private int n_ore; //indica il numero di ore nella quale è disponibile
    private int n_corse;
    private int n_posti;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalDate getNascita() {
        return nascita;
    }

    public void setNascita(LocalDate nascita) {
        this.nascita = nascita;
    }

    public int getN_ore() {
        return n_ore;
    }

    public void setN_ore(int n_ore) {
        this.n_ore = n_ore;
    }

    public int getN_corse() {
        return n_corse;
    }

    public void setN_corse(int n_corse) {
        this.n_corse = n_corse;
    }

    public int getN_posti() {
        return n_posti;
    }

    public void setN_posti(int n_posti) {
        this.n_posti = n_posti;
    }

    public ArrayList<String> getBacklog() {
        return backlog;
    }

    public void setBacklog(ArrayList<String> backlog) {
        this.backlog = backlog;
    }
}
