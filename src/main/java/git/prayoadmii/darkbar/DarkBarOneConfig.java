package git.prayoadmii.darkbar;

import git.prayoadmii.darkbar.controller.TitleBarController;
import org.polyfrost.oneconfig.api.config.v1.Tree;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;

public final class DarkBarOneConfig extends org.polyfrost.oneconfig.api.config.v1.Config {
    @Switch(
        title = "Dark Title Bar",
        description = "Toggles The Dark Title Bar On The Minecraft: Java Edition Game Window",
        category = "Visuals"
    )
    public boolean darkBarEnabled = DarkBarMain.config.darkBarEnabled;

    public DarkBarOneConfig() {
        super(
            "darkbar",
            "/assets/mod-icon.png",
            "Dark Title Bar For Minecraft",
            org.polyfrost.oneconfig.api.config.v1.Config.Category.VISUALS
        );

        addCallback("darkBarEnabled", this::saveAndApply);
    }

    @Override
    protected void onInitialized(Tree tree) {
        saveAndApply();
    }

    private void saveAndApply() {
        DarkBarMain.config.darkBarEnabled = darkBarEnabled;
        DarkBarMain.config.save();

        save();

        TitleBarController.setDark(darkBarEnabled);
    }
}