package pl.pb.finansista.request.service;

import pl.pb.finansista.request.view.CreateRequestForm;
import pl.pb.finansista.request.view.RequestView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestService {

    List<RequestView> findAll();

    Optional<RequestView> findById(UUID id);

    RequestView create(CreateRequestForm form);
}
