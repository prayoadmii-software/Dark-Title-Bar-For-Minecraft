package git.prayoadmii.darkbar.controller;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import git.prayoadmii.darkbar.DarkBarMain;
import net.minecraft.network.chat.Component;

public class DarkBarCommands {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                LiteralArgumentBuilder.<FabricClientCommandSource>literal("darkbar")
                
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("dark")
                    .executes(ctx -> {
                        DarkBarMain.setDark(true);

                        return 1;
                    })
                )

                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("light")
                    .executes(ctx -> {
                        DarkBarMain.setDark(false);

                        return 1;
                    })
                )

                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("config")
                    .executes(ctx -> {
                        String message = FabricLoader.getInstance().isModLoaded("oneconfigv1")
                            ? "Open OneConfig To Configure The Dark Title Bar"
                            : "Please Install OneConfig In Order For This Command To Work";
                        
                        ctx.getSource().sendFeedback(Component.literal(
                            message
                        ));
                        
                        return 1;
                    })
                )
            );
        });
    }
}