package pl.pb.finansista.user.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

import java.util.UUID;

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
