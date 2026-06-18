package pl.pb.finansista.user.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.exception.CannotDeactivateSelfException;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

/**
 * Soft delete: aktywacja / dezaktywacja konta użytkownika. Nie usuwa rekordu
 * (zachowuje integralność FK i audyt), tylko przełącza flagę {@code active}.
 */
@Service
@RequiredArgsConstructor
public class SetUserActiveUseCase {

    private final UserRepository userRepository;

    @Transactional
    public void execute(SetUserActiveCommand command) {
        User user = userRepository.findByExternalId(command.userExternalId())
                .orElseThrow(UserNotFoundException::new);

        // Admin nie może odciąć sam sobie dostępu, dezaktywując własne konto.
        if (!command.active() && user.getEmail().equals(command.actingUserEmail())) {
            throw new CannotDeactivateSelfException();
        }

        user.setActive(command.active());
        userRepository.save(user);
    }
}
