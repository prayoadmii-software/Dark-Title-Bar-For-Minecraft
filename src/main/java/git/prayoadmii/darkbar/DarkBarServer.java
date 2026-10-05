package git.prayoadmii.darkbar;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import git.prayoadmii.darkbar.controller.TitleBarController;
import git.prayoadmii.darkbar.helper.Config;

public class DarkBarServer implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("DarkBar");

    @Override
    public void onInitializeServer() {
        DarkBarMain.config = Config.load();

        String os = System.getProperty("os.name", "").toLowerCase();
        if (!os.contains("win")) {
            LOGGER.warn("DarkBar: Server GUI Theme Was Skipped! Make Sure You're On Windows!");
            return;
        }

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            TitleBarController.setDark(DarkBarMain.config.darkBarEnabled);
        });

        LOGGER.info("Server Initialized Successfully!");
    }
}
