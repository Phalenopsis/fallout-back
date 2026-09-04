package eu.nicosworld.falloutback.domain.namegenerator;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.namegenerator.Gender;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.namegenerator.FirstNameRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.namegenerator.LastNameRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.namegenerator.SuperMutantNameRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NameGeneratorService {

    private final FirstNameRepository firstNameRepository;
    private final LastNameRepository lastNameRepository;
    private final SuperMutantNameRepository superMutantNameRepository;

    public NameGeneratorService(FirstNameRepository firstNameRepository, LastNameRepository lastNameRepository, SuperMutantNameRepository superMutantNameRepository) {
        this.firstNameRepository = firstNameRepository;
        this.lastNameRepository = lastNameRepository;
        this.superMutantNameRepository = superMutantNameRepository;
    }

    public List<String> generateNames(Gender gender, int count) {
        List<String> firstNames;

        if (gender == null || gender == Gender.NEUTRAL) {
            // Si le MJ ne précise pas de genre ou demande "Tout / Neutre", on mélange tout
            firstNames = firstNameRepository.findRandomFirstNamesAnyGender(count);
        } else {
            // Homme = MALE + NEUTRAL / Femme = FEMALE + NEUTRAL
            firstNames = firstNameRepository.findRandomFirstNamesIncludingNeutral(gender.name(), count);
        }

        List<String> lastNames = lastNameRepository.findRandomLastNames(count);

        List<String> fullNames = new ArrayList<>();
        for (int i = 0; i < firstNames.size(); i++) {
            fullNames.add(firstNames.get(i) + " " + lastNames.get(i));
        }

        return fullNames;
    }

    public List<String> generateSuperMutantNames(int count) {
        return superMutantNameRepository.findRandomNames(count);
    }
}
