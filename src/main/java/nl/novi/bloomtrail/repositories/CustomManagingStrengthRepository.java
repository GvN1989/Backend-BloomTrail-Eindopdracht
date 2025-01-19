package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.ManagingStrength;

import java.util.List;

public interface CustomManagingStrengthRepository {
    List<ManagingStrength> getManagingStrengths(List<Integer> strengthIds);
}
