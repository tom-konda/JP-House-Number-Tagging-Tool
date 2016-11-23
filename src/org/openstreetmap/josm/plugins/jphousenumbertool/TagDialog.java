package org.openstreetmap.josm.plugins.jphousenumbertool;

import static org.openstreetmap.josm.tools.I18n.tr;
import static org.openstreetmap.josm.tools.I18n.trn;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.tagging.ac.AutoCompletingComboBox;
import org.openstreetmap.josm.gui.tagging.ac.AutoCompletionListItem;
import org.openstreetmap.josm.gui.tagging.ac.AutoCompletionManager;

/**
 * @author Tom-Konda <https://github.com/tom-konda>
 * 
 * Below authors are authors for original HouseNumberTool plugin.
 * @author Oliver Raupach 09.01.2012 <http://www.oliver-raupach.de>
 * @author Victor Kropp 10.03.2012 <http://victor.kropp.name>
 */
public class TagDialog extends ExtendedDialog {
    private static final String APPLY_CHANGES = tr("Apply Changes");

    private static final String TAG_BUILDING = "building";
    private static final String TAG_ADDR_COUNTRY = "addr:country";
    private static final String TAG_ADDR_PROVINCE = "addr:province";
    private static final String TAG_ADDR_COUNTY = "addr:county";
    private static final String TAG_ADDR_CITY = "addr:city";
    private static final String TAG_ADDR_POSTCODE = "addr:postcode";
    private static final String TAG_ADDR_SUBURB = "addr:suburb";
    private static final String TAG_ADDR_QUARTER = "addr:quarter";
    private static final String TAG_ADDR_NEIGHBOURHOOD = "addr:neighbourhood";
    private static final String TAG_ADDR_BLOCK_NUMBER = "addr:block_number";
    private static final String TAG_ADDR_HOUSENUMBER = "addr:housenumber";

    private static final String[] BUILDING_STRINGS = {
        "yes", 
        "apartments", "farm", "hotel", "house", "detached", "residential", "dormitory", "terrace", "houseboat", "bungalow", "static_caravan",
        "commercial", "office", "industrial", "retail", "warehouse",
        "cathedral", "chapel", "church", "mosque", "temple", "synagogue", "shrine", "civic", "hospital", "school", "stadium", "train_station", "transportation", "university", "public",
        "barn", "bridge", "bunker", "cabin", "construction", "cowshed", "digester", "farm_auxiliary", "garage", "garages", "greenhouse", "hangar", "hut", "roof", "shed", "stable", "sty", "transformer_tower", "service", "kiosk", "ruins"
    };

    private static final int FPS_MIN = -2;
    private static final int FPS_MAX =  2;

    private static final Logger LOGGER = Logger.getLogger(TagDialog.class.getName());

    private String pluginDir;
    private AutoCompletionManager acm;
    private OsmPrimitive selection;

    private static final String TEMPLATE_DATA = "/template.data";

    private AutoCompletingComboBox country;
    private AutoCompletingComboBox province;
    private AutoCompletingComboBox county;
    private AutoCompletingComboBox city;
    private AutoCompletingComboBox suburb;
    private AutoCompletingComboBox quarter;
    private AutoCompletingComboBox neighbourhood;
    private AutoCompletingComboBox block_number;
    private AutoCompletingComboBox postcode;
    private JTextField housenumber;
    
    private JCheckBox buildingEnabled;
    private JCheckBox countryEnabled;
    private JCheckBox provinceEnabled;
    private JCheckBox countyEnabled;
    private JCheckBox cityEnabled;
    private JCheckBox postcodeEnabled;
    private JCheckBox suburbEnabled;
    private JCheckBox quarterEnabled;
    private JCheckBox neighbourhoodEnabled;
    private JCheckBox block_numberEnabled;
    private JCheckBox housenumberEnabled;
    private JSlider housenumberChangeSequence;
    private JComboBox<String> building;

    public TagDialog(String pluginDir, OsmPrimitive selection) {
        super(Main.parent, tr("House Number Editor"), new String[] { tr("OK"), tr("Cancel") }, true);
        this.pluginDir = pluginDir;
        this.selection = selection;

        JPanel editPanel = createContentPane();

        setContent(editPanel);
        setButtonIcons(new String[] { "ok.png", "cancel.png" });
        setDefaultButton(1);
        setupDialog();
        getRootPane().setDefaultButton(defaultButton);

        // middle of the screen
        setLocationRelativeTo(null);

        SwingUtilities.invokeLater(new Runnable()  {
            @Override
            public void run() {
                housenumber.requestFocus();
                housenumber.selectAll();
            }
        });
    }

    private JPanel createContentPane() {    	
        acm = selection.getDataSet().getAutoCompletionManager();

        Dto dto = loadDto();
        		
        JPanel editPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        buildingEnabled = new JCheckBox(TAG_BUILDING);
        buildingEnabled.setFocusable(false);
        buildingEnabled.setSelected(dto.isSaveBuilding());
        buildingEnabled.setToolTipText(APPLY_CHANGES);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.gridwidth = 3;
        editPanel.add(buildingEnabled, c);

        Arrays.sort(BUILDING_STRINGS);
        building = new JComboBox<>(BUILDING_STRINGS);
        building.setSelectedItem(dto.getBuilding());
        building.setMaximumRowCount(50);
        c.gridx = 3;
        c.gridy = 0;
        c.weightx = 1;
        c.gridwidth = 1;
        editPanel.add(building, c);

        // country
        countryEnabled = new JCheckBox(TAG_ADDR_COUNTRY);
        countryEnabled.setFocusable(false);
        countryEnabled.setSelected(dto.isSaveCountry());
        countryEnabled.setToolTipText(APPLY_CHANGES);
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.gridwidth = 3;
        editPanel.add(countryEnabled, c);

        country = new AutoCompletingComboBox();
        country.setPossibleACItems(acm.getValues(TAG_ADDR_COUNTRY));
        country.setPreferredSize(new Dimension(200, 24));
        country.setEditable(true);
        country.setSelectedItem(dto.getCountry());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 1;
        c.weightx = 1;
        c.gridwidth = 1;
        editPanel.add(country, c);

        // province
        provinceEnabled = new JCheckBox(TAG_ADDR_PROVINCE);
        provinceEnabled.setFocusable(false);
        provinceEnabled.setSelected(dto.isSaveProvince());
        provinceEnabled.setToolTipText(APPLY_CHANGES);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        c.gridwidth = 3;
        editPanel.add(provinceEnabled, c);

        province = new AutoCompletingComboBox();
        province.setPossibleACItems(acm.getValues(TAG_ADDR_PROVINCE));
        province.setPreferredSize(new Dimension(200, 24));
        province.setEditable(true);
        province.setSelectedItem(dto.getProvince());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 2;
        c.weightx = 1;
        c.gridwidth = 1;
        editPanel.add(province, c);

        // county
        countyEnabled = new JCheckBox(TAG_ADDR_COUNTY);
        countyEnabled.setFocusable(false);
        countyEnabled.setSelected(dto.isSaveCounty());
        countyEnabled.setToolTipText(APPLY_CHANGES);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0;
        c.gridwidth = 3;
        editPanel.add(countyEnabled, c);

        county = new AutoCompletingComboBox();
        county.setPossibleACItems(acm.getValues(TAG_ADDR_COUNTY));
        county.setPreferredSize(new Dimension(200, 24));
        county.setEditable(true);
        county.setSelectedItem(dto.getCounty());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 3;
        c.weightx = 1;
        c.gridwidth = 1;
        editPanel.add(county, c);

        // city
        cityEnabled = new JCheckBox(TAG_ADDR_CITY);
        cityEnabled.setFocusable(false);
        cityEnabled.setSelected(dto.isSaveCity());
        cityEnabled.setToolTipText(APPLY_CHANGES);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 0;
        c.gridwidth = 3;
        editPanel.add(cityEnabled, c);

        city = new AutoCompletingComboBox();
        city.setPossibleACItems(acm.getValues(TAG_ADDR_CITY));
        city.setPreferredSize(new Dimension(200, 24));
        city.setEditable(true);
        city.setSelectedItem(dto.getCity());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 4;
        c.weightx = 1;
        c.gridwidth = 1;
        editPanel.add(city, c);

        // postcode
        postcodeEnabled = new JCheckBox(TAG_ADDR_POSTCODE);
        postcodeEnabled.setFocusable(false);
        postcodeEnabled.setSelected(dto.isSavePostcode());
        postcodeEnabled.setToolTipText(APPLY_CHANGES);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 0;
        c.gridwidth = 3;
        editPanel.add(postcodeEnabled, c);

        postcode = new AutoCompletingComboBox();
        postcode.setPossibleACItems(acm.getValues(TAG_ADDR_POSTCODE));
        postcode.setPreferredSize(new Dimension(200, 24));
        postcode.setEditable(true);
        postcode.setSelectedItem(dto.getPostcode());
        postcode.setMaxTextLength(8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 5;
        c.weightx = 1;
        c.gridwidth = 1;
        editPanel.add(postcode, c);

        // suburb
        suburbEnabled = new JCheckBox(TAG_ADDR_SUBURB);
        suburbEnabled.setFocusable(false);
        suburbEnabled.setSelected(dto.isSaveSuburb());
        suburbEnabled.setToolTipText(APPLY_CHANGES);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 6;
        c.weightx = 0;
        c.gridwidth = 3;
        editPanel.add(suburbEnabled, c);

        suburb = new AutoCompletingComboBox();
        suburb.setPossibleACItems(acm.getValues(TAG_ADDR_SUBURB));
        suburb.setPreferredSize(new Dimension(200, 24));
        suburb.setEditable(true);
        suburb.setSelectedItem(dto.getSuburb());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 6;
        c.weightx = 1;
        c.gridwidth = 1;
        editPanel.add(suburb, c);

        // quarter
        quarterEnabled = new JCheckBox(TAG_ADDR_QUARTER);
        quarterEnabled.setFocusable(false);
        quarterEnabled.setSelected(dto.isSaveQuarter());
        quarterEnabled.setToolTipText(APPLY_CHANGES);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 7;
        c.weightx = 0;
        c.gridwidth = 3;
        editPanel.add(quarterEnabled, c);

        quarter = new AutoCompletingComboBox();
        quarter.setPossibleACItems(acm.getValues(TAG_ADDR_QUARTER));
        quarter.setPreferredSize(new Dimension(200, 24));
        quarter.setEditable(true);
        quarter.setSelectedItem(dto.getQuarter());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 7;
        c.weightx = 1;
        c.gridwidth = 1;
        editPanel.add(quarter, c);

        // neighbourhood
        neighbourhoodEnabled = new JCheckBox(TAG_ADDR_NEIGHBOURHOOD);
        neighbourhoodEnabled.setFocusable(false);
        neighbourhoodEnabled.setSelected(dto.isSaveNeighbourhood());
        neighbourhoodEnabled.setToolTipText(APPLY_CHANGES);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 8;
        c.weightx = 0;
        c.gridwidth = 3;
        editPanel.add(neighbourhoodEnabled, c);

        neighbourhood = new AutoCompletingComboBox();
        neighbourhood.setPossibleACItems(acm.getValues(TAG_ADDR_NEIGHBOURHOOD));
        neighbourhood.setPreferredSize(new Dimension(200, 24));
        neighbourhood.setEditable(true);
        neighbourhood.setSelectedItem(dto.getNeighbourhood());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 8;
        c.weightx = 1;
        c.gridwidth = 1;
        editPanel.add(neighbourhood, c);

        // block_number
        block_numberEnabled = new JCheckBox(TAG_ADDR_BLOCK_NUMBER);
        block_numberEnabled.setFocusable(false);
        block_numberEnabled.setSelected(dto.isSaveBlockNumber());
        block_numberEnabled.setToolTipText(APPLY_CHANGES);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 9;
        c.weightx = 0;
        c.gridwidth = 3;
        editPanel.add(block_numberEnabled, c);

        block_number = new AutoCompletingComboBox();
        block_number.setPossibleACItems(acm.getValues(TAG_ADDR_BLOCK_NUMBER));
        block_number.setPreferredSize(new Dimension(200, 24));
        block_number.setEditable(true);
        block_number.setSelectedItem(dto.getBlockNumber());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 9;
        c.weightx = 1;
        c.gridwidth = 1;
        editPanel.add(block_number, c);

        // housenumber
        housenumberEnabled = new JCheckBox(TAG_ADDR_HOUSENUMBER);
        housenumberEnabled.setFocusable(false);
        housenumberEnabled.setSelected(dto.isSaveHousenumber());
        housenumberEnabled.setToolTipText(APPLY_CHANGES);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 10;
        c.weightx = 0;
        c.gridwidth = 3;
        editPanel.add(housenumberEnabled, c);

        housenumber = new JTextField();
        housenumber.setPreferredSize(new Dimension(200, 24));

        int number = 0;
        try {
            number = Integer.valueOf(dto.getHousenumber()) + dto.getHousenumberChangeValue();
        } catch (NumberFormatException e)  {
            // Do nothing
        }
        if (number > 0) {
            housenumber.setText(String.valueOf(number));
        }

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 10;
        c.weightx = 1;
        c.gridwidth = 1;
        editPanel.add(housenumber, c);

        JLabel seqLabel = new JLabel(tr("House number increment:"));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 11;
        c.weightx = 0;
        c.gridwidth = 3;
        editPanel.add(seqLabel, c);

        housenumberChangeSequence = new JSlider(JSlider.HORIZONTAL,  FPS_MIN, FPS_MAX, dto.getHousenumberChangeValue());
        housenumberChangeSequence.setPaintTicks(true);
        housenumberChangeSequence.setMajorTickSpacing(1);
        housenumberChangeSequence.setMinorTickSpacing(1);
        housenumberChangeSequence.setPaintLabels(true);
        housenumberChangeSequence.setSnapToTicks(true);
        c.gridx = 3;
        c.gridy = 11;
        c.weightx = 1;
        c.gridwidth = 1;
        editPanel.add(housenumberChangeSequence, c);

        return editPanel;
    }

    @Override
    protected void buttonAction(int buttonIndex, ActionEvent evt) {
        if (buttonIndex == 0) {
            Dto dto = new Dto();
            dto.setSaveBuilding(buildingEnabled.isSelected());
            dto.setSaveCountry(countryEnabled.isSelected());
            dto.setSaveProvince(provinceEnabled.isSelected());
            dto.setSaveCounty(countyEnabled.isSelected());
            dto.setSaveCity(cityEnabled.isSelected());
            dto.setSaveSuburb(suburbEnabled.isSelected());
            dto.setSaveQuarter(quarterEnabled.isSelected());
            dto.setSaveNeighbourhood(neighbourhoodEnabled.isSelected());
            dto.setSaveBlockNumber(block_numberEnabled.isSelected());
            dto.setSaveHousenumber(housenumberEnabled.isSelected());
            dto.setSavePostcode(postcodeEnabled.isSelected());

            dto.setBuilding((String) building.getSelectedItem());
            dto.setCountry(getAutoCompletingComboBoxValue(country));
            dto.setProvince(getAutoCompletingComboBoxValue(province));
            dto.setCounty(getAutoCompletingComboBoxValue(county));
            dto.setCity(getAutoCompletingComboBoxValue(city));
            dto.setSuburb(getAutoCompletingComboBoxValue(suburb));
            dto.setQuarter(getAutoCompletingComboBoxValue(quarter));
            dto.setNeighbourhood(getAutoCompletingComboBoxValue(neighbourhood));
            dto.setBlockNumber(getAutoCompletingComboBoxValue(block_number));
            dto.setHousenumber(housenumber.getText());
            dto.setPostcode(getAutoCompletingComboBoxValue(postcode));
            dto.setHousenumberChangeValue(housenumberChangeSequence.getValue());

            updateJOSMSelection(selection, dto);
            saveDto(dto);
        }
        setVisible(false);
    }

    private String getAutoCompletingComboBoxValue(AutoCompletingComboBox box) {
        Object item = box.getSelectedItem();
        if (item != null) {
            if (item instanceof String) {
                return (String) item;
            }
            if (item instanceof AutoCompletionListItem) {
                return ((AutoCompletionListItem) item).getValue();
            }
            return item.toString();
        } else {
            return "";
        }
    }

    protected void saveDto(Dto dto) {
        File path = new File(pluginDir);
        File fileName = new File(pluginDir + TagDialog.TEMPLATE_DATA);

        try {
            path.mkdirs();
            try (
                FileOutputStream file = new FileOutputStream(fileName);
                ObjectOutputStream o = new ObjectOutputStream(file)
            ) {
                o.writeObject(dto);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage());
            fileName.delete();
        }
    }

    protected void updateJOSMSelection(OsmPrimitive selection, Dto dto) {
        List<Command> commands = new ArrayList<>();

        if (dto.isSaveBuilding()) {
            String value = selection.get(TagDialog.TAG_BUILDING);
            if (value == null || (value != null && !value.equals(dto.getBuilding()))) {
                ChangePropertyCommand command = new ChangePropertyCommand(selection, TagDialog.TAG_BUILDING, dto.getBuilding());
                commands.add(command);
            }
        }

        if (dto.isSaveCountry())  {
            String value = selection.get(TagDialog.TAG_ADDR_COUNTRY);
            if (value == null || (value != null && !value.equals(dto.getCountry()))) {
                ChangePropertyCommand command = new ChangePropertyCommand(selection, TagDialog.TAG_ADDR_COUNTRY, dto.getCountry());
                commands.add(command);
            }
        }

        if (dto.isSaveProvince()) {
            String value = selection.get(TagDialog.TAG_ADDR_PROVINCE);
            if (value == null || (value != null && !value.equals(dto.getProvince()))) {
                ChangePropertyCommand command = new ChangePropertyCommand(selection, TagDialog.TAG_ADDR_PROVINCE, dto.getProvince());
                commands.add(command);
            }
        }

        if (dto.isSaveCounty()) {
            String value = selection.get(TagDialog.TAG_ADDR_COUNTY);
            if (value == null || (value != null && !value.equals(dto.getCounty()))) {
                ChangePropertyCommand command = new ChangePropertyCommand(selection, TagDialog.TAG_ADDR_COUNTY, dto.getCounty());
                commands.add(command);
            }
        }

        if (dto.isSaveCity()) {
            String value = selection.get(TagDialog.TAG_ADDR_CITY);
            if (value == null || (value != null && !value.equals(dto.getCity()))) {
                ChangePropertyCommand command = new ChangePropertyCommand(selection, TagDialog.TAG_ADDR_CITY, dto.getCity());
                commands.add(command);
            }
        }

        if (dto.isSaveSuburb()) {
            String value = selection.get(TagDialog.TAG_ADDR_SUBURB);
            if (value == null || (value != null && !value.equals(dto.getSuburb()))) {
                ChangePropertyCommand command = new ChangePropertyCommand(selection, TagDialog.TAG_ADDR_SUBURB, dto.getSuburb());
                commands.add(command);
            }
        }

        if (dto.isSaveQuarter()) {
            String value = selection.get(TagDialog.TAG_ADDR_QUARTER);
            if (value == null || (value != null && !value.equals(dto.getQuarter()))) {
                ChangePropertyCommand command = new ChangePropertyCommand(selection, TagDialog.TAG_ADDR_QUARTER, dto.getQuarter());
                commands.add(command);
            }
        }

        if (dto.isSaveNeighbourhood()) {
            String value = selection.get(TagDialog.TAG_ADDR_NEIGHBOURHOOD);
            if (value == null || (value != null && !value.equals(dto.getNeighbourhood()))) {
                ChangePropertyCommand command = new ChangePropertyCommand(selection, TagDialog.TAG_ADDR_NEIGHBOURHOOD, dto.getNeighbourhood());
                commands.add(command);
            }
        }

        if (dto.isSaveBlockNumber()) {
            String value = selection.get(TagDialog.TAG_ADDR_BLOCK_NUMBER);
            if (value == null || (value != null && !value.equals(dto.getBlockNumber()))) {
                ChangePropertyCommand command = new ChangePropertyCommand(selection, TagDialog.TAG_ADDR_BLOCK_NUMBER, dto.getBlockNumber());
                commands.add(command);
            }
        }

        if (dto.isSaveHousenumber())  {
            String value = selection.get(TagDialog.TAG_ADDR_HOUSENUMBER);
            if (value == null || (value != null && !value.equals(dto.getHousenumber()))) {
                ChangePropertyCommand command = new ChangePropertyCommand(selection, TagDialog.TAG_ADDR_HOUSENUMBER, dto.getHousenumber());
                commands.add(command);
            }
        }

        if (dto.isSavePostcode()) {
            String value = selection.get(TagDialog.TAG_ADDR_POSTCODE);
            if (value == null || (value != null && !value.equals(dto.getPostcode()))) {
                ChangePropertyCommand command = new ChangePropertyCommand(selection, TagDialog.TAG_ADDR_POSTCODE, dto.getPostcode());
                commands.add(command);
            }
        }

        if (!commands.isEmpty()) {
            SequenceCommand sequenceCommand = new SequenceCommand(
                 trn("Updating properties of up to {0} object", 
                     "Updating properties of up to {0} objects", commands.size(), commands.size()), commands);

            // executes the commands and adds them to the undo/redo chains
            Main.main.undoRedo.add(sequenceCommand);
        }
    }

    private Dto loadDto() {
        Dto dto = new Dto();
        File fileName = new File(pluginDir + TagDialog.TEMPLATE_DATA);

        try {
            if (fileName.exists()) {
                try (
                    FileInputStream file = new FileInputStream(fileName);
                    ObjectInputStream o = new ObjectInputStream(file);
                ) {
                    dto = (Dto) o.readObject();
                }
            } else {
            	dto.setCountry(selection.get(TagDialog.TAG_ADDR_COUNTY));
            	dto.setProvince(selection.get(TagDialog.TAG_ADDR_PROVINCE));
                dto.setCity(selection.get(TagDialog.TAG_ADDR_CITY));
                dto.setSuburb(selection.get(TagDialog.TAG_ADDR_SUBURB));
                dto.setQuarter(selection.get(TagDialog.TAG_ADDR_QUARTER));
                dto.setNeighbourhood(selection.get(TagDialog.TAG_ADDR_NEIGHBOURHOOD));
                dto.setBlockNumber(selection.get(TagDialog.TAG_ADDR_BLOCK_NUMBER));
                dto.setCountry(selection.get(TagDialog.TAG_ADDR_COUNTRY));
                dto.setHousenumber(selection.get(TagDialog.TAG_ADDR_HOUSENUMBER));
                dto.setPostcode(selection.get(TagDialog.TAG_ADDR_POSTCODE));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage());
            fileName.delete();
        }
        return dto;
    }
}
