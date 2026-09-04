package eu.nicosworld.falloutback.infrastructure.persistence.entity.namegenerator;

import jakarta.persistence.*;

@Entity
@Table(name = "super_mutant_name")
public class SuperMutantName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    public SuperMutantName() {}

    public SuperMutantName(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
