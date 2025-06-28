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

import javax.swing.text.html.Option;
import java.security.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
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
        List<User> username = userRepository.findByNameAndSurname(userDTO.getNome(), userDTO.getCognome());
        if (!existingUser.isEmpty()) {
            resultDTO.setResult(RegistrationResultDTO.EMAIL_ALREADY_EXISTS);
            resultDTO.setMessage("La mail è già associata ad un altro utente");
            return resultDTO;
        }
        if (!username.isEmpty()) {
            resultDTO.setResult(RegistrationResultDTO.USERNAME_NOD_VALID);
            resultDTO.setMessage("Lo username è già in uso");
            return resultDTO;
        } else {
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setName(userDTO.getNome());
            user.setSurname(userDTO.getCognome());
            user.setEmail(userDTO.getEmail());
            user.setRole(userDTO.getRuolo());
            if (userDTO.getRuolo() == null) {
                user.setRole("utente");
                user.setAvailable(false);
                user.setData(userDTO.getData());
                user.setEmail_parent(userDTO.getEmail_parente());
                System.out.println("Email parente: " + userDTO.getEmail_parente());
            } else {
                String ruolo = userDTO.getRuolo();

                switch (ruolo) {
                    case "utente":
                        user.setRole("utente");
                        user.setAvailable(false);
                        user.setData(userDTO.getData());
                        user.setEmail_parent(userDTO.getEmail_parente());
                        System.out.println("Email parente: " + userDTO.getEmail_parente());
                        break;
                    case "amministratore":
                        user.setRole("amministratore");
                        user.setAvailable(false); // o true, a seconda della logica che desideri
                        break;

                    default: // trattiamo come autista o altro ruolo
                        user.setRole(ruolo);
                        user.setAvailable(true);
                        if (userDTO.getData() == null) {
                            user.setData(LocalDate.of(2024, 6, 18));
                        } else {
                            user.setData(userDTO.getData());
                            user.setN_hours(userDTO.getN_ore());
                        }
                        user.setN_posti(userDTO.getN_posti());
                        break;
                }
            }

            //codifica la password
            user.setPassword(passwordEncoder().encode(userDTO.getPassword()));
            user.setData_nascita(userDTO.getNascita());
            user = userRepository.save(user);


            userDTO.setDisponibile(user.isAvailable());
            resultDTO.setResult(RegistrationResultDTO.OK);
            resultDTO.setMessage("Registrazione effettuata con successo");
            resultDTO.getLog().add(resultDTO.getMessage());
            resultDTO.setUser(userDTO);
        }
        return resultDTO;
    }

}
