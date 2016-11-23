package org.openstreetmap.josm.plugins.jphousenumbertool;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.SelectionChangedListener;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.tools.Shortcut;

public class LaunchAction extends JosmAction implements SelectionChangedListener {

    private OsmPrimitive selection = null;

    private String pluginDir;
   
    public LaunchAction(String pluginDir) {
        super("Launch JPHouseNumberTaggingTool", 
              "home-icon32", 
              "Launches the JPHouseNumberTaggingTool dialog", 
              Shortcut.registerShortcut("edit:jphousenumbertaggingtool", "JPHouseNumberTaggingTool", KeyEvent.VK_B, Shortcut.DIRECT),
              true);

        this.pluginDir = pluginDir;
        DataSet.addSelectionListener(this);
        setEnabled(false);
    }

    /**
     * launch the editor
     */
    protected void launchEditor() {
        if (!isEnabled()) {
            return;
        }
      
        TagDialog dialog = new TagDialog(pluginDir, selection);
        dialog.showDialog();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        launchEditor();
    }

    @Override
    public void selectionChanged(Collection<? extends OsmPrimitive> newSelection) {
        if (newSelection != null && newSelection.size() == 1) {
            setEnabled(true);
            selection  =  newSelection.iterator().next();
        } else {
            setEnabled(false);
            selection = null;
        }
    }
}
