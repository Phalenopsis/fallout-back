package eu.nicosworld.falloutback.infrastructure.persistence.entity.character;

import eu.nicosworld.falloutback.infrastructure.web.dto.character.SkillsDto;
import jakarta.persistence.*;

@Entity
public class Skills {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Character character;

    private int energyWeapons = 0;
    private boolean isEnergyWeaponsTagSkill = false;

    private int meleeWeapons = 0;
    private boolean isMeleeWeaponsTagSkill = false;

    private int smallGuns = 0;
    private boolean isSmallGunsTagSkill = false;

    private int bigGuns = 0;
    private boolean isBigGunsTagSkill = false;

    private int athletics = 0;
    private boolean isAthleticsTagSkill = false;

    private int lockpick = 0;
    private boolean isLockpickTagSkill = false;

    private int speech = 0;
    private boolean isSpeechTagSkill = false;

    private int sneak = 0;
    private boolean isSneakTagSkill = false;

    private int explosives = 0;
    private boolean isExplosivesTagSkill = false;

    private int unarmed = 0;
    private boolean isUnarmedTagSkill = false;

    private int medicine = 0;
    private boolean isMedicineTagSkill = false;

    private int pilot = 0;
    private boolean isPilotTagSkill = false;

    private int throwing = 0;
    private boolean isThrowingTagSkill = false;

    private int repair = 0;
    private boolean isRepairTagSkill = false;

    private int science = 0;
    private boolean isScienceTagSkill = false;

    private int survival = 0;
    private boolean isSurvivalTagSkill = false;

    private int barter = 0;
    private boolean isBarterTagSkill = false;

    public Skills() {

    }

    public Skills(SkillsDto dto) {
        energyWeapons = dto.energyWeapons();
        isEnergyWeaponsTagSkill = dto.isEnergyWeaponsTagSkill();

        meleeWeapons = dto.meleeWeapons();
        isMeleeWeaponsTagSkill = dto.isMeleeWeaponsTagSkill();

        smallGuns = dto.smallGuns();
        isSmallGunsTagSkill = dto.isSmallGunsTagSkill();

        bigGuns = dto.bigGuns();
        isBigGunsTagSkill = dto.isBigGunsTagSkill();

        athletics = dto.athletics();
        isAthleticsTagSkill = dto.isAthleticsTagSkill();

        lockpick = dto.lockpick();
        isLockpickTagSkill = dto.isLockpickTagSkill();

        speech = dto.speech();
        isSpeechTagSkill = dto.isSpeechTagSkill();

        sneak = dto.sneak();
        isSneakTagSkill = dto.isSneakTagSkill();

        explosives = dto.explosives();
        isExplosivesTagSkill = dto.isExplosivesTagSkill();

        unarmed = dto.unarmed();
        isUnarmedTagSkill = dto.isUnarmedTagSkill();

        medicine = dto.medicine();
        isMedicineTagSkill = dto.isMedicineTagSkill();

        pilot = dto.pilot();
        isPilotTagSkill = dto.isPilotTagSkill();

        throwing = dto.throwing();
        isThrowingTagSkill = dto.isThrowingTagSkill();

        repair = dto.repair();
        isRepairTagSkill = dto.isRepairTagSkill();

        science = dto.science();
        isScienceTagSkill = dto.isScienceTagSkill();

        survival = dto.survival();
        isSurvivalTagSkill = dto.isSurvivalTagSkill();

        barter = dto.barter();
        isBarterTagSkill = dto.isBarterTagSkill();
    }

    public void update(SkillsDto dto) {
        energyWeapons = dto.energyWeapons();
        isEnergyWeaponsTagSkill = dto.isEnergyWeaponsTagSkill();

        meleeWeapons = dto.meleeWeapons();
        isMeleeWeaponsTagSkill = dto.isMeleeWeaponsTagSkill();

        smallGuns = dto.smallGuns();
        isSmallGunsTagSkill = dto.isSmallGunsTagSkill();

        bigGuns = dto.bigGuns();
        isBigGunsTagSkill = dto.isBigGunsTagSkill();

        athletics = dto.athletics();
        isAthleticsTagSkill = dto.isAthleticsTagSkill();

        lockpick = dto.lockpick();
        isLockpickTagSkill = dto.isLockpickTagSkill();

        speech = dto.speech();
        isSpeechTagSkill = dto.isSpeechTagSkill();

        sneak = dto.sneak();
        isSneakTagSkill = dto.isSneakTagSkill();

        explosives = dto.explosives();
        isExplosivesTagSkill = dto.isExplosivesTagSkill();

        unarmed = dto.unarmed();
        isUnarmedTagSkill = dto.isUnarmedTagSkill();

        medicine = dto.medicine();
        isMedicineTagSkill = dto.isMedicineTagSkill();

        pilot = dto.pilot();
        isPilotTagSkill = dto.isPilotTagSkill();

        throwing = dto.throwing();
        isThrowingTagSkill = dto.isThrowingTagSkill();

        repair = dto.repair();
        isRepairTagSkill = dto.isRepairTagSkill();

        science = dto.science();
        isScienceTagSkill = dto.isScienceTagSkill();

        survival = dto.survival();
        isSurvivalTagSkill = dto.isSurvivalTagSkill();

        barter = dto.barter();
        isBarterTagSkill = dto.isBarterTagSkill();
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
        if (character.getSkills() != this) {
            character.setSkills(this);
        }
    }

    public int getEnergyWeapons() {
        return energyWeapons;
    }

    public void setEnergyWeapons(int energyWeapons) {
        this.energyWeapons = energyWeapons;
    }

    public boolean isEnergyWeaponsTagSkill() {
        return isEnergyWeaponsTagSkill;
    }

    public void setEnergyWeaponsTagSkill(boolean energyWeaponsTagSkill) {
        isEnergyWeaponsTagSkill = energyWeaponsTagSkill;
    }

    public int getMeleeWeapons() {
        return meleeWeapons;
    }

    public void setMeleeWeapons(int meleeWeapons) {
        this.meleeWeapons = meleeWeapons;
    }

    public boolean isMeleeWeaponsTagSkill() {
        return isMeleeWeaponsTagSkill;
    }

    public void setMeleeWeaponsTagSkill(boolean meleeWeaponsTagSkill) {
        isMeleeWeaponsTagSkill = meleeWeaponsTagSkill;
    }

    public int getSmallGuns() {
        return smallGuns;
    }

    public void setSmallGuns(int smallGuns) {
        this.smallGuns = smallGuns;
    }

    public boolean isSmallGunsTagSkill() {
        return isSmallGunsTagSkill;
    }

    public void setSmallGunsTagSkill(boolean smallGunsTagSkill) {
        isSmallGunsTagSkill = smallGunsTagSkill;
    }

    public int getBigGuns() {
        return bigGuns;
    }

    public void setBigGuns(int bigGuns) {
        this.bigGuns = bigGuns;
    }

    public boolean isBigGunsTagSkill() {
        return isBigGunsTagSkill;
    }

    public void setBigGunsTagSkill(boolean bigGunsTagSkill) {
        isBigGunsTagSkill = bigGunsTagSkill;
    }

    public int getAthletics() {
        return athletics;
    }

    public void setAthletics(int athletics) {
        this.athletics = athletics;
    }

    public boolean isAthleticsTagSkill() {
        return isAthleticsTagSkill;
    }

    public void setAthleticsTagSkill(boolean athleticsTagSkill) {
        isAthleticsTagSkill = athleticsTagSkill;
    }

    public int getLockpick() {
        return lockpick;
    }

    public void setLockpick(int lockpick) {
        this.lockpick = lockpick;
    }

    public boolean isLockpickTagSkill() {
        return isLockpickTagSkill;
    }

    public void setLockpickTagSkill(boolean lockpickTagSkill) {
        isLockpickTagSkill = lockpickTagSkill;
    }

    public int getSpeech() {
        return speech;
    }

    public void setSpeech(int speech) {
        this.speech = speech;
    }

    public boolean isSpeechTagSkill() {
        return isSpeechTagSkill;
    }

    public void setSpeechTagSkill(boolean speechTagSkill) {
        isSpeechTagSkill = speechTagSkill;
    }

    public int getSneak() {
        return sneak;
    }

    public void setSneak(int sneak) {
        this.sneak = sneak;
    }

    public boolean isSneakTagSkill() {
        return isSneakTagSkill;
    }

    public void setSneakTagSkill(boolean sneakTagSkill) {
        isSneakTagSkill = sneakTagSkill;
    }

    public int getExplosives() {
        return explosives;
    }

    public void setExplosives(int explosives) {
        this.explosives = explosives;
    }

    public boolean isExplosivesTagSkill() {
        return isExplosivesTagSkill;
    }

    public void setExplosivesTagSkill(boolean explosivesTagSkill) {
        isExplosivesTagSkill = explosivesTagSkill;
    }

    public int getUnarmed() {
        return unarmed;
    }

    public void setUnarmed(int unarmed) {
        this.unarmed = unarmed;
    }

    public boolean isUnarmedTagSkill() {
        return isUnarmedTagSkill;
    }

    public void setUnarmedTagSkill(boolean unarmedTagSkill) {
        isUnarmedTagSkill = unarmedTagSkill;
    }

    public int getMedicine() {
        return medicine;
    }

    public void setMedicine(int medicine) {
        this.medicine = medicine;
    }

    public boolean isMedicineTagSkill() {
        return isMedicineTagSkill;
    }

    public void setMedicineTagSkill(boolean medicineTagSkill) {
        isMedicineTagSkill = medicineTagSkill;
    }

    public int getPilot() {
        return pilot;
    }

    public void setPilot(int pilot) {
        this.pilot = pilot;
    }

    public boolean isPilotTagSkill() {
        return isPilotTagSkill;
    }

    public void setPilotTagSkill(boolean pilotTagSkill) {
        isPilotTagSkill = pilotTagSkill;
    }

    public int getThrowing() {
        return throwing;
    }

    public void setThrowing(int throwing) {
        this.throwing = throwing;
    }

    public boolean isThrowingTagSkill() {
        return isThrowingTagSkill;
    }

    public void setThrowingTagSkill(boolean throwingTagSkill) {
        isThrowingTagSkill = throwingTagSkill;
    }

    public int getRepair() {
        return repair;
    }

    public void setRepair(int repair) {
        this.repair = repair;
    }

    public boolean isRepairTagSkill() {
        return isRepairTagSkill;
    }

    public void setRepairTagSkill(boolean repairTagSkill) {
        isRepairTagSkill = repairTagSkill;
    }

    public int getScience() {
        return science;
    }

    public void setScience(int science) {
        this.science = science;
    }

    public boolean isScienceTagSkill() {
        return isScienceTagSkill;
    }

    public void setScienceTagSkill(boolean scienceTagSkill) {
        isScienceTagSkill = scienceTagSkill;
    }

    public int getSurvival() {
        return survival;
    }

    public void setSurvival(int survival) {
        this.survival = survival;
    }

    public boolean isSurvivalTagSkill() {
        return isSurvivalTagSkill;
    }

    public void setSurvivalTagSkill(boolean survivalTagSkill) {
        isSurvivalTagSkill = survivalTagSkill;
    }

    public int getBarter() {
        return barter;
    }

    public void setBarter(int barter) {
        this.barter = barter;
    }

    public boolean isBarterTagSkill() {
        return isBarterTagSkill;
    }

    public void setBarterTagSkill(boolean barterTagSkill) {
        isBarterTagSkill = barterTagSkill;
    }
}

