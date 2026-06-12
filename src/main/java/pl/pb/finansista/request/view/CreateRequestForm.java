package pl.pb.finansista.request.view;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateRequestForm(
        @NotBlank(message = "Tytuł jest wymagany")
        @Size(max = 100, message = "Tytuł może mieć najwyżej 100 znaków")
        String title,

        @NotBlank(message = "Opis jest wymagany")
        String description,

        @NotNull(message = "Wartość nie może być pusta")
        @Positive(message = "Wartość nie może być ujemna")
        BigDecimal amount,

        @NotNull(message = "kategoria nie może być pusta")
        Long costCategoryId
) {
}
