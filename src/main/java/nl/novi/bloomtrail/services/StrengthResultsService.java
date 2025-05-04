package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.StrengthResultsInputDto;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.StrengthResultsRepository;
import org.springframework.stereotype.Service;
import nl.novi.bloomtrail.helper.ValidationHelper;

@Service
public class StrengthResultsService {
    private final StrengthResultsRepository strengthResultsRepository;
    private final ValidationHelper validationHelper;

    public StrengthResultsService(StrengthResultsRepository strengthResultsRepository, ValidationHelper validationHelper) {
        this.strengthResultsRepository = strengthResultsRepository;
        this.validationHelper = validationHelper;

    }

    public StrengthResults getStrengthResultsByUsername(String username) {
        User user = validationHelper.validateUser(username);
        return strengthResultsRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("No strength results found for user: " + username));
    }

    public StrengthResults createStrengthResults(StrengthResultsInputDto inputDto, String username) {
        User user = validationHelper.validateUser(username);

        if (strengthResultsRepository.findByUser(user).isPresent()) {
            throw new IllegalStateException("Strength results already exist for user: " + username);
        }

        StrengthResults strengthResults = new StrengthResults();
        strengthResults.setSummary(inputDto.getSummary());
        strengthResults.setTopStrengthNames(inputDto.getTopStrengthNames());
        strengthResults.setUser(user);

        return strengthResultsRepository.save(strengthResults);
    }

    public StrengthResults modifyStrengthResults(StrengthResultsInputDto inputDto, String username) {
        User user = validationHelper.validateUser(username);

        StrengthResults strengthResults = strengthResultsRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("No strength results found for user: " + username));

        strengthResults.setSummary(inputDto.getSummary());
        strengthResults.setTopStrengthNames(inputDto.getTopStrengthNames());

        return strengthResultsRepository.save(strengthResults);
    }

    public void deleteStrengthResultsByUsername(String username) {
        User user = validationHelper.validateUser(username);
        StrengthResults strengthResults = strengthResultsRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("No strength results found for user: " + username));

        strengthResultsRepository.delete(strengthResults);
    }


}

