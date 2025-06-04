package it.unisalento.iot2425.userserviceproject.restcontrollers;

import it.unisalento.iot2425.userserviceproject.domain.User;
import it.unisalento.iot2425.userserviceproject.dto.RegistrationResultDTO;
import it.unisalento.iot2425.userserviceproject.dto.ResultDTO;
import it.unisalento.iot2425.userserviceproject.dto.UserDTO;
import it.unisalento.iot2425.userserviceproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static it.unisalento.iot2425.userserviceproject.configuration.SecurityConfig.passwordEncoder;

@RestController
//Contiene la mappatura delle logiche di business che risponderà alle richieste

@RequestMapping("/api/registration")
//api come risorsa di primo livello, poi una basepath che mi gestisce le richieste per queste specifiche istruzioni(?)
public class UserRegistrationRestController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserRegistrationRestController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = "/",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE //consuma/produce un json
    )
    //cambiare il tipo che restiuisce, aggiungendo il codice errore
    public ResultDTO save(@RequestBody UserDTO userDTO) {
        ResultDTO resultDTO = new ResultDTO();

        //modifica l'id
        //prevede una modifica all'interno del DataBase
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (!existingUser.isEmpty()) {
            resultDTO.setResult(RegistrationResultDTO.EMAIL_ALREADY_EXISTS);
            resultDTO.setMessage("La mail è già associata ad un altro utente");
            return resultDTO;
        } else {

            User user = new User();
            user.setName(userDTO.getNome());
            user.setSurname(userDTO.getCognome());
            user.setEmail(userDTO.getEmail());
            if (userDTO.getRuolo() == null) {
                user.setRole("utente");
            } else {
                user.setRole(userDTO.getRuolo());
            }
            //codifica la password
            user.setPassword(passwordEncoder().encode(userDTO.getPassword()));
            user = userRepository.save(user);
            userDTO.setId(user.getId());
            userDTO.setEmail_parente(user.getEmail_parent());

            resultDTO.setResult(RegistrationResultDTO.OK);
            resultDTO.setMessage("Registrazione effettuata con successo");
            resultDTO.getLog().add(resultDTO.getMessage());
        }
        return resultDTO;
    }

}
