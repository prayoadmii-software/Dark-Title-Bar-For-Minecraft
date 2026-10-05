package git.prayoadmii.darkbar;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import git.prayoadmii.darkbar.controller.DarkBarConfigScreen;

public class DarkBarModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return DarkBarConfigScreen::create;
    }
}