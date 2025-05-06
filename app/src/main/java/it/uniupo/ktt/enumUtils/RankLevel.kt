package it.uniupo.ktt.enumUtils

import it.uniupo.ktt.R

enum class RankLevel(val minPt: Int, val maxPt: Int, val label: String, val drawableId: Int) {
    LEVEL_1(0, 99, "Level 1", R.drawable.tier_new),
    LEVEL_2(100, 499, "Level 2", R.drawable.tier_good),
    LEVEL_3(500, 1499, "Level 3", R.drawable.tier_boxe),
    LEVEL_4(1500, 4999, "Level 4", R.drawable.tier_fire),
    LEVEL_5(5000, 19999, "Level 5", R.drawable.tier_medal),
    LEVEL_6(20000, 99999, "Level 6", R.drawable.tier_ruby),
    LEVEL_7(100000, 800000, "Level 7", R.drawable.tier_diamond);

    companion object {
        fun findLevelByPoint(points: Int): RankLevel {
            for (level in entries.reversed()) {
                if (points >= level.minPt) {
                    return level
                }
            }
            // Fallback caso comportamento inaspettato
            return entries.first()
        }
    }
}