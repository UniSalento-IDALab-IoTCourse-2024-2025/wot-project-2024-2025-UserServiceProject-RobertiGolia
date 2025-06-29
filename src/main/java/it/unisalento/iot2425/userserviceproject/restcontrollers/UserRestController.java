package it.unisalento.iot2425.userserviceproject.restcontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisalento.iot2425.userserviceproject.di.IPaymentService;
import it.unisalento.iot2425.userserviceproject.domain.User;
import it.unisalento.iot2425.userserviceproject.dto.*;
import it.unisalento.iot2425.userserviceproject.exceptions.UserNotFoundException;
import it.unisalento.iot2425.userserviceproject.repositories.UserRepository;
import it.unisalento.iot2425.userserviceproject.security.JwtUtilities;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@RestController
//Contiene la mappatura delle logiche di business che risponderà alle richieste

@RequestMapping("/api/users")
//api come risorsa di primo livello, poi una basepath che mi gestisce le richieste per queste specifiche istruzioni(?)

public class UserRestController {

    //Disaccoppio le classi rest dalle azioni
    //Gli oggeti creati con @Autowired devono avere le classi mappate con @Component
    @Autowired
    private RabbitTemplate rabbitTemplate; //scambia i messaggi

    @Autowired
    UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtilities jwtUtilities;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    //tramite restcontroller si occupa di tradurre la lista in stringa, il mareshelling lo fa spring
    public UsersListDTO getAll() { //Data Trasfer Object, contiene solo le informazioni che volgiamo trasmettere al client
        List<User> users = userRepository.findAll();
        //crea l'oggetto
        List<UserDTO> list = new ArrayList<UserDTO>();
        for (User user : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setNome(user.getName());
            userDTO.setEmail(user.getEmail());
            userDTO.setCognome(user.getSurname());
            userDTO.setRuolo(user.getRole());
            userDTO.setNascita(user.getData_nascita());
            if (userDTO.getRuolo().equals("utente")) {
                userDTO.setEmail_parente(user.getEmail_parent());
                userDTO.setDisponibile(false);
                userDTO.setData(null);
            } else if (userDTO.getRuolo().equals("autista")) {
                userDTO.setEmail_parente("");
                userDTO.setDisponibile(user.isAvailable());
                userDTO.setData(user.getData());
                userDTO.setN_posti(user.getN_posti());
            }
            list.add(userDTO);
        }

        //casting dell'array in oggetto (wrapping)
        UsersListDTO listDTO = new UsersListDTO();
        listDTO.setUsersList(list);
        return listDTO; //restituisce un oggetto, non più un array
    }

    @RequestMapping(value = "/ID",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getID(@RequestHeader("Authorization") String token) throws UserNotFoundException {
        // Rimuovi "Bearer " dalla stringa dell'header per ottenere solo il token JWT.
        String jwtToken = token.substring(7);
        // Verifica e decodifica il token JWT utilizzando il segreto condiviso.
        Date expirationDate = jwtUtilities.extractExpiration(jwtToken);

        if (expirationDate.before(new Date())) {
            // Token scaduto
            return null;
        }
        //prende l'id dell'utente per collegarlo ad un mezzo
        String userId = jwtUtilities.extractClaim(jwtToken, claims -> claims.get("userId", String.class));
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }
        UserDTO userDto = new UserDTO();
        userDto.setId(user.get().getId());
        userDto.setUsername(user.get().getUsername());
        userDto.setNome(user.get().getName());
        userDto.setCognome(user.get().getSurname());
        userDto.setEmail(user.get().getEmail());
        userDto.setEmail_parente(user.get().getEmail_parent());
        userDto.setRuolo(user.get().getRole());
        userDto.setNascita(user.get().getData_nascita());
        if (userDto.getRuolo().equals("utente")) {
            userDto.setEmail_parente(user.get().getEmail_parent());
        } else if (userDto.getRuolo().equals("autista")) {
            userDto.setEmail_parente("");
            userDto.setDisponibile(user.get().isAvailable());
            userDto.setData(user.get().getData());
            userDto.setN_ore(user.get().getN_hours());
            userDto.setN_posti(user.get().getN_posti());
        }

        return userDto;
    }

    //non deve ritornare gli amministratori
    @RequestMapping(value = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserDTO findById(@PathVariable String id) throws UserNotFoundException {

        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }
        UserDTO userDto = new UserDTO();
        userDto.setId(user.get().getId());
        userDto.setUsername(user.get().getUsername());
        userDto.setNome(user.get().getName());
        userDto.setCognome(user.get().getSurname());
        userDto.setEmail(user.get().getEmail());
        userDto.setEmail_parente(user.get().getEmail_parent());
        userDto.setRuolo(user.get().getRole());
        userDto.setNascita(user.get().getData_nascita());
        if (userDto.getRuolo().equals("utente")) {
            userDto.setEmail_parente(user.get().getEmail_parent());
            userDto.setDisponibile(false);
            userDto.setData(null);
            userDto.setN_ore(0);
        } else if (userDto.getRuolo().equals("autista")) {
            userDto.setEmail_parente("");
            userDto.setDisponibile(user.get().isAvailable());
            userDto.setData(user.get().getData());
            userDto.setN_ore(user.get().getN_hours());
            userDto.setN_posti(user.get().getN_posti());
        }
        return userDto;

    }

    @RequestMapping(value = "/username/{username}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserDTO findByUsername(@PathVariable String username) throws UserNotFoundException {

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }
        UserDTO userDto = new UserDTO();
        userDto.setId(user.get().getId());
        userDto.setUsername(user.get().getUsername());
        userDto.setNome(user.get().getName());
        userDto.setCognome(user.get().getSurname());
        userDto.setEmail(user.get().getEmail());
        userDto.setEmail_parente(user.get().getEmail_parent());
        userDto.setRuolo(user.get().getRole());
        userDto.setNascita(user.get().getData_nascita());
        if (userDto.getRuolo().equals("utente")) {
            userDto.setEmail_parente(user.get().getEmail_parent());
            userDto.setDisponibile(false);
            userDto.setData(null);
            userDto.setN_ore(0);
        } else if (userDto.getRuolo().equals("autista")) {
            userDto.setEmail_parente("");
            userDto.setDisponibile(user.get().isAvailable());
            userDto.setData(user.get().getData());
            userDto.setN_ore(user.get().getN_hours());
            userDto.setN_posti(user.get().getN_posti());
        }
        return userDto;

    }

    @RequestMapping(value = "/update/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO update(@PathVariable String id, @RequestBody UserDTO userDTO) throws UserNotFoundException {
        ResultDTO resultDTO = new ResultDTO();

        resultDTO.setUser(null);
        resultDTO.setMessage("Utente non trovato");
        resultDTO.setResult(ResultDTO.ERRORE);

        Optional<User> user = userRepository.findById(id);

        user.ifPresent(u -> {
            userRepository.save(u);
            u.setId(userDTO.getId());
            u.setName(userDTO.getNome());
            u.setSurname(userDTO.getCognome());
            u.setUsername(userDTO.getUsername());
            u.setEmail(userDTO.getEmail());
            u.setRole(userDTO.getRuolo());
            u.setData_nascita(userDTO.getNascita());
            if (u.getRole().equals("utente")) {
                u.setEmail_parent(userDTO.getEmail_parente());
                u.setAvailable(false);
                u.setData(null);
                u.setN_hours(0);
            } else if (u.getRole().equals("autista")) {
                u.setEmail_parent("");
                u.setAvailable(userDTO.isDisponibile());
                u.setData(userDTO.getData());
                u.setN_hours(userDTO.getN_ore());
                u.setN_posti(userDTO.getN_posti());
            }

            userRepository.save(u);
            resultDTO.setUser(userDTO);
            resultDTO.setMessage("User aggiornato");
            resultDTO.setResult(ResultDTO.AGGIORNATO);
            resultDTO.getLog().add(resultDTO.getMessage());

        });

        return resultDTO;
    }

    @RequestMapping(
            value = "/addCorsa/{idAutista}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResultDTO addCorsa(@PathVariable String idAutista) throws UserNotFoundException {
        ResultDTO resultDTO = new ResultDTO();

        Optional<User> optionalUser = userRepository.findById(idAutista);
        if (optionalUser.isEmpty()) {
            resultDTO.setUser(null);
            resultDTO.setMessage("Utente non trovato");
            resultDTO.setResult(ResultDTO.ERRORE);
            resultDTO.getLog().add(resultDTO.getMessage());
            return resultDTO;
        }

        User user = optionalUser.get();
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setEmail_parente(user.getEmail_parent());
        userDTO.setRuolo(user.getRole());
        userDTO.setData(user.getData());
        userDTO.setNome(user.getName());
        userDTO.setCognome(user.getSurname());
        userDTO.setDisponibile(user.isAvailable());
        userDTO.setN_ore(user.getN_hours());

        switch (user.getRole()) {
            case "utente":
                user.setEmail_parent(userDTO.getEmail_parente());
                user.setAvailable(false);
                user.setData(null);
                user.setN_hours(0);
                break;

            case "autista":
                user.setEmail_parent("");
                user.setAvailable(userDTO.isDisponibile());
                user.setData(userDTO.getData());
                user.setN_hours(userDTO.getN_ore());
                System.out.println("numero corse dell'utente: " + user.getEmail() + " " + user.getN_corse());
                if (user.getN_corse() == 0) {
                    user.setN_corse(1);
                } else {
                    user.setN_corse(user.getN_corse() + 1);
                }
                user.setN_posti(userDTO.getN_posti());

                break;

            default:
                resultDTO.setUser(null);
                resultDTO.setMessage("Ruolo utente non valido");
                resultDTO.setResult(ResultDTO.ERRORE);
                resultDTO.getLog().add(resultDTO.getMessage());
                return resultDTO;
        }
        userDTO.setN_corse(user.getN_corse());
        userRepository.save(user);

        resultDTO.setUser(userDTO);
        resultDTO.setMessage("User aggiornato");
        resultDTO.setResult(ResultDTO.AGGIORNATO);
        resultDTO.getLog().add(resultDTO.getMessage());

        return resultDTO;
    }

    @RequestMapping(value = "/remCorsa/{idAutista}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO remCorsa(@PathVariable String idAutista) throws UserNotFoundException {
        ResultDTO resultDTO = new ResultDTO();

        Optional<User> optionalUser = userRepository.findById(idAutista);
        if (optionalUser.isEmpty()) {
            resultDTO.setUser(null);
            resultDTO.setMessage("Utente non trovato");
            resultDTO.setResult(ResultDTO.ERRORE);
            resultDTO.getLog().add(resultDTO.getMessage());
            return resultDTO;
        }

        User user = optionalUser.get();
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setEmail_parente(user.getEmail_parent());
        userDTO.setRuolo(user.getRole());
        userDTO.setData(user.getData());
        userDTO.setNome(user.getName());
        userDTO.setCognome(user.getSurname());
        userDTO.setDisponibile(user.isAvailable());
        userDTO.setN_ore(user.getN_hours());

        switch (user.getRole()) {
            case "utente":
                user.setEmail_parent(userDTO.getEmail_parente());
                user.setAvailable(false);
                user.setData(null);
                user.setN_hours(0);
                break;

            case "autista":
                user.setEmail_parent("");
                user.setAvailable(userDTO.isDisponibile());
                user.setData(userDTO.getData());
                user.setN_hours(userDTO.getN_ore());
                System.out.println("numero corse dell'utente: " + user.getEmail() + " " + user.getN_corse());
                if (user.getN_corse() > 0) {
                    user.setN_corse(user.getN_corse() - 1);
                }
                user.setN_posti(userDTO.getN_posti());

                break;

            default:
                resultDTO.setUser(null);
                resultDTO.setMessage("Ruolo utente non valido");
                resultDTO.setResult(ResultDTO.ERRORE);
                resultDTO.getLog().add(resultDTO.getMessage());
                return resultDTO;
        }
        userDTO.setN_corse(user.getN_corse());
        userRepository.save(user);

        resultDTO.setUser(userDTO);
        resultDTO.setMessage("User aggiornato");
        resultDTO.setResult(ResultDTO.AGGIORNATO);
        resultDTO.getLog().add(resultDTO.getMessage());

        return resultDTO;
    }

    @RequestMapping(
            value = "/takeSeat/{idAutista}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResultDTO takeSeat(@PathVariable String idAutista) {
        ResultDTO resultDTO = new ResultDTO();

        Optional<User> optionalUser = userRepository.findById(idAutista);
        if (optionalUser.isEmpty()) {
            return buildErrorResult("Utente non trovato");
        }

        User user = optionalUser.get();

        if (!"autista".equals(user.getRole())) {
            return buildErrorResult("Ruolo utente non valido");
        }

        if (user.getN_posti() <= 0) {
            return buildErrorResult("Posti non disponibili");
        }

        user.setN_posti(user.getN_posti() - 1);
        userRepository.save(user);

        ResultDTO successResult = new ResultDTO();
        successResult.setMessage("Posto prenotato");
        successResult.setResult(ResultDTO.AGGIORNATO);
        successResult.setUser(buildUserDTO(user));

        return successResult;
    }

    @RequestMapping(
            value = "/leaveSeat/{idAutista}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResultDTO leaveSeat(@PathVariable String idAutista) {
        ResultDTO resultDTO = new ResultDTO();

        Optional<User> optionalUser = userRepository.findById(idAutista);
        if (optionalUser.isEmpty()) {
            return buildErrorResult("Utente non trovato");
        }

        User user = optionalUser.get();

        if (!"autista".equals(user.getRole())) {
            return buildErrorResult("Ruolo utente non valido");
        }

        user.setN_posti(user.getN_posti() + 1);
        userRepository.save(user);

        ResultDTO successResult = new ResultDTO();
        successResult.setMessage("Posto lasciato");
        successResult.setResult(ResultDTO.AGGIORNATO);
        successResult.setUser(buildUserDTO(user));

        return successResult;
    }

    /** Costruisce un UserDTO a partire da un User */
    private UserDTO buildUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setEmail_parente(user.getEmail_parent());
        dto.setRuolo(user.getRole());
        dto.setData(user.getData());
        dto.setNome(user.getName());
        dto.setCognome(user.getSurname());
        dto.setDisponibile(user.isAvailable());
        dto.setN_ore(user.getN_hours());
        dto.setN_posti(user.getN_posti());
        dto.setN_corse(user.getN_corse());
        return dto;
    }

    /** Restituisce un ResultDTO d’errore con messaggio */
    private ResultDTO buildErrorResult(String message) {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setUser(null);
        resultDTO.setMessage(message);
        resultDTO.setResult(ResultDTO.ERRORE);
        resultDTO.getLog().add(message);
        return resultDTO;
    }


    @RequestMapping(value = "/deleteAutista",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO deleteAutista(@RequestHeader("Authorization") String token) throws UserNotFoundException, JsonProcessingException {
        ResultDTO resultDTO = new ResultDTO();

        resultDTO.setUser(null);
        resultDTO.setMessage("Utente non trovato");
        resultDTO.setResult(ResultDTO.ERRORE);

        // Rimuovi "Bearer " dalla stringa dell'header per ottenere solo il token JWT.
        String jwtToken = token.substring(7);
        // Verifica e decodifica il token JWT utilizzando il segreto condiviso.
        Date expirationDate = jwtUtilities.extractExpiration(jwtToken);

        if (expirationDate.before(new Date())) {
            // Token scaduto
            return null;
        }
        //prende l'id dell'utente per collegarlo ad un mezzo
        String userId = jwtUtilities.extractClaim(jwtToken, claims -> claims.get("userId", String.class));

        //chiamo il microservizio di trip per vedere se l'autista ha delle corse ancora in corso, prima di eliminare l'account
        String url = "http://TripSerProIoT:8080/api/trip/corse";
        WebClient webClient = webClientBuilder.build();
        String responseTrip = webClient.get()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("Risposta da users-service: " + responseTrip);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(responseTrip);

        // Accedi all'array "usersList"
        JsonNode usersList = root.path("usersList");
        // Verifica che l'array non sia vuoto e prendi il primo elemento
        //scorriamo tutta la lista per vedere se è presente l'autista
        for (int i = 0; i < usersList.size(); i++) {
            JsonNode firstUser = usersList.get(i);

            String idAutista = firstUser.path("idAutista").asText();
            System.out.println("IdAutista: " + idAutista);
            if (idAutista.equals(userId)) {
                resultDTO.setMessage("Devi terminare prima la corsa");
                resultDTO.setResult(ResultDTO.ERRORE);
                return resultDTO;
            }
        }

        //se esce dal ciclo for allora significa che non ha trovato l'autista nell'elenco delle corse
        Optional<User> user = userRepository.findById(userId);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.get().getId());
        userDTO.setUsername(user.get().getUsername());
        userDTO.setNome(user.get().getName());
        userDTO.setCognome(user.get().getSurname());
        userDTO.setEmail(user.get().getEmail());
        userDTO.setRuolo(user.get().getRole());
        userDTO.setData(userDTO.getData());
        userDTO.setN_ore(user.get().getN_hours());
        userDTO.setEmail_parente(user.get().getEmail_parent());
        userDTO.setNascita(user.get().getData_nascita());
        userDTO.setN_posti(user.get().getN_posti());
        userRepository.delete(user.get());
        resultDTO.setResult(ResultDTO.OK);
        resultDTO.setMessage("Autista eliminato");
        return resultDTO;
    }

    @RequestMapping(value = "/changeDispo/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO changeDispo(@PathVariable String id) throws UserNotFoundException {
        ResultDTO resultDTO = new ResultDTO();

        resultDTO.setUser(null);
        resultDTO.setMessage("Utente non trovato");
        resultDTO.setResult(ResultDTO.ERRORE);

        Optional<User> user = userRepository.findById(id);

        user.ifPresent(u -> {
            if (u.isAvailable()) {
                u.setAvailable(false);
                resultDTO.setMessage(u.getName() + " non disponibile");
            } else {
                u.setAvailable(true);
                resultDTO.setMessage(u.getName() + " disponibile");
            }
            userRepository.save(u);


            resultDTO.setResult(ResultDTO.AGGIORNATO);
            resultDTO.getLog().add(resultDTO.getMessage());

        });

        return resultDTO;
    }


    //viene usato dagli altri microservizi per aggiornare il backlog
    @RequestMapping(value = "/setBacklogOtherService/{message}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO setBacklogOtherService(@PathVariable String message, @RequestHeader("Authorization") String token) {
        ResultDTO resultDTO = new ResultDTO();
        // Rimuovi "Bearer " dalla stringa dell'header per ottenere solo il token JWT.
        String jwtToken = token.substring(7);
        // Verifica e decodifica il token JWT utilizzando il segreto condiviso.
        Date expirationDate = jwtUtilities.extractExpiration(jwtToken);

        if (expirationDate.before(new Date())) {
            // Token scaduto
            return null;
        }
        //prende l'id dell'utente per collegarlo ad un mezzo
        String userId = jwtUtilities.extractClaim(jwtToken, claims -> claims.get("userId", String.class));
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u -> {
            u.getBacklog().add(u.getEmail() + ": " + message);
            userRepository.save(u);
        });
        resultDTO.getLog().add(user.get().getEmail() + ": " + message);
        resultDTO.setResult(ResultDTO.OK);
        resultDTO.setMessage("Azione salvata nel log");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.get().getId());
        userDTO.setNome(user.get().getName());
        userDTO.setUsername(user.get().getUsername());
        userDTO.setCognome(user.get().getSurname());
        userDTO.setEmail(user.get().getEmail());
        userDTO.setRuolo(user.get().getRole());
        userDTO.setNascita(user.get().getData_nascita());
        if (userDTO.getRuolo().equals("utente")) {
            userDTO.setEmail_parente(user.get().getEmail_parent());
            userDTO.setDisponibile(false);
            userDTO.setData(null);
        } else if (userDTO.getRuolo().equals("autista")) {
            userDTO.setEmail_parente("");
            userDTO.setDisponibile(user.get().isAvailable());
            userDTO.setData(userDTO.getData());
            userDTO.setN_ore(user.get().getN_hours());
        }
        resultDTO.setUser(userDTO);
        return resultDTO;
    }

    @RequestMapping(value = "/setBacklogMoney/{message}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO setBacklogMoney(@PathVariable String message, @RequestHeader("Authorization") String token) {
        ResultDTO resultDTO = new ResultDTO();
        // Rimuovi "Bearer " dalla stringa dell'header per ottenere solo il token JWT.
        String jwtToken = token.substring(7);
        // Verifica e decodifica il token JWT utilizzando il segreto condiviso.
        Date expirationDate = jwtUtilities.extractExpiration(jwtToken);

        if (expirationDate.before(new Date())) {
            // Token scaduto
            return null;
        }
        //prende l'id dell'utente per collegarlo ad un mezzo
        String userId = jwtUtilities.extractClaim(jwtToken, claims -> claims.get("userId", String.class));
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u -> {
            u.getBacklog().add(message);
            userRepository.save(u);
        });
        resultDTO.getLog().add(user.get().getEmail() + ": " + message);
        resultDTO.setResult(ResultDTO.OK);
        resultDTO.setMessage("Azione salvata nel log (denaro)");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.get().getId());
        userDTO.setNome(user.get().getName());
        userDTO.setUsername(user.get().getUsername());
        userDTO.setCognome(user.get().getSurname());
        userDTO.setEmail(user.get().getEmail());
        userDTO.setRuolo(user.get().getRole());
        userDTO.setNascita(user.get().getData_nascita());
        if (userDTO.getRuolo().equals("utente")) {
            userDTO.setEmail_parente(user.get().getEmail_parent());
            userDTO.setDisponibile(false);
            userDTO.setData(null);
            userDTO.setN_ore(0);
        } else if (userDTO.getRuolo().equals("autista")) {
            userDTO.setEmail_parente("");
            userDTO.setDisponibile(user.get().isAvailable());
            userDTO.setData(userDTO.getData());
            userDTO.setN_ore(user.get().getN_hours());
            userDTO.setN_posti(user.get().getN_posti());
        }

        resultDTO.setUser(userDTO);
        return resultDTO;
    }

    @RequestMapping(value = "/backlog/{idUser}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO getBackLogByIdUser(@PathVariable String idUser) {
        ResultDTO resultDTO = new ResultDTO();
        Optional<User> user = userRepository.findById(idUser);
        ArrayList<String> lista = new ArrayList<>();
        if (!user.isEmpty()) {
            for (String msg : user.get().getBacklog()) {
                lista.add(msg);
            }

            resultDTO.setResult(ResultDTO.OK);
            resultDTO.setLog(lista);
            resultDTO.setMessage("Lista dei movimenti dell'utente: " + user.get().getName());
        } else {
            resultDTO.setMessage("Lista dei movimenti non trovata per l'utente: " + user.get().getName());
            resultDTO.setResult(ResultDTO.ERRORE);
            resultDTO.setLog(lista);
        }


        return resultDTO;
    }

    @RequestMapping(value = "/deletebacklog/{idUser}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO deleteBackLogByIdUser(@PathVariable String idUser) {
        ResultDTO resultDTO = new ResultDTO();
        Optional<User> user = userRepository.findById(idUser);
        ArrayList<String> lista = new ArrayList<>();
        if (!user.isEmpty()) {

            user.get().getBacklog().clear();
            userRepository.save(user.get());
            resultDTO.setResult(ResultDTO.OK);
            resultDTO.setLog(lista);
            resultDTO.setMessage("Lista dei movimenti dell'utente: " + user.get().getName() + " eliminata");
        } else {
            resultDTO.setMessage("Lista dei movimenti non trovata per l'utente: " + user.get().getName());
            resultDTO.setResult(ResultDTO.ERRORE);
            resultDTO.setLog(lista);
        }


        return resultDTO;
    }


    @RequestMapping(value = "/backlogTotale",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO getBackLogAll() {
        ResultDTO resultDTO = new ResultDTO();
        List<User> users = userRepository.findAll();
        ArrayList<String> lista = new ArrayList<>();
        for (User user : users) {
            for (String msg : user.getBacklog()) {
                lista.add(msg);
            }

        }
        resultDTO.setMessage("Lista dei movimenti");
        resultDTO.setResult(ResultDTO.OK);
        resultDTO.setLog(lista);
        return resultDTO;
    }


    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );
        Optional<User> user = userRepository.findByEmail(authentication.getName());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(loginDTO.getEmail());
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.get().getId());


        //crea il token
        final String jwt = jwtUtilities.generateToken(user.get().getEmail(), claims);
        return ResponseEntity.ok(new AuthenticationResponseDTO(jwt));
    }


    @RequestMapping(value = "/deleteUser/{idUser}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO deleteUser(@PathVariable String idUser) {
        ResultDTO resultDTO = new ResultDTO();
        Optional<User> user = userRepository.findById(idUser);
        if (!user.isEmpty()) {
            userRepository.delete(user.get());
            resultDTO.setResult(ResultDTO.OK);
            resultDTO.setUser(null);
            resultDTO.setMessage("Utente eliminato");
        } else {
            resultDTO.setMessage("Utente non trovato");
            resultDTO.setResult(ResultDTO.ERRORE);
        }


        return resultDTO;
    }

    @RequestMapping(value = "/avaible",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET)
    public UsersListDTO getUsersAvaible() {

        List<User> users = userRepository.findAll();
        //crea l'oggetto
        List<UserDTO> list = new ArrayList<UserDTO>();
        for (User user : users) {

            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setNome(user.getName());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setCognome(user.getSurname());
            userDTO.setRuolo(user.getRole());
            userDTO.setNascita(user.getData_nascita());
            if (userDTO.getRuolo().equals("autista")) {
                userDTO.setEmail_parente("");
                userDTO.setDisponibile(user.isAvailable());
                userDTO.setData(user.getData());
                if (user.isAvailable()) {
                    list.add(userDTO);
                }
                userDTO.setN_posti(user.getN_posti());
            }


        }

        UsersListDTO usersListDTO = new UsersListDTO();
        usersListDTO.setUsersList(list);
        return usersListDTO;
    }
}