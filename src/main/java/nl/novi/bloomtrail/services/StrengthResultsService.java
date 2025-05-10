package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.StrengthResultsInputDto;
import nl.novi.bloomtrail.exceptions.BadRequestException;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.helper.AccessValidator;
import nl.novi.bloomtrail.mappers.StrengthResultsMapper;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.StrengthResultsRepository;
import org.springframework.stereotype.Service;
import nl.novi.bloomtrail.helper.ValidationHelper;

import java.util.List;

@Service
public class StrengthResultsService {
    private final StrengthResultsRepository strengthResultsRepository;
    private final AccessValidator accessValidator;
    private final ValidationHelper validationHelper;

    public StrengthResultsService(StrengthResultsRepository strengthResultsRepository, AccessValidator accessValidator, ValidationHelper validationHelper) {
        this.strengthResultsRepository = strengthResultsRepository;
        this.accessValidator = accessValidator;
        this.validationHelper = validationHelper;

    }

    public StrengthResults getStrengthResultsByUsername(String username) {
        User user = validationHelper.validateUser(username);
        return strengthResultsRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("No strength results found for user: " + username));
    }

    public StrengthResults createStrengthResults(StrengthResultsInputDto inputDto, String username) {
        User user = validationHelper.validateUser(username);
        accessValidator.validateSelfOrAdminAccess(user.getUsername());


        if (strengthResultsRepository.findByUser(user).isPresent()) {
            throw new BadRequestException("Strength results already exist for user: " + username);
        }

        StrengthResults strengthResults = StrengthResultsMapper.toStrengthResultsEntity(inputDto, user);

        return strengthResultsRepository.save(strengthResults);
    }

    public StrengthResults modifyStrengthResults(StrengthResultsInputDto inputDto, String username) {
        User user = validationHelper.validateUser(username);
        accessValidator.validateSelfOrAdminAccess(user.getUsername());

        StrengthResults strengthResults = strengthResultsRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("No strength results found for user: " + username));

        strengthResults.setSummary(inputDto.getSummary());
        strengthResults.setTopStrengthNames(inputDto.getTopStrengthNames());

        return strengthResultsRepository.save(strengthResults);
    }

    public void deleteStrengthResultsByUsername(String username) {
        User user = validationHelper.validateUser(username);

        StrengthResults strengthResults = strengthResultsRepository.findByUser(user)
                .orElseThrow(() -> {
                    return new NotFoundException("No strength results found for user: " + username);
                });
        strengthResultsRepository.delete(strengthResults);
    }


}

