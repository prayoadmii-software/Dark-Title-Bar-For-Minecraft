package git.prayoadmii.darkbar.controller;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import git.prayoadmii.darkbar.DarkBarMain;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DarkBarConfigScreen {
    public static Screen create(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
            .title(Component.literal("DarkBar Config"))

            .category(ConfigCategory.createBuilder()
                .name(Component.literal("Darkbar General Config"))

                .option(Option.<Boolean>createBuilder()
                    .name(Component.literal("Dark Title Bar"))

                    .description(OptionDescription.of(Component.literal("Toggles The Dark Title Bar On The Minecraft: Java Edition Game Window")))

                    .binding(
                        true,
                        () -> DarkBarMain.config.darkBarEnabled,
                        val -> {
                            DarkBarMain.config.darkBarEnabled = val;
                            TitleBarController.setDark(val);
                        }
                    )
                    .controller(TickBoxControllerBuilder::create)

                    .build()
                )
                .build()
            )
            .save(() -> DarkBarMain.config.save())
            .build()
            .generateScreen(parent);
    }
}