package git.prayoadmii.darkbar.client.helper;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;

import git.prayoadmii.darkbar.client.helper.DwmApi;

public interface DwmApi extends Library {
    DwmApi INSTANCE = Native.load("dwmapi", DwmApi.class);

    int DwmSetWindowAttribute(HWND hwnd, int attr, int[] value, int size);
}