package git.prayoadmii.darkbar.controller;

import java.awt.Window;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.lwjgl.glfw.GLFWNativeWin32;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;

import git.prayoadmii.darkbar.helper.DwmApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TitleBarController {
    private static final Logger LOGGER = LoggerFactory.getLogger("DarkBar");
    private static final int DWMWA_USE_IMMERSIVE_DARK_MODE = 20;

    public static void setDark(boolean enabled) {
        String os = System.getProperty("os.name", "").toLowerCase();

        if (!os.contains("win")) {
            LOGGER.warn("DarkBar: Theme Apply Was Skipped! You Have To Run The Mod On Windows Machine!");

            return;
        }

        try {
            long hwnd = getWindowHandle();

            if (hwnd == 0L) {
                LOGGER.warn("DarkBar: No Windows Window Was Found To Apply The Theme To.");

                return;
            }

            HWND nativeWindow = new HWND(new Pointer(hwnd));
            int[] value = { enabled ? 1 : 0 };

            DwmApi.INSTANCE.DwmSetWindowAttribute(nativeWindow, DWMWA_USE_IMMERSIVE_DARK_MODE, value, 4);

            LOGGER.info("Title Bar Theme Was Set To {}", enabled ? "Dark" : "Light");
        } catch (Throwable t) {
            LOGGER.error("Failed To Apply Title Bar Theme As:", t);
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
            // No client window available yet; fall through to AWT server GUI detection below.
        }

        for (Window window : Window.getWindows()) {
            long hwnd = getAwtWindowHandle(window);
            if (hwnd != 0L) {
                return hwnd;
            }
        }

        return 0L;
    }

    private static long getAwtWindowHandle(Window window) {
        if (window == null || !window.isShowing()) {
            return 0L;
        }

        try {
            Field peerField = Window.class.getDeclaredField("peer");
            peerField.setAccessible(true);
            Object peer = peerField.get(window);
            if (peer == null) {
                return 0L;
            }

            for (Method method : peer.getClass().getMethods()) {
                if (method.getName().equals("getHWnd") && method.getParameterCount() == 0) {
                    Object result = method.invoke(peer);
                    if (result instanceof Number number) {
                        return number.longValue();
                    }
                }
            }

            for (Field field : peer.getClass().getDeclaredFields()) {
                if (field.getType() == long.class || field.getType() == Long.class || field.getType().getSimpleName().equals("long")) {
                    field.setAccessible(true);
                    Object value = field.get(peer);
                    if (value instanceof Number number) {
                        return number.longValue();
                    }
                }
            }
        } catch (Throwable ignored) {
            // The server GUI may still be starting up or use a different AWT peer implementation.
        }

        return 0L;
    }
}