package eu.nicosworld.falloutback.infrastructure.web.dto.character;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Skills;

public record SkillsDto(

        int energyWeapons,
        boolean isEnergyWeaponsTagSkill,

        int meleeWeapons,
        boolean isMeleeWeaponsTagSkill,

        int smallGuns,
        boolean isSmallGunsTagSkill,

        int bigGuns,
        boolean isBigGunsTagSkill,

        int athletics,
        boolean isAthleticsTagSkill,

        int lockpick,
        boolean isLockpickTagSkill,

        int speech,
        boolean isSpeechTagSkill,

        int sneak,
        boolean isSneakTagSkill,

        int explosives,
        boolean isExplosivesTagSkill,

        int unarmed,
        boolean isUnarmedTagSkill,

        int medicine,
        boolean isMedicineTagSkill,

        int pilot,
        boolean isPilotTagSkill,

        int throwing,
        boolean isThrowingTagSkill,

        int repair,
        boolean isRepairTagSkill,

        int science,
        boolean isScienceTagSkill,

        int survival,
        boolean isSurvivalTagSkill,

        int barter,
        boolean isBarterTagSkill

) {

    public static SkillsDto mapFromEntity(Skills skills) {
        return new SkillsDto(
                skills.getEnergyWeapons(),
                skills.isEnergyWeaponsTagSkill(),

                skills.getMeleeWeapons(),
                skills.isMeleeWeaponsTagSkill(),

                skills.getSmallGuns(),
                skills.isSmallGunsTagSkill(),

                skills.getBigGuns(),
                skills.isBigGunsTagSkill(),

                skills.getAthletics(),
                skills.isAthleticsTagSkill(),

                skills.getLockpick(),
                skills.isLockpickTagSkill(),

                skills.getSpeech(),
                skills.isSpeechTagSkill(),

                skills.getSneak(),
                skills.isSneakTagSkill(),

                skills.getExplosives(),
                skills.isExplosivesTagSkill(),

                skills.getUnarmed(),
                skills.isUnarmedTagSkill(),

                skills.getMedicine(),
                skills.isMedicineTagSkill(),

                skills.getPilot(),
                skills.isPilotTagSkill(),

                skills.getThrowing(),
                skills.isThrowingTagSkill(),

                skills.getRepair(),
                skills.isRepairTagSkill(),

                skills.getScience(),
                skills.isScienceTagSkill(),

                skills.getSurvival(),
                skills.isSurvivalTagSkill(),

                skills.getBarter(),
                skills.isBarterTagSkill()
        );
    }
}

