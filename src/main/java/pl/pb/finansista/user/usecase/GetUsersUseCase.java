package pl.pb.finansista.user.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.user.repository.UserRepository;
import pl.pb.finansista.user.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUsersUseCase {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> execute() {
        return userRepository.findAll();
    }
}
