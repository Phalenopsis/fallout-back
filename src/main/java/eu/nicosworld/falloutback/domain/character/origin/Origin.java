package eu.nicosworld.falloutback.domain.character.origin;

import java.util.List;
import java.util.Map;

public class Origin {
    private String name;
    private String nom;
    private List<String> histoire;
    private List<String> traitAChoisir;
    private List<String> trait;
    private Map<String, Integer> bonusCompetence;
    private List<StatModifier> modificateurStats;
    private List<StatLimiter> maximumStats;
    private List<String> immunite;
    private List<String> accessoireBras;
    private String handicap;
    private String malus;
    private String amelioration;
    private String atout;
    private Integer limitateurCompetence;
    private Limitation limitation;
    private Integer resistanceRadiation;
    private Map<String, String> reputation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<String> getHistoire() {
        return histoire;
    }

    public void setHistoire(List<String> histoire) {
        this.histoire = histoire;
    }

    public List<String> getTraitAChoisir() {
        return traitAChoisir;
    }

    public void setTraitAChoisir(List<String> traitAChoisir) {
        this.traitAChoisir = traitAChoisir;
    }

    public List<String> getTrait() {
        return trait;
    }

    public void setTrait(List<String> trait) {
        this.trait = trait;
    }

    public List<StatModifier> getModificateurStats() {
        return modificateurStats;
    }

    public void setModificateurStats(List<StatModifier> modificateurStats) {
        this.modificateurStats = modificateurStats;
    }

    public List<StatLimiter> getMaximumStats() {
        return maximumStats;
    }

    public void setMaximumStats(List<StatLimiter> maximumStats) {
        this.maximumStats = maximumStats;
    }

    public Map<String, Integer> getBonusCompetence() {
        return bonusCompetence;
    }

    public void setBonusCompetence(Map<String, Integer> bonusCompetence) {
        this.bonusCompetence = bonusCompetence;
    }

    public List<String> getImmunite() {
        return immunite;
    }

    public void setImmunite(List<String> immunite) {
        this.immunite = immunite;
    }

    public List<String> getAccessoireBras() {
        return accessoireBras;
    }

    public void setAccessoireBras(List<String> accessoireBras) {
        this.accessoireBras = accessoireBras;
    }

    public String getHandicap() {
        return handicap;
    }

    public void setHandicap(String handicap) {
        this.handicap = handicap;
    }

    public String getMalus() {
        return malus;
    }

    public void setMalus(String malus) {
        this.malus = malus;
    }

    public String getAmelioration() {
        return amelioration;
    }

    public void setAmelioration(String amelioration) {
        this.amelioration = amelioration;
    }

    public String getAtout() {
        return atout;
    }

    public void setAtout(String atout) {
        this.atout = atout;
    }

    public Integer getLimitateurCompetence() {
        return limitateurCompetence;
    }

    public void setLimitateurCompetence(Integer limitateurCompetence) {
        this.limitateurCompetence = limitateurCompetence;
    }

    public Limitation getLimitation() {
        return limitation;
    }

    public void setLimitation(Limitation limitation) {
        this.limitation = limitation;
    }

    public Integer getResistanceRadiation() {
        return resistanceRadiation;
    }

    public void setResistanceRadiation(Integer resistanceRadiation) {
        this.resistanceRadiation = resistanceRadiation;
    }

    public Map<String, String> getReputation() {
        return reputation;
    }

    public void setReputation(Map<String, String> reputation) {
        this.reputation = reputation;
    }
}
