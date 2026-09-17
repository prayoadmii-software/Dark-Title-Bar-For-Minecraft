package git.prayoadmii.darkbar.client.controller;

import net.minecraft.client.Minecraft;

import org.lwjgl.glfw.GLFWNativeWin32;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;

import git.prayoadmii.darkbar.client.helper.DwmApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TitleBarController {
    private static final Logger LOGGER = LoggerFactory.getLogger("DarkBar");

    public static void setDark(boolean enabled) {
        String os = System.getProperty("os.name", "").toLowerCase();

        if (!os.contains("win")) {
            LOGGER.warn("DarkBar: Theme Apply Was Skipped! You Have To Run The Mod On Windows Machine!");

            return;
        }

        try {
            long glfwWindow = Minecraft.getInstance().getWindow().handle();
            long hwndLong = GLFWNativeWin32.glfwGetWin32Window(glfwWindow);

            HWND hwnd = new HWND(new Pointer(hwndLong));

            int DWMWA_USE_IMMERSIVE_DARK_MODE = 20;

            int[] value = { enabled ? 1 : 0 };

            DwmApi.INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_USE_IMMERSIVE_DARK_MODE, value, 4);

            LOGGER.info("Title Bar Theme Was Set To {}", enabled ? "Dark" : "Light");
        } catch (Throwable t) {
            LOGGER.error("Failed To Apply Title Bar Theme As:", t);
        }
    }
}