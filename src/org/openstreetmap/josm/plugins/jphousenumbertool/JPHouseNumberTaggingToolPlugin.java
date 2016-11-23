package org.openstreetmap.josm.plugins.jphousenumbertool;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

/**
 * Simple tool to tag house numbers. Select house and press 'b'. Select your addr-tags and press OK.
 */
public class JPHouseNumberTaggingToolPlugin extends Plugin {
    private final LaunchAction action;

    /**
     * constructor
     * @param info plugin info
     */
    public JPHouseNumberTaggingToolPlugin(PluginInformation info) {
        super(info);
        action = new LaunchAction(getPluginDir());
        int lastItemIndex = Main.main.menu.dataMenu.getItemCount();
        MainMenu.add(Main.main.menu.dataMenu, action, false, lastItemIndex);
    }
}
