package git.prayoadmii.darkbar;

import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.regex.Pattern;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import git.prayoadmii.darkbar.controller.TitleBarController;
import git.prayoadmii.darkbar.helper.Config;

public class DarkBarServer implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("DarkBar");
    private static final Pattern NO_GUI_ARGUMENT = Pattern.compile("^(--)?nogui$", Pattern.CASE_INSENSITIVE);

    @Override
    public void onInitializeServer() {
        DarkBarMain.config = Config.load();

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
            if (!TitleBarController.setDark(DarkBarMain.config.darkBarEnabled)) {
                LOGGER.warn("DarkBar: Mod Will Be Disabled - The Server Could Not Start With A GUI Window.");
            }
        });

        LOGGER.info("Server Initialized Successfully!");
    }

    private static boolean hasNoGuiArgument() {
        if (ProcessHandle.current().info().arguments().map(arguments -> Arrays.stream(arguments).anyMatch(argument -> NO_GUI_ARGUMENT.matcher(argument).matches())).orElse(false)) {

            return true;
        }

        String javaCommand = System.getProperty("sun.java.command", "");

        return Arrays.stream(javaCommand.split("\\s+")).anyMatch(argument -> NO_GUI_ARGUMENT.matcher(argument).matches());
    }
}