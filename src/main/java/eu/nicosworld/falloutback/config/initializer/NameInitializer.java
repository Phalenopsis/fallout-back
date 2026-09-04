package eu.nicosworld.falloutback.config.initializer;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.namegenerator.FirstName;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.namegenerator.Gender;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.namegenerator.LastName;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.namegenerator.SuperMutantName;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.namegenerator.FirstNameRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.namegenerator.LastNameRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.namegenerator.SuperMutantNameRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NameInitializer implements CommandLineRunner {

    private final FirstNameRepository firstNameRepository;
    private final LastNameRepository lastNameRepository;
    private final SuperMutantNameRepository superMutantNameRepository;

    public NameInitializer(FirstNameRepository firstNameRepository, LastNameRepository lastNameRepository, SuperMutantNameRepository superMutantNameRepository) {
        this.firstNameRepository = firstNameRepository;
        this.lastNameRepository = lastNameRepository;
        this.superMutantNameRepository = superMutantNameRepository;
    }

    @Override
    public void run(String... args) {
        initFirstNames();
        initLastNames();
        initSuperMutantNames();
    }

    private void initSuperMutantNames() {
        if (superMutantNameRepository.count() == 0) {
            List<SuperMutantName> superMutantNames = List.of(
                new SuperMutantName("Bashing"),
                new SuperMutantName("Beast"),
                new SuperMutantName("Biter"),
                new SuperMutantName("Blunt"),
                new SuperMutantName("Bones"),
                new SuperMutantName("Boulder"),
                new SuperMutantName("Brick"),
                new SuperMutantName("Brute"),
                new SuperMutantName("Bull"),
                new SuperMutantName("Chopper"),
                new SuperMutantName("Claw"),
                new SuperMutantName("Clobber"),
                new SuperMutantName("Crush"),
                new SuperMutantName("Gore"),
                new SuperMutantName("Grapple"),
                new SuperMutantName("Grum"),
                new SuperMutantName("Grunt"),
                new SuperMutantName("Guts"),
                new SuperMutantName("Hammer"),
                new SuperMutantName("Heavy"),
                new SuperMutantName("Iron"),
                new SuperMutantName("Jagged"),
                new SuperMutantName("Knuckles"),
                new SuperMutantName("Lunk"),
                new SuperMutantName("Mangle"),
                new SuperMutantName("Maul"),
                new SuperMutantName("Meat"),
                new SuperMutantName("Moose"),
                new SuperMutantName("Muck"),
                new SuperMutantName("Nails"),
                new SuperMutantName("Oaf"),
                new SuperMutantName("Pound"),
                new SuperMutantName("Rage"),
                new SuperMutantName("Ram"),
                new SuperMutantName("Raw"),
                new SuperMutantName("Rend"),
                new SuperMutantName("Rip"),
                new SuperMutantName("Rock"),
                new SuperMutantName("Scab"),
                new SuperMutantName("Sledge"),
                new SuperMutantName("Smash"),
                new SuperMutantName("Spike"),
                new SuperMutantName("Stone"),
                new SuperMutantName("Stump"),
                new SuperMutantName("Tank"),
                new SuperMutantName("Thump"),
                new SuperMutantName("Tough"),
                new SuperMutantName("Trash"),
                new SuperMutantName("Wreck"),
                new SuperMutantName("Yuk")
                );
            superMutantNameRepository.saveAll(superMutantNames);
        }
    }

    private void initFirstNames() {
        if (firstNameRepository.count() == 0) {
            List<FirstName> firstNames = List.of(
                // Hommes
                new FirstName("Aaron", Gender.MALE),
                new FirstName("Adam", Gender.MALE),
                new FirstName("Alan", Gender.MALE),
                new FirstName("Arthur", Gender.MALE),
                new FirstName("Benjamin", Gender.MALE),
                new FirstName("Billy", Gender.MALE),
                new FirstName("Bobby", Gender.MALE),
                new FirstName("Brandon", Gender.MALE),
                new FirstName("Brian", Gender.MALE),
                new FirstName("Bruce", Gender.MALE),
                new FirstName("Carl", Gender.MALE),
                new FirstName("Chester", Gender.MALE),
                new FirstName("Craig", Gender.MALE),
                new FirstName("David", Gender.MALE),
                new FirstName("Davy", Gender.MALE),
                new FirstName("Dennis", Gender.MALE),
                new FirstName("Donald", Gender.MALE),
                new FirstName("Duke", Gender.MALE),
                new FirstName("Ed", Gender.MALE),
                new FirstName("Eugene", Gender.MALE),
                new FirstName("Francis", Gender.MALE),
                new FirstName("Frank", Gender.MALE),
                new FirstName("Gabriel", Gender.MALE),
                new FirstName("George", Gender.MALE),
                new FirstName("Gregory", Gender.MALE),
                new FirstName("Hank", Gender.MALE),
                new FirstName("Harold", Gender.MALE),
                new FirstName("Istvan", Gender.MALE),
                new FirstName("Jason", Gender.MALE),
                new FirstName("Jeffrey", Gender.MALE),
                new FirstName("Jerry", Gender.MALE),
                new FirstName("Joseph", Gender.MALE),
                new FirstName("Juan", Gender.MALE),
                new FirstName("Justin", Gender.MALE),
                new FirstName("Luke", Gender.MALE),
                new FirstName("Lyle", Gender.MALE),
                new FirstName("Mickael", Gender.MALE),
                new FirstName("Nathan", Gender.MALE),
                new FirstName("Norman", Gender.MALE),
                new FirstName("Otis", Gender.MALE),
                new FirstName("Patrick", Gender.MALE),
                new FirstName("Paul", Gender.MALE),
                new FirstName("Peter", Gender.MALE),
                new FirstName("Robert", Gender.MALE),
                new FirstName("Roy", Gender.MALE),
                new FirstName("Russell", Gender.MALE),
                new FirstName("Ryan", Gender.MALE),
                new FirstName("Scott", Gender.MALE),
                new FirstName("Sean", Gender.MALE),
                new FirstName("Stephen", Gender.MALE),
                new FirstName("Steven", Gender.MALE),
                new FirstName("Thomas", Gender.MALE),
                new FirstName("Timothy", Gender.MALE),
                new FirstName("Walter", Gender.MALE),
                new FirstName("Wes", Gender.MALE),
                new FirstName("William", Gender.MALE),
                new FirstName("Willie", Gender.MALE),


                // Femmes
                new FirstName("Alice", Gender.FEMALE),
                new FirstName("Amanda", Gender.FEMALE),
                new FirstName("Amber", Gender.FEMALE),
                new FirstName("Amy", Gender.FEMALE),
                new FirstName("Ann", Gender.FEMALE),
                new FirstName("Anna", Gender.FEMALE),
                new FirstName("Axelle", Gender.FEMALE),
                new FirstName("Betty", Gender.FEMALE),
                new FirstName("Betty", Gender.FEMALE),
                new FirstName("Brittany", Gender.FEMALE),
                new FirstName("Catherine", Gender.FEMALE),
                new FirstName("Charlotte", Gender.FEMALE),
                new FirstName("Cheryl", Gender.FEMALE),
                new FirstName("Chistina", Gender.FEMALE),
                new FirstName("Deborah", Gender.FEMALE),
                new FirstName("Debra", Gender.FEMALE),
                new FirstName("Denise", Gender.FEMALE),
                new FirstName("Diane", Gender.FEMALE),
                new FirstName("Dixie", Gender.FEMALE),
                new FirstName("Donna", Gender.FEMALE),
                new FirstName("Doris", Gender.FEMALE),
                new FirstName("Dorothy", Gender.FEMALE),
                new FirstName("Emma", Gender.FEMALE),
                new FirstName("Gloria", Gender.FEMALE),
                new FirstName("Grace", Gender.FEMALE),
                new FirstName("Hannah", Gender.FEMALE),
                new FirstName("Hazel", Gender.FEMALE),
                new FirstName("Helen", Gender.FEMALE),
                new FirstName("Janet", Gender.FEMALE),
                new FirstName("Jennifer", Gender.FEMALE),
                new FirstName("Judith", Gender.FEMALE),
                new FirstName("Julia", Gender.FEMALE),
                new FirstName("Julie", Gender.FEMALE),
                new FirstName("June", Gender.FEMALE),
                new FirstName("Karen", Gender.FEMALE),
                new FirstName("Kathy", Gender.FEMALE),
                new FirstName("Lauren", Gender.FEMALE),
                new FirstName("Linda", Gender.FEMALE),
                new FirstName("Line", Gender.FEMALE),
                new FirstName("Lisa", Gender.FEMALE),
                new FirstName("Loretta", Gender.FEMALE),
                new FirstName("Lori", Gender.FEMALE),
                new FirstName("Lucy", Gender.FEMALE),
                new FirstName("Maeve", Gender.FEMALE),
                new FirstName("Maria", Gender.FEMALE),
                new FirstName("Marie", Gender.FEMALE),
                new FirstName("Mary", Gender.FEMALE),
                new FirstName("Maryline", Gender.FEMALE),
                new FirstName("Melissa", Gender.FEMALE),
                new FirstName("Michelle", Gender.FEMALE),
                new FirstName("Mildred", Gender.FEMALE),
                new FirstName("Mina", Gender.FEMALE),
                new FirstName("Nancy", Gender.FEMALE),
                new FirstName("Nathalie", Gender.FEMALE),
                new FirstName("Nicole", Gender.FEMALE),
                new FirstName("Patricia", Gender.FEMALE),
                new FirstName("Peggy", Gender.FEMALE),
                new FirstName("Rebecca", Gender.FEMALE),
                new FirstName("Rose", Gender.FEMALE),
                new FirstName("Sara", Gender.FEMALE),
                new FirstName("Sarah", Gender.FEMALE),
                new FirstName("Tiffany", Gender.FEMALE),
                new FirstName("Velma", Gender.FEMALE),
                new FirstName("Véronique", Gender.FEMALE),
                new FirstName("Victoria", Gender.FEMALE),
                new FirstName("Virginia", Gender.FEMALE),
                new FirstName("Wilhelmina", Gender.FEMALE),


                // Neutres / Épicènes
                new FirstName("Ashley", Gender.NEUTRAL),
                new FirstName("Carol", Gender.NEUTRAL),
                new FirstName("Cassidy", Gender.NEUTRAL),
                new FirstName("Charlie", Gender.NEUTRAL),
                new FirstName("Christian", Gender.NEUTRAL),
                new FirstName("Dallas", Gender.NEUTRAL),
                new FirstName("Dylan", Gender.NEUTRAL),
                new FirstName("Frances", Gender.NEUTRAL),
                new FirstName("Frankie", Gender.NEUTRAL),
                new FirstName("Jesse", Gender.NEUTRAL),
                new FirstName("Jordan", Gender.NEUTRAL),
                new FirstName("Joyce", Gender.NEUTRAL),
                new FirstName("Lee", Gender.NEUTRAL),
                new FirstName("Madison", Gender.NEUTRAL),
                new FirstName("Marion", Gender.NEUTRAL),
                new FirstName("Robin", Gender.NEUTRAL),
                new FirstName("Sammy", Gender.NEUTRAL),
                new FirstName("Sunny", Gender.NEUTRAL),
                new FirstName("Terry", Gender.NEUTRAL),
                new FirstName("Trace", Gender.NEUTRAL),
                new FirstName("Tyler", Gender.NEUTRAL)
                );

            firstNameRepository.saveAll(firstNames);
            System.out.println("DataInitializer : 30 prénoms insérés avec succès.");
        }
    }

    private void initLastNames() {
        if (lastNameRepository.count() == 0) {
            List<LastName> lastNames = List.of(
                new LastName("Adams"),
                new LastName("Allen"),
                new LastName("Applegate"),
                new LastName("Armstrong"),
                new LastName("Blackwood"),
                new LastName("Brimley"),
                new LastName("Bush"),
                new LastName("Calloway"),
                new LastName("Clark"),
                new LastName("Copper"),
                new LastName("Cox"),
                new LastName("Cranston"),
                new LastName("Englund"),
                new LastName("Finch"),
                new LastName("Ford"),
                new LastName("Foster"),
                new LastName("Fox"),
                new LastName("Garrison"),
                new LastName("Graham"),
                new LastName("Hancock"),
                new LastName("Hart"),
                new LastName("Holloway"),
                new LastName("Jackson"),
                new LastName("Johnson"),
                new LastName("Jordan"),
                new LastName("King"),
                new LastName("Kingsley"),
                new LastName("Lewis"),
                new LastName("Li"),
                new LastName("Lyons"),
                new LastName("MacIntyre"),
                new LastName("Mercer"),
                new LastName("Miller"),
                new LastName("Montgomery"),
                new LastName("Morgan"),
                new LastName("Newton"),
                new LastName("Overton"),
                new LastName("Owens"),
                new LastName("Parker"),
                new LastName("Pendleton"),
                new LastName("Perry"),
                new LastName("Peters"),
                new LastName("Pritchard"),
                new LastName("Radford"),
                new LastName("Robert"),
                new LastName("Rusty"),
                new LastName("Sanders"),
                new LastName("Scrapper"),
                new LastName("Snow"),
                new LastName("Steel"),
                new LastName("Sterling"),
                new LastName("Stone"),
                new LastName("Stuart"),
                new LastName("Suton"),
                new LastName("Thorne"),
                new LastName("Turner"),
                new LastName("Underwood"),
                new LastName("Vance"),
                new LastName("Vancleave"),
                new LastName("Wainwright"),
                new LastName("Walker"),
                new LastName("Webster"),
                new LastName("West"),
                new LastName("Winter"),
                new LastName("Young")
                );

            lastNameRepository.saveAll(lastNames);
            System.out.println("DataInitializer : 30 noms de famille insérés avec succès.");
        }
    }
}
