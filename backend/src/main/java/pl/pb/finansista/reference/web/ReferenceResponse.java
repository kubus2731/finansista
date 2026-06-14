package pl.pb.finansista.reference.web;

import pl.pb.finansista.reference.CostCategory;
import pl.pb.finansista.reference.Department;
import pl.pb.finansista.reference.FundingSource;

public record ReferenceResponse(
        Long id,
        String name
) {
    public static ReferenceResponse of(Department department) {
        return new ReferenceResponse(department.getId(), department.getName());
    }

    public static ReferenceResponse of(CostCategory costCategory) {
        return new ReferenceResponse(costCategory.getId(), costCategory.getName());
    }

    public static ReferenceResponse of(FundingSource fundingSource) {
        return new ReferenceResponse(null, fundingSource.getName());
    }
}
