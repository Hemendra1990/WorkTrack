package com.hemendra.util;

import java.util.HashMap;
import java.util.Map;

public class AppCategoryIdentifier {
    private static final Map<String, String> appCategoryMap = new HashMap<>();

    static {
        // macOS Apps
        appCategoryMap.put("Finder", AppCategoryConstants.FILE_MANAGER);
        appCategoryMap.put("Mail", AppCategoryConstants.EMAIL);
        appCategoryMap.put("Camunda Modeler", AppCategoryConstants.DEVELOPMENT_TOOLS);
        appCategoryMap.put("Calculator", AppCategoryConstants.UTILITIES);
        appCategoryMap.put("Docker Desktop", AppCategoryConstants.DEVELOPMENT_TOOLS);
        appCategoryMap.put("Messages", AppCategoryConstants.MESSAGING);
        appCategoryMap.put("Safari", AppCategoryConstants.BROWSER);
        appCategoryMap.put("TextMate", AppCategoryConstants.TEXT_EDITOR);
        appCategoryMap.put("Photos", AppCategoryConstants.MEDIA);
        appCategoryMap.put("Postman", AppCategoryConstants.DEVELOPMENT_TOOLS);
        appCategoryMap.put("Microsoft Teams", AppCategoryConstants.COMMUNICATION);
        appCategoryMap.put("Outlook", AppCategoryConstants.EMAIL);
        appCategoryMap.put("zoom.us", AppCategoryConstants.COMMUNICATION);
        appCategoryMap.put("Word", AppCategoryConstants.OFFICE_SUITE);
        appCategoryMap.put("Warp", AppCategoryConstants.TERMINAL);
        appCategoryMap.put("Chess", AppCategoryConstants.GAMES);
        appCategoryMap.put("Photo Booth", AppCategoryConstants.MEDIA);
        appCategoryMap.put("Chrome", AppCategoryConstants.BROWSER);
        appCategoryMap.put("Notion", AppCategoryConstants.PRODUCTIVITY);
        appCategoryMap.put("Image Capture", AppCategoryConstants.MEDIA);
        appCategoryMap.put("PowerPoint", AppCategoryConstants.OFFICE_SUITE);
        appCategoryMap.put("Maps", AppCategoryConstants.UTILITIES);
        appCategoryMap.put("Edge", AppCategoryConstants.BROWSER);
        appCategoryMap.put("FaceTime", AppCategoryConstants.COMMUNICATION);
        appCategoryMap.put("Script Editor", AppCategoryConstants.DEVELOPMENT_TOOLS);
        appCategoryMap.put("Code", AppCategoryConstants.DEVELOPMENT_TOOLS);
        appCategoryMap.put("IntelliJ IDEA", AppCategoryConstants.DEVELOPMENT_TOOLS);
        appCategoryMap.put("Firefox", AppCategoryConstants.BROWSER);
        appCategoryMap.put("Notes", AppCategoryConstants.PRODUCTIVITY);
        appCategoryMap.put("Microsoft Remote Desktop", AppCategoryConstants.REMOTE_ACCESS);
        appCategoryMap.put("Excel", AppCategoryConstants.OFFICE_SUITE);
        appCategoryMap.put("DBeaver Community", AppCategoryConstants.DEVELOPMENT_TOOLS);
        appCategoryMap.put("Calendar", AppCategoryConstants.PRODUCTIVITY);

        // Windows Apps
        appCategoryMap.put("Explorer", AppCategoryConstants.FILE_MANAGER);
        appCategoryMap.put("VSCode", AppCategoryConstants.DEVELOPMENT_TOOLS);
        appCategoryMap.put("Notepad++", AppCategoryConstants.TEXT_EDITOR);
        appCategoryMap.put("Photos", AppCategoryConstants.MEDIA);
        appCategoryMap.put("Zoom", AppCategoryConstants.COMMUNICATION);
        appCategoryMap.put("Remote Desktop Connection", AppCategoryConstants.REMOTE_ACCESS);

        // Linux Apps
        appCategoryMap.put("Nautilus", AppCategoryConstants.FILE_MANAGER);
        appCategoryMap.put("Thunderbird", AppCategoryConstants.EMAIL);
        appCategoryMap.put("Gnome Calculator", AppCategoryConstants.UTILITIES);
        appCategoryMap.put("Slack", AppCategoryConstants.COMMUNICATION);
        appCategoryMap.put("Gedit", AppCategoryConstants.TEXT_EDITOR);
        appCategoryMap.put("Shotwell", AppCategoryConstants.MEDIA);
        appCategoryMap.put("LibreOffice Writer", AppCategoryConstants.OFFICE_SUITE);
        appCategoryMap.put("Chromium", AppCategoryConstants.BROWSER);
        appCategoryMap.put("LibreOffice Impress", AppCategoryConstants.OFFICE_SUITE);
        appCategoryMap.put("LibreOffice Calc", AppCategoryConstants.OFFICE_SUITE);
        appCategoryMap.put("Remmina", AppCategoryConstants.REMOTE_ACCESS);
    }

    public static String getAppCategory(String currentActiveWindow) {
        if (currentActiveWindow == null || currentActiveWindow.isEmpty()) {
            return AppCategoryConstants.UNKNOWN;
        }

        return appCategoryMap.getOrDefault(currentActiveWindow, AppCategoryConstants.UNKNOWN);
    }
}
           
