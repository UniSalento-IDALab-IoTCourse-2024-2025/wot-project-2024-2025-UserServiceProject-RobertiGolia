package it.unisalento.iot2425.userserviceproject.dto;

import java.util.List;

public class UsersListDTO {

    private List<UserDTO> usersList;

    public List<UserDTO> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<UserDTO> usersList) {
        this.usersList = usersList;
    }
}