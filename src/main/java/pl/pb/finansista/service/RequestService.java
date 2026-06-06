package pl.pb.finansista.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pb.finansista.reference.CostCategory;
import pl.pb.finansista.reference.Department;
import pl.pb.finansista.repository.*;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatus;
import pl.pb.finansista.request.RequestTemplate;
import pl.pb.finansista.user.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestStatusRepository requestStatusRepository;
    private final RequestTemplateRepository requestTemplateRepository;
    private final UserRepository userRepository;
    private final CostCategoryRepository costCategoryRepository;
    private final DepartmentRepository departmentRepository;

    public List<Request> getAllRequests(){
        return requestRepository.findAll();
    }

    public Request getRequestById(Long id){
        return requestRepository.findById(id).orElseThrow(()
        -> new RuntimeException("Nie znaleziono wniosku o ID: " + id));
    }

    public Request createNewRequest(Request newRequest, Long templateId, Long userId, Long categoryId, Long departmentId){

        RequestStatus initialStatus = requestStatusRepository.findById(1L).orElseThrow(
                () -> new RuntimeException("Błąd: Brak statusu początkowego"));
        newRequest.setStatus(initialStatus);

        if(templateId != null){
            RequestTemplate template = requestTemplateRepository.findById(templateId)
                    .orElseThrow(() -> new RuntimeException("Błąd: Szablon o ID: " + templateId + " nie istnieje"));
            newRequest.setTemplate(template);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Błąd: nie znaleziono użytkownika o ID: " + userId));
        newRequest.setUser(user);

        CostCategory category = costCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Błąd: Nie znaleziono kategorii kosztów o ID " + categoryId));
        newRequest.setCostCategory(category);

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Błąd: Nie znaleziono wydziału o ID " + departmentId));
        newRequest.setDepartment(department);

        return requestRepository.save(newRequest);
    }
}
