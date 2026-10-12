package git.prayoadmii.darkbar;

public final class DarkBarOneConfigIntegration {
    private static DarkBarOneConfig config;

    private DarkBarOneConfigIntegration() {
    }

    public static void initialize() {
        config = new DarkBarOneConfig();
    }

    public static void setEnabled(boolean enabled) {
        if (config == null) {
            return;
        }

        config.darkBarEnabled = enabled;
        
        config.save();
    }
}