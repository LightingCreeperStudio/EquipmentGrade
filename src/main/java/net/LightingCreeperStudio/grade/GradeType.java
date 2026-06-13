package net.LightingCreeperStudio.grade;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * 物品等级枚举
 * 属性调整值计算:
 * - 不入品及以下: 每底一品 -1
 * - 凡品: 0 (原版属性)
 * - 下品及以上: 每升一级 +0.5
 * 
 * 耐久调整: 每级 ±50
 */
public enum GradeType {
    // 名称, 属性调整值(相对于凡品), 显示名称, 附魔上限(-1=不限), 铁砧经验上限(-1=永不过于昂贵)
    // 废品~上品: 永不过于昂贵，有附魔数量上限，经验显示封顶40
    // 极品: 经验≥50 触发过于昂贵，无附魔数量上限
    // 完美: 经验≥75 触发过于昂贵，无附魔数量上限
    // 灵宝★及以上: 永不过于昂贵，经验显示封顶75
    WASTE("废品", -3, "§7废品", 0, -1),
    DEFECTIVE("残次品", -2, "§8残次品", 1, -1),
    UNRANKED("不入品", -1, "§f不入品", 2, -1),
    COMMON("凡品", 0, "§a凡品", 4, -1),
    LOW("下品", 0.5, "§b下品", 5, -1),
    MEDIUM("中品", 1.0, "§3中品", 6, -1),
    HIGH("上品", 1.5, "§9上品", 7, -1),
    EXCELLENT("极品", 2.0, "§d极品", 8, 50),
    PERFECT("完美", 2.5, "§e完美", -1, 75),
    SPIRIT_TREASURE_1("灵宝★", 3.0, "§6灵宝★", -1, -1),
    SPIRIT_TREASURE_2("灵宝★★", 3.5, "§6灵宝★★", -1, -1),
    SPIRIT_TREASURE_3("灵宝★★★", 4.0, "§6灵宝★★★", -1, -1),
    SUPREME_TREASURE_1("至宝★", 4.5, "§5至宝★", -1, -1),
    SUPREME_TREASURE_2("至宝★★", 5.0, "§5至宝★★", -1, -1),
    SUPREME_TREASURE_3("至宝★★★", 5.5, "§5至宝★★★", -1, -1),
    CELESTIAL("先天", 6.0, "§c先天", -1, -1),
    UNKNOWN("未知", 0, "§4未知", -1, -1);

    private final String name;
    private final double attributeOffset;
    private final String displayName;
    private final TextColor color;
    private final int maxEnchantCount;
    private final int maxAnvilCost;

    GradeType(String name, double attributeOffset, String displayName, int maxEnchantCount, int maxAnvilCost) {
        this.name = name;
        this.attributeOffset = attributeOffset;
        this.displayName = displayName;
        this.color = parseColor(displayName);
        this.maxEnchantCount = maxEnchantCount;
        this.maxAnvilCost = maxAnvilCost;
    }

    /**
     * 从 displayName 中提取 § 颜色代码并转换为 Adventure TextColor
     */
    private static TextColor parseColor(String displayName) {
        if (displayName != null && displayName.startsWith("§") && displayName.length() >= 2) {
            char code = displayName.charAt(1);
            return switch (code) {
                case '0' -> NamedTextColor.BLACK;
                case '1' -> NamedTextColor.DARK_BLUE;
                case '2' -> NamedTextColor.DARK_GREEN;
                case '3' -> NamedTextColor.DARK_AQUA;
                case '4' -> NamedTextColor.DARK_RED;
                case '5' -> NamedTextColor.DARK_PURPLE;
                case '6' -> NamedTextColor.GOLD;
                case '7' -> NamedTextColor.GRAY;
                case '8' -> NamedTextColor.DARK_GRAY;
                case '9' -> NamedTextColor.BLUE;
                case 'a' -> NamedTextColor.GREEN;
                case 'b' -> NamedTextColor.AQUA;
                case 'c' -> NamedTextColor.RED;
                case 'd' -> NamedTextColor.LIGHT_PURPLE;
                case 'e' -> NamedTextColor.YELLOW;
                case 'f' -> NamedTextColor.WHITE;
                default -> NamedTextColor.WHITE;
            };
        }
        return NamedTextColor.WHITE;
    }

    /**
     * 获取等级对应的颜色
     */
    public TextColor getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    /**
     * 获取相对于凡品的属性调整值
     */
    public double getAttributeOffset() {
        return attributeOffset;
    }

    /**
     * 获取属性调整值的整数部分
     */
    public String getAttributeOffsetString() {
        if (this == UNKNOWN) {
            return "自定义";
        }
        if (attributeOffset == (int) attributeOffset) {
            return String.valueOf((int) attributeOffset);
        }
        return String.valueOf(attributeOffset);
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 获取耐久调整值
     */
    public int getDurabilityOffset() {
        if (this == UNKNOWN) {
            return 0; // 未知等级需要自定义
        }
        // 等级级差 × 50
        return (int) Math.round(Math.abs(attributeOffset) * 50);
    }

    /**
     * 获取附魔上限
     * @return 附魔上限,-1 表示不限制
     */
    public int getMaxEnchantCount() {
        return maxEnchantCount;
    }

    /**
     * 获取铁砧经验上限
     * @return 铁砧经验上限,-1 表示不限制
     */
    public int getMaxAnvilCost() {
        return maxAnvilCost < 0 ? Integer.MAX_VALUE : maxAnvilCost;
    }

    /**
     * 获取经验扣除封顶值
     * 仅对永不过于昂贵的品级有效
     * 极品和完美由"过于昂贵"机制限制，不需要额外封顶
     * @return 经验扣除封顶值，-1 表示不需要封顶
     */
    public int getDeductionCap() {
        if (this.ordinal() <= HIGH.ordinal()) {
            return 40;  // 废品~上品
        }
        if (this == EXCELLENT || this == PERFECT) {
            return -1;  // 极品/完美:由"过于昂贵"机制限制
        }
        return 75;  // 灵宝★及以上
    }

    public static GradeType fromName(String name) {
        for (GradeType type : values()) {
            if (type.name.equals(name) || type.name().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
