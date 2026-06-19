package pl.pb.finansista.user.usecase;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class GetMyProfileUseCase {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User execute(UUID externalId) {
        return userRepository.findByExternalId(externalId)
                .orElseThrow(UserNotFoundException::new);
    }
}
