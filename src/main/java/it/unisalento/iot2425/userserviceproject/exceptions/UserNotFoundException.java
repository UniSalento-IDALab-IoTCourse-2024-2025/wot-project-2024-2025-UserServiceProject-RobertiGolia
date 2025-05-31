package it.unisalento.iot2425.userserviceproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//serve per restituire i codice di errore
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends Exception{

}
