package it.unisalento.iot2425.userserviceproject.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;

@Document("user")
public class User {

    @Id
    private String id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String role;
    private String email_parent;
    private boolean available;
    private Date data;
    private int n_hours;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail_parent() {
        return email_parent;
    }

    public void setEmail_parent(String email_parent) {
        this.email_parent = email_parent;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public int getN_hours() {
        return n_hours;
    }

    public void setN_hours(int n_hours) {
        this.n_hours = n_hours;
    }

    public ArrayList<String> getBacklog() {
        return backlog;
    }

    public void setBacklog(ArrayList<String> backlog) {
        this.backlog = backlog;
    }
}