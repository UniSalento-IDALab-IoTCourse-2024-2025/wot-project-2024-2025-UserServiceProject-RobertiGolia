package it.unisalento.iot2425.userserviceproject.repositories;

import it.unisalento.iot2425.userserviceproject.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);
    List<User> findAll();
    Optional<User> findById(String id);

    //fa la ricerca per due campi
    List<User> findByNameAndSurname(String name, String surname);
}
