package eu.nicosworld.falloutback.infrastructure.web.controller.namegenerator;

import eu.nicosworld.falloutback.domain.namegenerator.NameGeneratorService;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.namegenerator.Gender;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/names")
public class NameController {
    private final NameGeneratorService nameGeneratorService;

    public NameController(NameGeneratorService nameGeneratorService) {
        this.nameGeneratorService = nameGeneratorService;
    }

    @GetMapping("/generate")
    public ResponseEntity<List<String>> generate(@RequestParam Gender gender, @RequestParam(defaultValue = "5") int count) {
        return ResponseEntity.ok(nameGeneratorService.generateNames(gender, count));
    }

    @GetMapping("/super-mutant")
    public ResponseEntity<List<String>> generateSuperMutants(@RequestParam(defaultValue = "5") int count) {
        return ResponseEntity.ok(nameGeneratorService.generateSuperMutantNames(count));
    }
}
