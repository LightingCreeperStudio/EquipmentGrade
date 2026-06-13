# EquipmentGrade

A Minecraft server item grading plugin that assigns random grades and attribute bonuses to equipment. Supports multiple sources including crafting, anvils, enchanting, fishing, entity drops, loot chests, and villager trading.

## Requirements

This plugin requires the server to be running **Paper 1.21 or higher** (or any core compatible with Paper API 1.21+).

Supported server cores include:
- **Paper** (recommended)
- **Purpur** (enhanced fork of Paper, fully compatible)
- **Folia** (Paper's multithreaded fork – compatibility should be verified)
- Any other core built on **Paper API 1.21+**

**CraftBukkit** and vanilla **Spigot** are NOT supported, as they do not include the Paper API 1.21+ interfaces such as `AnvilView`.

## Features

### 16-Tier Grading System

The plugin implements a comprehensive 16-tier grading system that adds depth and rarity to equipment. The tiers are:

WASTE → DEFECTIVE → UNRANKED → COMMON → LOW → MEDIUM → HIGH → EXCELLENT → PERFECT → SPIRIT_TREASURE_1 → SPIRIT_TREASURE_2 → SPIRIT_TREASURE_3 → SUPREME_TREASURE_1 → SUPREME_TREASURE_2 → SUPREME_TREASURE_3 → CELESTIAL

Each tier has a unique color scheme and provides different levels of attribute bonuses.

### Attribute Bonuses

Higher grades provide increased combat effectiveness through attribute modifiers:
- Attack Damage bonus
- Armor value bonus
- Attack Speed bonus
- Other equipment-specific attributes

The attribute values scale with the grade tier, making higher-tier equipment significantly more powerful.

### Source Tracking

Every graded item displays its acquisition source, allowing players to identify where the item came from. Supported sources include:
- Crafting (workbench synthesis)
- Anvil (repair/enchant operations)
- Fishing (fishing rod catches)
- Loot Chest (dungeon/structure chests)
- Entity Drop (mob drops)
- Villager (villager trading)
- Wandering Trader
- Enchanting Table

### Anti-Grinding Mechanism

To prevent players from repeatedly crafting or anvil-operating items to "farm" high grades, the plugin implements a preview system:
- During the preview phase, items display "Level: ???" instead of the actual grade
- The actual grade is only determined when the player retrieves the item from the crafting result slot or anvil output slot
- This ensures each item acquisition is a one-time random roll that cannot be repeated

### Anvil Mechanics

The anvil system has been carefully balanced:

Upgrade Chance: 5% chance for the item grade to increase by one tier after anvil operations.

Downgrade Chance: 25% chance for normal items to drop by one tier. For high-tier items (SUPREME_TREASURE and above), this is reduced to only 2%.

Repair Cost Cap:
- WASTE to HIGH tiers: repair cost capped at 40 levels
- SPIRIT_TREASURE and above: repair cost capped at 75 levels

Too Expensive Mechanism:
- EXCELLENT tier: triggers "Too Expensive" when repair cost reaches 50 or more
- PERFECT tier: triggers "Too Expensive" when repair cost reaches 75 or more
- WASTE to HIGH and SPIRIT_TREASURE and above: never trigger "Too Expensive"

### Enchantment Limits

Lower-grade equipment has strict enchantment slot limits to maintain game balance:
- WASTE: 0 enchantments allowed (completely prohibits enchanting)
- DEFECTIVE: maximum 1 enchantment
- UNRANKED: maximum 2 enchantments
- COMMON: maximum 3 enchantments
- LOW: maximum 4 enchantments
- MEDIUM: maximum 5 enchantments
- HIGH: maximum 6 enchantments
- EXCELLENT: maximum 7 enchantments
- PERFECT: maximum 8 enchantments
- SPIRIT_TREASURE and above: unlimited enchantments

When an item's grade drops and the enchantment count exceeds the new limit, a random enchantment is removed.

### Auto-Naming

Items of PERFECT grade and above can be automatically assigned a random equipment name, adding uniqueness and prestige to high-tier finds. The naming system uses a curated list of equipment names that are applied when the item is first graded.

### API Support

The plugin provides a GradeAPI class for other plugins to integrate with. This allows developers to programmatically check grades, set grades, remove grades, and roll random grades for items. This is useful for custom gameplay mechanics, quests, or other plugin interoperations.

## Requirements

Server: Paper 1.21 or higher (requires paper-api 1.21+ for AnvilView API support)

Dependencies: PlaceholderAPI (optional – required only if you want to use the placeholder variables in other plugins like holograms or scoreboards)

## Installation

Step 1: Download the EquipmentGrade.jar file from the releases page.

Step 2: Place the jar file into your server's plugins/ folder.

Step 3: Restart or reload the server. On first run, the plugin will generate a config.yml file in plugins/EquipmentGrade/.

Step 4 (optional): Install PlaceholderAPI and run /papi download to enable placeholder support.

## Configuration

The plugin generates a config.yml file with two main sections:

sources – Controls which item sources have grade assignment enabled. You can disable grading for specific sources like crafting, fishing, loot chests, entity drops, villager trades, wandering trader, anvil operations, or enchanting table.

probabilities – Defines the probability distribution for each source. Each probability value is a weight (0-100) and the plugin automatically normalizes them. This means you do not need to make the values sum to 100; the plugin handles the math.

For example, if you want crafting to produce mostly common and low-tier items with very rare high-tier items, you would set higher values for COMMON and LOW, and very small decimal values for SPIRIT_TREASURE and CELESTIAL.

The same probability structure applies to all sources: crafting, fishing, loot_chest, entity_drop, villager, wandering, and enchanting. The anvil section uses different parameters (upgrade chance, downgrade chance, and high-grade downgrade chance) since anvil grades are calculated based on the input item's current grade rather than random rolls.

After editing the config, run /grade reload to apply changes without restarting.

## Commands

/grade reload – Reloads the configuration file. Requires permission equipmentgrade.reload.

/grade give <player> <grade> – Gives the specified player an item with the specified grade. Requires permission equipmentgrade.give.

/grade check – Checks the grade of the item currently held by the player. Requires permission equipmentgrade.check.

/grade set <grade> – Sets the grade of the held item to the specified tier. Requires permission equipmentgrade.set.

/grade remove – Removes the grade from the held item, resetting it to ungraded status. Requires permission equipmentgrade.remove.

/grade list – Lists all available grades in the system. Requires permission equipmentgrade.list.

## Grade Details

WASTE – Gray – The lowest possible tier. Cannot be enchanted. Anvil repair cost capped at 40. Mostly a penalty tier for unlucky rolls.

DEFECTIVE – Red – Very poor quality. Maximum 1 enchantment. Anvil repair cost capped at 40.

UNRANKED – Gold – Below-average quality. Maximum 2 enchantments. Anvil repair cost capped at 40.

COMMON – Yellow – Standard quality, the most commonly obtained tier from most sources. Maximum 3 enchantments. Anvil repair cost capped at 40.

LOW – Green – Slightly below average. Maximum 4 enchantments. Anvil repair cost capped at 40.

MEDIUM – Blue – Average quality. Maximum 5 enchantments. Anvil repair cost capped at 40.

HIGH – Purple – Above average. Maximum 6 enchantments. Anvil repair cost capped at 40.

EXCELLENT – Pink – High quality. Maximum 7 enchantments. Anvil triggers "Too Expensive" at 50 repair cost.

PERFECT – Gold – Exceptional quality. Maximum 8 enchantments. Anvil triggers "Too Expensive" at 75 repair cost. Eligible for auto-naming.

SPIRIT_TREASURE (3 sub-tiers) – Aqua – Rare spiritual treasure-grade items. Unlimited enchantments. Anvil repair cost capped at 75. Eligible for auto-naming.

SUPREME_TREASURE (3 sub-tiers) – Red – Legendary supreme treasure-grade items. Unlimited enchantments. Anvil repair cost capped at 75. Downgrade chance reduced to 2%. Eligible for auto-naming.

CELESTIAL – Rainbow – The highest possible tier. Mythical quality. Unlimited enchantments. Anvil repair cost capped at 75. Downgrade chance reduced to 2%. Always eligible for auto-naming.

UNKNOWN – Dark Purple – A special internal tier used for error states or unidentifiable items. Not normally obtainable by players.

## PlaceholderAPI Integration

If PlaceholderAPI is installed, the following placeholders are available:

%equipmentgrade_grade% – Returns the grade name of the item held by the player.

%equipmentgrade_grade_color% – Returns the color code of the grade of the held item.

%equipmentgrade_source% – Returns the source label of the held item (e.g., "Crafting", "Fishing", etc.).

These placeholders can be used in scoreboards, holograms, chat plugins, or any plugin that supports PlaceholderAPI.

## API Usage for Developers

Other plugin developers can access the grading system through the GradeAPI class. The API provides methods to:

Check whether an item has a grade assigned.

Get the grade of an item.

Set the grade of an item programmatically.

Remove the grade from an item.

Roll a random grade for a given source (using the probability distribution from config).

To obtain the API instance, use the getAPI() method from the main EquipmentGrade class. This ensures proper initialization and access to all grading functions.

## Known Issues

None at this time. If you encounter bugs, please report them on the issue tracker.

## License

This project is licensed under the MIT License. You are free to modify, distribute, and use this plugin for any purpose.

## Author

Developed by LightingCreeperStudio.
