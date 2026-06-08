package pl.pb.finansista.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pb.finansista.model.User;
import pl.pb.finansista.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("Nie znaleziono użytkownika o podanym emialu: " + email));
    }
}
