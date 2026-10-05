package git.prayoadmii.darkbar.controller;

import java.util.Locale;

import org.lwjgl.glfw.GLFWNativeWin32;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;

import git.prayoadmii.darkbar.helper.DwmApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TitleBarController {
    private static final Logger LOGGER = LoggerFactory.getLogger("DarkBar");
    private static final int DWMWA_USE_IMMERSIVE_DARK_MODE = 20;

    public static boolean setDark(boolean enabled) {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);

        if (!os.contains("win")) {
            LOGGER.warn("DarkBar: Theme Apply Was Skipped! You Have To Run The Mod On Windows Machine!");

            return false;
        }

        try {
            long hwnd = getWindowHandle();

            if (hwnd == 0L) {
                LOGGER.warn("DarkBar: No Visible Windows Window Owned By This Process Was Found!");

                return false;
            }

            HWND nativeWindow = new HWND(new Pointer(hwnd));

            int[] value = { enabled ? 1 : 0 };

            int result = DwmApi.INSTANCE.DwmSetWindowAttribute(nativeWindow, DWMWA_USE_IMMERSIVE_DARK_MODE, value, 4);

            if (result < 0) {
                LOGGER.error("Failed To Apply Title Bar Theme! DwmSetWindowAttribute Returned HRESULT 0x{}", Integer.toHexString(result).toUpperCase(Locale.ROOT));

                return false;
            }

            LOGGER.info("Title Bar Theme Was Set To {}", enabled ? "Dark" : "Light");

            return true;
        } catch (Throwable t) {
            LOGGER.error("Failed To Apply Title Bar Theme As:", t);

            return false;
        }
    }

    private static long getWindowHandle() {
        try {
            Object minecraft = net.minecraft.client.Minecraft.getInstance();

            if (minecraft != null && minecraft.getClass().getName().startsWith("net.minecraft")) {
                Object window = minecraft.getClass().getMethod("getWindow").invoke(minecraft);

                if (window != null) {
                    Object handle = window.getClass().getMethod("handle").invoke(window);

                    if (handle instanceof Number number) {
                        long glfwWindow = number.longValue();
                        long hwnd = GLFWNativeWin32.glfwGetWin32Window(glfwWindow);

                        if (hwnd != 0L) {
                            return hwnd;
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
            // A dedicated server has no Minecraft client window; resolve its GUI through User32.
        }

        return getCurrentProcessWindowHandle();
    }

    private static long getCurrentProcessWindowHandle() {
        User32 user32 = User32.INSTANCE;
        int processId = Kernel32.INSTANCE.GetCurrentProcessId();
        long[] firstWindow = { 0L };
        long[] minecraftWindow = { 0L };

        user32.EnumWindows((WNDENUMPROC) (window, data) -> {
            if (!user32.IsWindowVisible(window)) {
                return true;
            }

            IntByReference windowProcessId = new IntByReference();

            user32.GetWindowThreadProcessId(window, windowProcessId);

            if (windowProcessId.getValue() != processId) {
                return true;
            }

            int titleLength = user32.GetWindowTextLength(window);

            if (titleLength == 0) {
                return true;
            }

            char[] titleBuffer = new char[titleLength + 1];
            user32.GetWindowText(window, titleBuffer, titleBuffer.length);
            String title = new String(titleBuffer).trim();

            if (title.isEmpty()) {
                return true;
            }

            if (firstWindow[0] == 0L) {
                firstWindow[0] = Pointer.nativeValue(window.getPointer());
            }

            if (title.toLowerCase(Locale.ROOT).contains("minecraft")) {
                minecraftWindow[0] = Pointer.nativeValue(window.getPointer());

                return false;
            }

            return true;
        }, null);

        long handle = minecraftWindow[0] != 0L ? minecraftWindow[0] : firstWindow[0];

        if (handle != 0L) {
            LOGGER.info("Found Server GUI Window Handle 0x{}", Long.toHexString(handle).toUpperCase(Locale.ROOT));
        }
        
        return handle;
    }
}