package com.fan.edgex.config

object AppConfig {
    const val PREFS_NAME = "config"

    // Top-level flags
    const val GESTURES_ENABLED = "gestures_enabled"
    const val KEYS_ENABLED = "keys_enabled"
    const val DEBUG_MATRIX = "debug_matrix_enabled"
    const val FREEZER_ARC_DRAWER = "freezer_arc_drawer_enabled"
    const val FREEZER_APP_LIST = "freezer_app_list"
    const val HAS_MIGRATED_FREEZER_LIST = "has_migrated_freezer_list"
    const val THEME_PRESET = "theme_preset"
    const val THEME_CUSTOM_COLOR = "theme_custom_color"
    const val UI_ACCENT = "ui_accent"
    const val UI_DARK_MODE = "ui_dark_mode"
    const val UI_DENSITY = "ui_density"
    const val HAPTIC_FEEDBACK = "haptic_feedback_enabled"
    const val HAPTIC_FEEDBACK_TYPE = "haptic_feedback_type"
    const val FLUID_EFFECT_ENABLED = "fluid_effect_enabled"
    const val FLUID_EFFECT_COLOR = "fluid_effect_color"
    const val FLUID_EFFECT_COLOR_LEFT = "fluid_effect_color_left"
    const val FLUID_EFFECT_COLOR_RIGHT = "fluid_effect_color_right"
    const val FLUID_EFFECT_COLOR_TOP = "fluid_effect_color_top"
    const val FLUID_EFFECT_COLOR_BOTTOM = "fluid_effect_color_bottom"
    const val FLUID_EFFECT_SIZE = "fluid_effect_size"
    const val FLUID_EFFECT_ALPHA = "fluid_effect_alpha"
    const val FLUID_EFFECT_SIZE_DEFAULT = 63

    const val DYNAMIC_ISLAND_ENABLED = "dynamic_island_enabled"
    const val DYNAMIC_ISLAND_Y_OFFSET_DP = "dynamic_island_y_offset_dp"
    const val DYNAMIC_ISLAND_SIZE_DP = "dynamic_island_size_dp"
    const val DYNAMIC_ISLAND_SLIDE_DISTANCE_DP = "dynamic_island_slide_distance_dp"
    const val DYNAMIC_ISLAND_SHOW_PERCENTAGE = "dynamic_island_show_percentage"
    const val DYNAMIC_ISLAND_SCALE = "dynamic_island_scale"

    const val HAPTIC_FEEDBACK_TYPE_CLICK = "click"
    const val HAPTIC_FEEDBACK_TYPE_TICK = "tick"
    const val HAPTIC_FEEDBACK_TYPE_HEAVY_CLICK = "heavy_click"
    const val HAPTIC_FEEDBACK_TYPE_DOUBLE_CLICK = "double_click"

    const val CUSTOM_PANEL_ACTION = "custom_panel"
    const val SIDE_BAR_LEFT_ACTION = "side_bar:left"
    const val SIDE_BAR_RIGHT_ACTION = "side_bar:right"
    const val CUSTOM_PANEL_ROWS = 4
    const val CUSTOM_PANEL_COLUMNS = 4
    const val SIDE_BAR_SLOTS = 7

    val ZONES = listOf(
        "left_top",
        "left_mid",
        "left_bottom",
        "left",
        "right_top",
        "right_mid",
        "right_bottom",
        "right",
        "top_left",
        "top_mid",
        "top_right",
        "top",
        "bottom_left",
        "bottom_mid",
        "bottom_right",
        "bottom",
    )
    val GESTURES = listOf("click", "double_click", "long_press", "swipe_up", "swipe_down", "swipe_left", "swipe_right")
    val KEY_TRIGGERS = listOf("click", "double_click", "long_press")

    const val SUB_GESTURE_ACTION = "sub_gesture"
    val SUB_GESTURE_DIRECTIONS = listOf("hold", "swipe_left", "swipe_right", "swipe_up", "swipe_down")

    fun subGestureChildKey(parentKey: String, direction: String) = "${parentKey}_sub_${direction}"

    const val PIE_ACTION = "pie"
    const val PARTIAL_SCREENSHOT_ACTION = "partial_screenshot"
    const val PIE_RINGS = 2
    const val PIE_SLOTS_PER_RING = 6
    const val PIE_SIZE_SCALE = "pie_size_scale"
    const val PIE_COLOR = "pie_color"
    const val PIE_SIZE_SCALE_DEFAULT = 1.0f
    const val PIE_ENABLED = "pie_enabled"
    val PIE_EDGES = listOf("left", "right", "top", "bottom")

    fun pieSlot(edge: String, ring: Int, slot: Int) = "pie_${edge}_ring${ring}_slot${slot}"
    fun pieSlotLabel(edge: String, ring: Int, slot: Int) = "pie_${edge}_ring${ring}_slot${slot}_label"

    fun zoneEnabled(zone: String) = "zone_enabled_$zone"
    fun gestureAction(zone: String, gesture: String) = "${zone}_${gesture}"
    fun gestureActionLabel(zone: String, gesture: String) = "${zone}_${gesture}_label"
    fun keyEnabled(keyCode: Int) = "key_enabled_$keyCode"
    fun keyAction(keyCode: Int, trigger: String) = "key_${keyCode}_$trigger"
    fun keyActionLabel(keyCode: Int, trigger: String) = "key_${keyCode}_${trigger}_label"
    fun customPanelSlot(row: Int, column: Int) = "custom_panel_${row}_${column}"
    fun customPanelSlotTitle(row: Int, column: Int) = "custom_panel_${row}_${column}_title"
    fun sideBarSlot(side: String, index: Int) = "side_bar_${side}_$index"
    fun sideBarSlotTitle(side: String, index: Int) = "side_bar_${side}_${index}_title"

    const val DEFAULT_SPLIT_FIRST_PERCENT = 33
    const val DEFAULT_SPLIT_SECOND_PERCENT = 66
    const val MIN_SEGMENT_PERCENT = 10
    const val DEFAULT_THICKNESS_DP = 16
    const val MIN_THICKNESS_DP = 8
    const val MAX_THICKNESS_DP = 32

    fun zoneSplitFirstPercentKey(edge: String) = "zone_split_${edge}_first_percent"
    fun zoneSplitSecondPercentKey(edge: String) = "zone_split_${edge}_second_percent"
    fun zoneThicknessKey(zone: String) = "zone_thickness_${zone}_dp"

    fun fallbackEdgeZone(zone: String): String? =
        when (zone) {
            "left_top", "left_mid", "left_bottom" -> "left"
            "right_top", "right_mid", "right_bottom" -> "right"
            "top_left", "top_mid", "top_right" -> "top"
            "bottom_left", "bottom_mid", "bottom_right" -> "bottom"
            else -> null
        }

    fun isActiveActionValue(value: String): Boolean =
        value.isNotBlank() && value != "none"

    fun gestureActionParts(key: String): Pair<String, String>? {
        val gesture = GESTURES.sortedByDescending(String::length)
            .firstOrNull { key.endsWith("_$it") }
            ?: return null
        val zone = key.removeSuffix("_$gesture")
        return if (zone in ZONES) zone to gesture else null
    }

    fun keyActionParts(key: String): Pair<Int, String>? {
        if (!key.startsWith("key_")) return null
        val trigger = KEY_TRIGGERS.sortedByDescending(String::length)
            .firstOrNull { key.endsWith("_$it") }
            ?: return null
        val keyCode = key
            .removePrefix("key_")
            .removeSuffix("_$trigger")
            .toIntOrNull()
            ?: return null
        return keyCode to trigger
    }
}
