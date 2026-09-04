package eu.nicosworld.falloutback.infrastructure.persistence.entity.namegenerator;

import jakarta.persistence.*;

@Entity
@Table(name = "last_name")
public class LastName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    public LastName() {}

    public LastName(String name) {
        this.name = name;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
