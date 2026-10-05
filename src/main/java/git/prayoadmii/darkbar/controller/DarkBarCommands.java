package git.prayoadmii.darkbar.controller;

import net.minecraft.client.Minecraft;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import git.prayoadmii.darkbar.DarkBarMain;

public class DarkBarCommands {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                LiteralArgumentBuilder.<FabricClientCommandSource>literal("darkbar")
                
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("dark")
                    .executes(ctx -> {
                        TitleBarController.setDark(true);

                        DarkBarMain.config.darkBarEnabled = true;
                        DarkBarMain.config.save();

                        return 1;
                    })
                )

                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("light")
                    .executes(ctx -> {
                        TitleBarController.setDark(false);

                        DarkBarMain.config.darkBarEnabled = false;
                        DarkBarMain.config.save();

                        return 1;
                    })
                )

                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("config")
                    .executes(ctx -> {
                        Minecraft.getInstance().execute(() ->
                            Minecraft.getInstance().gui.setScreen(
                                DarkBarConfigScreen.create(null)
                            )
                        );
                        
                        return 1;
                    })
                )
            );
        });
    }
}