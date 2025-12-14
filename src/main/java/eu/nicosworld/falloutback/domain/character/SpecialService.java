package eu.nicosworld.falloutback.domain.character;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Special;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.character.SpecialRepository;
import eu.nicosworld.falloutback.infrastructure.web.dto.character.SpecialDto;
import org.springframework.stereotype.Service;

@Service
public class SpecialService {

    public SpecialRepository specialRepository;

    public SpecialService(SpecialRepository specialRepository) {
        this.specialRepository = specialRepository;
    }

    public Special save(SpecialDto specialDto, Character character) {
        Special special = new Special(specialDto);
        special.setCharacter(character);
        return this.specialRepository.save(special);
    }
}
