package git.prayoadmii.darkbar.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import git.prayoadmii.darkbar.client.controller.DarkBarConfigScreen;

public class DarkBarModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return DarkBarConfigScreen::create;
    }
}