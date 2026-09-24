package org.xiyu.yee.copper_friend_backport.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import net.minecraft.resources.ResourceLocation;
import org.xiyu.yee.copper_friend_backport.CopperFriendBackport;

/**
 * JEI插件 - 为铜箱子添加JEI支持
 */
@JeiPlugin
public class CopperFriendJEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(CopperFriendBackport.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        // 可以在这里注册自定义配方类别,目前使用原版类别
    }

    // 修复BUG：铜箱子不应作为合成配方的催化剂(catalyst)注册。
    // 催化剂会显示在配方界面左侧，暗示该方块是执行该配方的"工作台"，
    // 但铜箱子是存储容器而非合成站，导致其在合成配方界面左侧错误出现。
    // 已移除 registerRecipeCatalysts 中对 RecipeTypes.CRAFTING 的注册。
}

