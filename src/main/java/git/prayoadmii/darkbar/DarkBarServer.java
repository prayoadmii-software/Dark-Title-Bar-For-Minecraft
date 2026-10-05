package git.prayoadmii.darkbar;

import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.regex.Pattern;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import git.prayoadmii.darkbar.controller.TitleBarController;
import git.prayoadmii.darkbar.helper.Config;

public class DarkBarServer implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("DarkBar");
    private static final Pattern NO_GUI_ARGUMENT = Pattern.compile("^(--)?nogui$", Pattern.CASE_INSENSITIVE);
    private static Config config;

    @Override
    public void onInitializeServer() {
        config = Config.load();
        registerConsoleCommands();

        String os = System.getProperty("os.name", "").toLowerCase();
        if (!os.contains("win")) {
            LOGGER.warn("DarkBar: Server GUI Theme Was Skipped! Make Sure You're On Windows!");

            return;
        }

        if (hasNoGuiArgument()) {
            LOGGER.warn("DarkBar: Mod Will Be Disabled - `nogui` Was Passed In The Launch Command.");

            return;
        }

        if (GraphicsEnvironment.isHeadless()) {
            LOGGER.warn("DarkBar: Mod Will Be Disabled - The Server Cannot Start With A GUI In This Environment.");
            
            return;
        }

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            if (!TitleBarController.setDark(config.darkBarEnabled)) {
                LOGGER.warn("DarkBar: Mod Will Be Disabled - The Server Could Not Start With A GUI Window.");
            }
        });

        LOGGER.info("Server Initialized Successfully!");
    }

    private static void registerConsoleCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) ->
            dispatcher.register(
                Commands.literal("darkbar")
                    .requires(source -> source.getEntity() == null && source.getTextName().equals("Server"))
                    .then(Commands.literal("dark")
                        .executes(context -> setDark(context.getSource(), true)))
                    .then(Commands.literal("light")
                        .executes(context -> setDark(context.getSource(), false)))
            )
        );
    }

    private static int setDark(net.minecraft.commands.CommandSourceStack source, boolean enabled) {
        config.darkBarEnabled = enabled;
        config.save();
        boolean applied = TitleBarController.setDark(enabled);
        String message = applied
            ? "DarkBar set to " + (enabled ? "dark." : "light.")
            : "DarkBar setting saved, but the title bar could not be updated. Check the server log.";
        source.sendSuccess(() -> Component.literal(message), false);
        return applied ? 1 : 0;
    }

    private static boolean hasNoGuiArgument() {
        if (ProcessHandle.current().info().arguments().map(arguments -> Arrays.stream(arguments).anyMatch(argument -> NO_GUI_ARGUMENT.matcher(argument).matches())).orElse(false)) {

            return true;
        }

        String javaCommand = System.getProperty("sun.java.command", "");

        return Arrays.stream(javaCommand.split("\\s+")).anyMatch(argument -> NO_GUI_ARGUMENT.matcher(argument).matches());
    }
}