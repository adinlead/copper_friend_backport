package org.xiyu.yee.copper_friend_backport;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Configuration class for Copper Golem AI behavior.
 * Controls various parameters for how the Copper Golem operates.
 */
public class CopperGolemConfig {
    
    public static final ForgeConfigSpec COMMON_SPEC;
    
    // AI Behavior Settings
    public static final ForgeConfigSpec.DoubleValue PANIC_SPEED_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue IDLE_SPEED_MULTIPLIER;
    
    // Transport Item Settings
    public static final ForgeConfigSpec.IntValue TRANSPORT_HORIZONTAL_SEARCH_RADIUS;
    public static final ForgeConfigSpec.IntValue TRANSPORT_VERTICAL_SEARCH_RADIUS;
    public static final ForgeConfigSpec.IntValue TICK_TO_START_INTERACTION;
    public static final ForgeConfigSpec.IntValue TICK_TO_PLAY_SOUND;
    
    // Random Stroll Settings
    public static final ForgeConfigSpec.DoubleValue RANDOM_STROLL_SPEED;
    public static final ForgeConfigSpec.IntValue RANDOM_STROLL_MIN_DISTANCE;
    public static final ForgeConfigSpec.IntValue RANDOM_STROLL_MAX_DISTANCE;
    
    // DoNothing Behavior Settings
    public static final ForgeConfigSpec.IntValue DO_NOTHING_MIN_DURATION;
    public static final ForgeConfigSpec.IntValue DO_NOTHING_MAX_DURATION;
    
    // Look Settings
    public static final ForgeConfigSpec.IntValue LOOK_AT_PLAYER_MIN_LOOK_DURATION;
    public static final ForgeConfigSpec.IntValue LOOK_AT_PLAYER_MAX_LOOK_DURATION;
    public static final ForgeConfigSpec.DoubleValue LOOK_AT_PLAYER_PROBABILITY;
    
    // Chest Interaction Settings
    public static final ForgeConfigSpec.IntValue CHEST_INTERACTION_DURATION;

    // Container Filter Settings - 容器过滤配置（取出/存入）
    // 需求点1：可在配置文件中自定义铜傀儡取出的容器
    // 需求点2：可在配置文件中自定义铜傀儡存入的容器
    // 需求点4：配置项以容器ID（ResourceLocation 字符串）作为标志
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> EXTRACT_CONTAINER_IDS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DEPOSIT_CONTAINER_IDS;
    
    // Weathering/Oxidation Settings
    public static final ForgeConfigSpec.IntValue WEATHERING_TICK_MIN;
    public static final ForgeConfigSpec.IntValue WEATHERING_TICK_MAX;
    public static final ForgeConfigSpec.DoubleValue TURN_TO_STATUE_CHANCE;
    
    // Animation Settings
    public static final ForgeConfigSpec.IntValue SPIN_ANIMATION_MIN_COOLDOWN;
    public static final ForgeConfigSpec.IntValue SPIN_ANIMATION_MAX_COOLDOWN;
    
    // Spawn Settings
    public static final ForgeConfigSpec.IntValue SPAWN_COOLDOWN_MIN;
    public static final ForgeConfigSpec.IntValue SPAWN_COOLDOWN_MAX;
    
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        
        builder.comment("Copper Golem AI Configuration||铜傀儡AI配置")
               .push("ai_behavior");
        
        // AI Behavior
        builder.comment("Speed and movement settings||速度和移动设置")
               .push("speed");
        
        PANIC_SPEED_MULTIPLIER = builder
            .comment("Speed multiplier when the Copper Golem is panicking (default: 1.5)||当铜傀儡处于惊慌状态时的速度倍增器（默认值：1.5）")
            .defineInRange("panicSpeedMultiplier", 1.5, 0.1, 10.0);
        
        IDLE_SPEED_MULTIPLIER = builder
            .comment("Speed multiplier when the Copper Golem is idle/walking (default: 1.0)||当铜傀儡处于闲置/行走状态时的速度倍增器（默认值：1.0）")
            .defineInRange("idleSpeedMultiplier", 1.0, 0.1, 10.0);
        
        builder.pop();
        
        // Transport Item Settings
        builder.comment("Item transportation settings||物品运输设置")
               .push("transport");
        
        TRANSPORT_HORIZONTAL_SEARCH_RADIUS = builder
            .comment("Horizontal search radius (in blocks) for finding chests to transport items (default: 32)||寻找箱子以运输物品的水平搜索半径（以方块为单位）（默认值：32）")
            .defineInRange("horizontalSearchRadius", 32, 1, 128);
        
        TRANSPORT_VERTICAL_SEARCH_RADIUS = builder
            .comment("Vertical search radius (in blocks) for finding chests to transport items (default: 8)||寻找箱子以运输物品的垂直搜索半径（以方块为单位）（默认值：8）")
            .defineInRange("verticalSearchRadius", 8, 1, 64);
        
        TICK_TO_START_INTERACTION = builder
            .comment("Number of ticks before starting chest interaction animation (default: 1)||在开始与箱子交互动画之前的刻数（默认值：1）")
            .defineInRange("tickToStartInteraction", 1, 0, 100);
        
        TICK_TO_PLAY_SOUND = builder
            .comment("Number of ticks before playing interaction sound (default: 9)||在播放交互声音之前的刻数（默认值：9）")
            .defineInRange("tickToPlaySound", 9, 0, 100);
        
        CHEST_INTERACTION_DURATION = builder
            .comment("Total duration (in ticks) of chest interaction before closing (default: 60, 3 seconds)||与箱子交互的总持续时间（以刻为单位），然后关闭（默认值：60，3秒）")
            .defineInRange("chestInteractionDuration", 60, 1, 200);

        builder.pop();

        // Container Filter Settings - 容器过滤配置
        // 需求点1&2&4：自定义铜傀儡取出/存入的容器，以容器ID作为标志
        builder.comment("Container filter settings. Use block IDs like 'minecraft:chest'.||容器过滤设置。使用方块ID，如 'minecraft:chest'。")
               .push("containers");

        // 取出容器配置（铜傀儡从中取出物品的容器ID列表）
        EXTRACT_CONTAINER_IDS = builder
            .comment(
                "List of container block IDs the Copper Golem will EXTRACT items from.||铜傀儡将从中【取出】物品的容器方块ID列表。",
                "Must NOT overlap with depositContainerIds or the mod will fail to load.||不得与 depositContainerIds 存在交集，否则MOD加载失败。",
                "Default: all copper chest variants.||默认值：所有铜箱子变体。"
            )
            .defineList(
                "extractContainerIds",
                Arrays.asList(
                    "copper_friend_backport:copper_chest",
                    "copper_friend_backport:exposed_copper_chest",
                    "copper_friend_backport:weathered_copper_chest",
                    "copper_friend_backport:oxidized_copper_chest",
                    "copper_friend_backport:waxed_copper_chest",
                    "copper_friend_backport:waxed_exposed_copper_chest",
                    "copper_friend_backport:waxed_weathered_copper_chest",
                    "copper_friend_backport:waxed_oxidized_copper_chest"
                ),
                obj -> obj instanceof String
            );

        // 存入容器配置（铜傀儡向其中存入物品的容器ID列表）
        DEPOSIT_CONTAINER_IDS = builder
            .comment(
                "List of container block IDs the Copper Golem will DEPOSIT items into.||铜傀儡将向其中【存入】物品的容器方块ID列表。",
                "Must NOT overlap with extractContainerIds or the mod will fail to load.||不得与 extractContainerIds 存在交集，否则MOD加载失败。",
                "Default: vanilla chest and trapped chest.||默认值：原版箱子和陷阱箱子。"
            )
            .defineList(
                "depositContainerIds",
                Arrays.asList(
                    "minecraft:chest",
                    "minecraft:trapped_chest"
                ),
                obj -> obj instanceof String
            );

        builder.pop();
        
        // Random Stroll Settings
        builder.comment("Random wandering behavior settings||随机漫步行为设置")
               .push("stroll");
        
        RANDOM_STROLL_SPEED = builder
            .comment("Speed multiplier for random strolling (default: 1.0)||随机漫步的速度倍增器（默认值：1.0）")
            .defineInRange("strollSpeed", 1.0, 0.1, 10.0);
        
        RANDOM_STROLL_MIN_DISTANCE = builder
            .comment("Minimum distance (in blocks) for random stroll targets (default: 2)||随机漫步目标的最小距离（以方块为单位）（默认值：2）")
            .defineInRange("minStrollDistance", 2, 1, 32);
        
        RANDOM_STROLL_MAX_DISTANCE = builder
            .comment("Maximum distance (in blocks) for random stroll targets (default: 2)||随机漫步目标的最大距离（以方块为单位）（默认值：2）")
            .defineInRange("maxStrollDistance", 2, 1, 32);
        
        builder.pop();
        
        // Idle Behavior
        builder.comment("Idle behavior settings||行为设置")
               .push("idle");
        
        DO_NOTHING_MIN_DURATION = builder
            .comment("Minimum duration (in ticks) for standing still (default: 30, 1.5 seconds)||最短静止时间（以刻为单位）（默认值：30，1.5秒）")
            .defineInRange("doNothingMinDuration", 30, 1, 1200);
        
        DO_NOTHING_MAX_DURATION = builder
            .comment("Maximum duration (in ticks) for standing still (default: 60, 3 seconds)||最长静止时间（以刻为单位）（默认值：60，3秒）")
            .defineInRange("doNothingMaxDuration", 60, 1, 1200);
        
        builder.pop();
        
        // Look Settings
        builder.comment("Looking at player settings||看向玩家的设置")
               .push("look");
        
        LOOK_AT_PLAYER_PROBABILITY = builder
            .comment("Maximum distance (in blocks) at which golem will look at players (default: 6.0)||最大距离（以方块为单位），在此范围内傀儡会注视玩家（默认值：6.0）")
            .defineInRange("lookAtPlayerDistance", 6.0, 1.0, 32.0);
        
        LOOK_AT_PLAYER_MIN_LOOK_DURATION = builder
            .comment("Minimum duration (in ticks) for looking at player (default: 40, 2 seconds)||最短注视玩家时间（以刻为单位）（默认值：40，2秒）")
            .defineInRange("lookAtPlayerMinDuration", 40, 1, 1200);
        
        LOOK_AT_PLAYER_MAX_LOOK_DURATION = builder
            .comment("Maximum duration (in ticks) for looking at player (default: 80, 4 seconds)||最长注视玩家时间（以刻为单位）（默认值：80，4秒）")
            .defineInRange("lookAtPlayerMaxDuration", 80, 1, 1200);
        
        builder.pop();
        builder.pop();
        
        // Weathering/Oxidation Settings
        builder.comment("Weathering and oxidation settings||风化和氧化设置")
               .push("weathering");
        
        WEATHERING_TICK_MIN = builder
            .comment("Minimum ticks before oxidation occurs (default: 504000, ~7 hours)||氧化发生前的最少刻数（默认值：504000，约7小时）")
            .defineInRange("weatheringTickMin", 504000, 1, 2000000);
        
        WEATHERING_TICK_MAX = builder
            .comment("Maximum ticks before oxidation occurs (default: 552000, ~7.7 hours)||氧化发生前的最多刻数（默认值：552000，约7.7小时）")
            .defineInRange("weatheringTickMax", 552000, 1, 2000000);
        
        TURN_TO_STATUE_CHANCE = builder
            .comment("Chance per tick for oxidized golem to turn into statue (default: 0.0058, ~0.58%)||氧化傀儡每刻变成雕像的概率（默认值：0.0058，约0.58%）")
            .defineInRange("turnToStatueChance", 0.0058, 0.0, 1.0);
        
        builder.pop();
        
        // Animation Settings
        builder.comment("Animation timing settings||动画计时设置")
               .push("animation");
        
        SPIN_ANIMATION_MIN_COOLDOWN = builder
            .comment("Minimum cooldown ticks between head spin animations (default: 200, 10 seconds)||头部旋转动画之间的最少冷却刻数（默认值：200，10秒）")
            .defineInRange("spinAnimationMinCooldown", 200, 1, 1200);
        
        SPIN_ANIMATION_MAX_COOLDOWN = builder
            .comment("Maximum cooldown ticks between head spin animations (default: 240, 12 seconds)||头部旋转动画之间的最多冷却刻数（默认值：240，12秒）")
            .defineInRange("spinAnimationMaxCooldown", 240, 1, 1200);
        
        builder.pop();
        
        // Spawn Settings
        builder.comment("Spawn behavior settings||生成行为设置")
               .push("spawn");
        
        SPAWN_COOLDOWN_MIN = builder
            .comment("Minimum cooldown ticks for item transport after spawn (default: 60, 3 seconds)||生成后物品传输的最少冷却刻数（默认值：60，3秒）")
            .defineInRange("spawnCooldownMin", 60, 1, 1200);
        
        SPAWN_COOLDOWN_MAX = builder
            .comment("Maximum cooldown ticks for item transport after spawn (default: 100, 5 seconds)||生成后物品传输的最多冷却刻数（默认值：100，5秒）")
            .defineInRange("spawnCooldownMax", 100, 1, 1200);
        
        builder.pop();
        
        COMMON_SPEC = builder.build();
    }
    
    /**
     * Register the configuration file.
     * Should be called during mod initialization.
     */
    public static void register(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, COMMON_SPEC, "copper_friend_backport-common.toml");
    }
    
    // Convenience getters for use in code
    public static float getPanicSpeedMultiplier() {
        return PANIC_SPEED_MULTIPLIER.get().floatValue();
    }
    
    public static float getIdleSpeedMultiplier() {
        return IDLE_SPEED_MULTIPLIER.get().floatValue();
    }
    
    public static int getTransportHorizontalSearchRadius() {
        return TRANSPORT_HORIZONTAL_SEARCH_RADIUS.get();
    }
    
    public static int getTransportVerticalSearchRadius() {
        return TRANSPORT_VERTICAL_SEARCH_RADIUS.get();
    }
    
    public static int getTickToStartInteraction() {
        return TICK_TO_START_INTERACTION.get();
    }
    
    public static int getTickToPlaySound() {
        return TICK_TO_PLAY_SOUND.get();
    }
    
    public static int getChestInteractionDuration() {
        return CHEST_INTERACTION_DURATION.get();
    }
    
    public static float getRandomStrollSpeed() {
        return RANDOM_STROLL_SPEED.get().floatValue();
    }
    
    public static int getRandomStrollMinDistance() {
        return RANDOM_STROLL_MIN_DISTANCE.get();
    }
    
    public static int getRandomStrollMaxDistance() {
        return RANDOM_STROLL_MAX_DISTANCE.get();
    }
    
    public static int getDoNothingMinDuration() {
        return DO_NOTHING_MIN_DURATION.get();
    }
    
    public static int getDoNothingMaxDuration() {
        return DO_NOTHING_MAX_DURATION.get();
    }
    
    public static float getLookAtPlayerDistance() {
        return LOOK_AT_PLAYER_PROBABILITY.get().floatValue();
    }
    
    public static int getLookAtPlayerMinDuration() {
        return LOOK_AT_PLAYER_MIN_LOOK_DURATION.get();
    }
    
    public static int getLookAtPlayerMaxDuration() {
        return LOOK_AT_PLAYER_MAX_LOOK_DURATION.get();
    }
    
    // Weathering/Oxidation getters
    public static int getWeatheringTickMin() {
        return WEATHERING_TICK_MIN.get();
    }
    
    public static int getWeatheringTickMax() {
        return WEATHERING_TICK_MAX.get();
    }
    
    public static float getTurnToStatueChance() {
        return TURN_TO_STATUE_CHANCE.get().floatValue();
    }
    
    // Animation getters
    public static int getSpinAnimationMinCooldown() {
        return SPIN_ANIMATION_MIN_COOLDOWN.get();
    }
    
    public static int getSpinAnimationMaxCooldown() {
        return SPIN_ANIMATION_MAX_COOLDOWN.get();
    }
    
    // Spawn getters
    public static int getSpawnCooldownMin() {
        return SPAWN_COOLDOWN_MIN.get();
    }

    public static int getSpawnCooldownMax() {
        return SPAWN_COOLDOWN_MAX.get();
    }

    /**
     * 需求点3：校验取出/存入容器配置是否存在冲突。
     * 若 extractContainerIds 与 depositContainerIds 存在交集，抛出 {@link IllegalStateException}
     * 以阻止游戏启动。应在 MOD 加载阶段（如 FMLCommonSetupEvent）调用。
     */
    public static void validateContainerConfig() {
        List<? extends String> extract = EXTRACT_CONTAINER_IDS.get();
        List<? extends String> deposit = DEPOSIT_CONTAINER_IDS.get();

        // 以小写归一化避免大小写差异导致的漏判
        Set<String> extractSet = new HashSet<>();
        for (String id : extract) {
            extractSet.add(String.valueOf(id).toLowerCase());
        }

        Set<String> conflicts = new HashSet<>();
        for (String id : deposit) {
            String normalized = String.valueOf(id).toLowerCase();
            if (extractSet.contains(normalized)) {
                conflicts.add(normalized);
            }
        }

        if (!conflicts.isEmpty()) {
            // 抛出异常将导致 MOD 加载失败，游戏不会进入主界面
            throw new IllegalStateException(
                "[copper_friend_backport] Container config conflict detected! "
                + "The following container IDs appear in BOTH extractContainerIds and depositContainerIds: " + conflicts
                + ". A container cannot be both an extraction source and a deposit destination. "
                + "Please edit 'copper_friend_backport-common.toml' and restart."
            );
        }

        CopperFriendBackport.LOGGER.info(
            "[copper_friend_backport] Container config validated: extract={}, deposit={}",
            extract.size(), deposit.size()
        );
    }

    /**
     * 需求点1：根据 extractContainerIds 配置构建取出容器判定谓词。
     * 将配置中的容器ID解析为方块集合，运行期通过集合查找判定。
     * 无效ID会被记录警告并跳过，不会匹配到任何方块。
     */
    public static Predicate<BlockState> buildExtractPredicate() {
        return buildBlockPredicate(EXTRACT_CONTAINER_IDS.get(), "extractContainerIds");
    }

    /**
     * 需求点2：根据 depositContainerIds 配置构建存入容器判定谓词。
     * 将配置中的容器ID解析为方块集合，运行期通过集合查找判定。
     * 无效ID会被记录警告并跳过，不会匹配到任何方块。
     */
    public static Predicate<BlockState> buildDepositPredicate() {
        return buildBlockPredicate(DEPOSIT_CONTAINER_IDS.get(), "depositContainerIds");
    }

    /**
     * 将容器ID字符串列表解析为方块集合，返回基于集合查找的谓词。
     * 在大脑初始化（实体生成/加载）时调用一次，谓词被实体长期持有，
     * 因此修改配置后需重新生成实体才会生效。
     * //TODO: {配置运行时热更新后，已存在的铜傀儡仍使用旧谓词，需重启或重新加载实体}
     */
    private static Predicate<BlockState> buildBlockPredicate(List<? extends String> ids, String configKey) {
        Set<Block> blocks = new HashSet<>();
        for (String id : ids) {
            ResourceLocation rl = ResourceLocation.tryParse(id);
            // 跳过无法解析或注册表中不存在的ID，避免误匹配 air
            if (rl == null || !ForgeRegistries.BLOCKS.containsKey(rl)) {
                CopperFriendBackport.LOGGER.warn(
                    "[copper_friend_backport] Unknown block ID '{}' in config '{}', skipping.", id, configKey
                );
                continue;
            }
            Block block = ForgeRegistries.BLOCKS.getValue(rl);
            if (block != null && block != Blocks.AIR) {
                blocks.add(block);
            }
        }
        // 闭包持有方块集合快照，运行期 O(1) 查找
        return blockState -> blocks.contains(blockState.getBlock());
    }
}
