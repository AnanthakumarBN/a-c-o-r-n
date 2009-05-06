/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.visualizationfactory;

import org.graphscene.GraphModelScene;
import acorn.webservice.RepeatedVisualizationNameException_Exception;
import acorn.webservice.VisValidationException_Exception;
import acorn.data.buffer.DBSupporter;
import acorn.data.buffer.structures.BadKeyInBufferStruct;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.dbStructs.NameStruct;
import org.exceptions.VisValidationException;
import org.interfaces.LoadSaveInterface;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.view.NoSelectBorder;
import org.view.NodeWidget;
import org.view.SelectBorder;
import org.visualapi.VisNode;

enum ComputationsVis {

    VISUALIZE, NOTVISUALIZE
}

public class VisualizationFactoryTopComponent extends TopComponent {

    final static String VISCOMP = "VISUALIZE COMPUTATIONS";
    private Lookup.Result result;
    private Lookup.Result visCompResult;
    InstanceContent content;
    private JComponent graphView;
    private static VisualizationFactoryTopComponent instance;
    private static final String PREFERRED_ID = "VisualizationFactoryTopComponent";
    private NodeWidget selectedWidget = null;
    private DBSupporter dbsupp = new DBSupporter();
    SelectBorder selectBorder = new SelectBorder();
    NoSelectBorder noselBorder = new NoSelectBorder();

    private VisualizationFactoryTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(VisualizationFactoryTopComponent.class, "CTL_VisualizationFactoryTopComponent"));
        setToolTipText(NbBundle.getMessage(VisualizationFactoryTopComponent.class, "HINT_VisualizationFactoryTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));
        content = new InstanceContent();
        associateLookup(new AbstractLookup(content));

        GraphModelScene scene = new GraphModelScene(new GraphModelScene.SelectionNodeListener() {

            public void nodeSelected(NodeWidget w) {
                if (dbsupp.getModelName() != null) {
                    if (selectedWidget != null) {
                        selectedWidget.getLabelWidget().setForeground(Color.BLACK);
                        selectedWidget.setBorder(noselBorder);
                        content.remove(selectedWidget.getVisNode());
                    }
                    selectedWidget = w;
                    VisNode node = w.getVisNode();
                    //Adds node to lookup associated with VisualFactoryTopComponent
                    content.add(node);
                    List<NameStruct> structNames = null;
                    if (w.isPlaceWidget()) {
                        nodeNameLabel.setText("Name of place: ");
                        structNames = dbsupp.getSuitableSpecies(node.getSourceNodesStruct(), node.getTargetNodesStruct());

                    } else if (w.isTransitionWidget()) {
                        nodeNameLabel.setText("Name of transition: ");
                        structNames = dbsupp.getSuitableReactions(node.getSourceNodesStruct(), node.getTargetNodesStruct());
                    }
                    structNames = node.removeUsedNodes(structNames);
                    namesComboBox.removeAllItems();
                    if (structNames.size() == 0) {
                        setNameButton.setEnabled(false);
                        namesComboBox.setEnabled(false);
                    } else {
                        for (NameStruct name : structNames) {
                            namesComboBox.addItem(name);
                        }
                    }
                    w.setBorder(selectBorder);
                    w.getLabelWidget().setForeground(Color.BLUE);
                    namesComboBox.setEnabled(true);
                    if (structNames.size() == 0) {
                        setNameButton.setEnabled(false);
                    } else {
                        setNameButton.setEnabled(true);
                    }
                }
            }

            public void unselect() {
                if (selectedWidget != null) {
                    selectedWidget.getLabelWidget().setForeground(Color.BLACK);
                    selectedWidget.setBorder(noselBorder);
                    content.remove(selectedWidget.getVisNode());
                }
                selectedWidget = null;
                namesComboBox.setEnabled(false);
                setNameButton.setEnabled(false);
                nodeNameLabel.setText("Select place or transition");
            }
        });


        dbsupp.setGraphProvider(scene.getLoadSaveListener());
        graphView = scene.createView();
        jScrollPane1.setViewportView(graphView);
        add(scene.createSatelliteView(), BorderLayout.WEST);

        result = org.openide.util.Utilities.actionsGlobalContext().lookupResult(VisNode.class);
        result.addLookupListener(new LookupListener() {

            @Override
            public void resultChanged(LookupEvent e) {
                Collection<VisNode> c = (Collection<VisNode>) result.allInstances();
                if (c.size() == 1) {
                    VisNode node = c.iterator().next();
                    fullNameLabel.setEnabled(true);
                    fullName.setText(node.getName());
                    if (node.isPlace()) {
                        fullNameLabel.setText("Full name of species:");
                    } else {
                        fullNameLabel.setText("Full name of reaction:");
                    }
                } else {
                    fullName.setText("");
                    fullNameLabel.setEnabled(false);
                }
            }
        });

        visCompResult = org.openide.util.Utilities.actionsGlobalContext().lookupResult(ComputationsVis.class);
        visCompResult.addLookupListener(new LookupListener() {

            @Override
            public void resultChanged(LookupEvent e) {
                Collection<ComputationsVis> c = (Collection<ComputationsVis>) visCompResult.allInstances();
                if(c.isEmpty()){
                    System.out.println("AAAAAAAAAAAAA");
                }else{
                    System.out.println("BBBBBBBBBBB");
                }
            }
        });
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        visualizationNamesComboBox1 = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        settingsPanel = new javax.swing.JTabbedPane();
        programPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        modelsComboBox = new javax.swing.JComboBox();
        Object[] elements = (Object[]) dbsupp.getModelNameList();
        AutoCompleteSupport support = AutoCompleteSupport.install(modelsComboBox, GlazedLists.eventListOf(elements));
        setModelButton = new javax.swing.JButton();
        ModelNameLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        validSavePanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        visualizationNameTextField = new javax.swing.JTextField();
        saveVisToDBButton = new javax.swing.JButton();
        SaveErrorsLabel = new javax.swing.JLabel();
        validateButton = new javax.swing.JButton();
        eraseAndSaveButton = new javax.swing.JButton();
        clearVisualizationButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        deleteVisualizationButton = new javax.swing.JButton();
        loadVisualizationButton = new javax.swing.JButton();
        visualizationNamesLabel = new javax.swing.JLabel();
        visualizationNamesComboBox = new javax.swing.JComboBox();
        visualizationPanel = new javax.swing.JPanel();
        nodeNameLabel = new javax.swing.JLabel();
        namesComboBox = new javax.swing.JComboBox();
        setNameButton = new javax.swing.JButton();
        fullNameLabel = new javax.swing.JLabel();
        fullName = new javax.swing.JLabel();
        computationsCheckBox = new javax.swing.JCheckBox();
        addCompToggleButton = new javax.swing.JToggleButton();

        visualizationNamesComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[0]));
        visualizationNamesComboBox1.setEnabled(false);

        setLayout(new java.awt.BorderLayout());
        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        settingsPanel.setToolTipText(org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.settingsPanel.toolTipText")); // NOI18N
        settingsPanel.setPreferredSize(new java.awt.Dimension(853, 150));

        programPanel.setToolTipText(org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.programPanel.toolTipText")); // NOI18N
        programPanel.setPreferredSize(new java.awt.Dimension(849, 300));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setLabelFor(modelsComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.jLabel1.text")); // NOI18N

        modelsComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modelsComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(setModelButton, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.setModelButton.text")); // NOI18N
        setModelButton.setMinimumSize(new java.awt.Dimension(130, 29));
        setModelButton.setEnabled(false);
        setModelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setModelButtonActionPerformed(evt);
            }
        });

        ModelNameLabel.setForeground(java.awt.Color.red);
        org.openide.awt.Mnemonics.setLocalizedText(ModelNameLabel, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.ModelNameLabel.text")); // NOI18N

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout programPanelLayout = new javax.swing.GroupLayout(programPanel);
        programPanel.setLayout(programPanelLayout);
        programPanelLayout.setHorizontalGroup(
            programPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(programPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(programPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(programPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(setModelButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(ModelNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(modelsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1075, Short.MAX_VALUE))
        );
        programPanelLayout.setVerticalGroup(
            programPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(programPanelLayout.createSequentialGroup()
                .addGroup(programPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(programPanelLayout.createSequentialGroup()
                        .addGroup(programPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(modelsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(setModelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ModelNameLabel))
                    .addGroup(programPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        settingsPanel.addTab(org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.programPanel.TabConstraints.tabTitle"), programPanel); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.jLabel2.text")); // NOI18N

        visualizationNameTextField.setText(org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.visualizationNameTextField.text")); // NOI18N
        visualizationNameTextField.setEnabled(false);
        visualizationNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visualizationNameTextFieldActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(saveVisToDBButton, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.saveVisToDBButton.text")); // NOI18N
        saveVisToDBButton.setEnabled(false);
        saveVisToDBButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveVisToDBButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(SaveErrorsLabel, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.SaveErrorsLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(validateButton, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.validateButton.text")); // NOI18N
        validateButton.setEnabled(false);
        validateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validateButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(eraseAndSaveButton, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.eraseAndSaveButton.text")); // NOI18N
        eraseAndSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eraseAndSaveButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(clearVisualizationButton, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.clearVisualizationButton.text")); // NOI18N
        clearVisualizationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearVisualizationButtonActionPerformed(evt);
            }
        });

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        org.openide.awt.Mnemonics.setLocalizedText(deleteVisualizationButton, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.deleteVisualizationButton.text")); // NOI18N
        deleteVisualizationButton.setEnabled(false);
        deleteVisualizationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteVisualizationButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(loadVisualizationButton, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.loadVisualizationButton.text")); // NOI18N
        loadVisualizationButton.setEnabled(false);
        loadVisualizationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadVisualizationButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(visualizationNamesLabel, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.visualizationNamesLabel.text")); // NOI18N

        visualizationNamesComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[0]));
        visualizationNamesComboBox.setEnabled(false);

        javax.swing.GroupLayout validSavePanelLayout = new javax.swing.GroupLayout(validSavePanel);
        validSavePanel.setLayout(validSavePanelLayout);
        validSavePanelLayout.setHorizontalGroup(
            validSavePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(validSavePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(validSavePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(validSavePanelLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(visualizationNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(SaveErrorsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(validSavePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(saveVisToDBButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(eraseAndSaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(validSavePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(validateButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clearVisualizationButton, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(validSavePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(loadVisualizationButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(deleteVisualizationButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(visualizationNamesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(visualizationNamesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(482, Short.MAX_VALUE))
        );
        validSavePanelLayout.setVerticalGroup(
            validSavePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, validSavePanelLayout.createSequentialGroup()
                .addGroup(validSavePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addGroup(validSavePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(validSavePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(visualizationNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(validSavePanelLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(validSavePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(saveVisToDBButton)
                                    .addComponent(validateButton))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(validSavePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(validSavePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(eraseAndSaveButton)
                                .addComponent(clearVisualizationButton))
                            .addComponent(SaveErrorsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, validSavePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(validSavePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(visualizationNamesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(visualizationNamesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deleteVisualizationButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(loadVisualizationButton)))
                .addContainerGap())
        );

        settingsPanel.addTab(org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.validSavePanel.TabConstraints.tabTitle"), validSavePanel); // NOI18N

        nodeNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(nodeNameLabel, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.nodeNameLabel.text")); // NOI18N

        namesComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        namesComboBox.setEnabled(false);
        namesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                namesComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(setNameButton, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.setNameButton.text")); // NOI18N
        setNameButton.setEnabled(false);
        setNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setNameButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(fullNameLabel, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.fullNameLabel.text")); // NOI18N
        fullNameLabel.setEnabled(false);
        fullNameLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        org.openide.awt.Mnemonics.setLocalizedText(fullName, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.fullName.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(computationsCheckBox, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.computationsCheckBox.text")); // NOI18N
        computationsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                computationsCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(addCompToggleButton, org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.addCompToggleButton.text")); // NOI18N
        addCompToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCompToggleButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout visualizationPanelLayout = new javax.swing.GroupLayout(visualizationPanel);
        visualizationPanel.setLayout(visualizationPanelLayout);
        visualizationPanelLayout.setHorizontalGroup(
            visualizationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(visualizationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(visualizationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(nodeNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(visualizationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(fullNameLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(setNameButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)))
                .addGap(53, 53, 53)
                .addGroup(visualizationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(visualizationPanelLayout.createSequentialGroup()
                        .addComponent(addCompToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fullName, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(computationsCheckBox)
                    .addComponent(namesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(662, Short.MAX_VALUE))
        );
        visualizationPanelLayout.setVerticalGroup(
            visualizationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(visualizationPanelLayout.createSequentialGroup()
                .addGroup(visualizationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nodeNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(namesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(visualizationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(setNameButton)
                    .addComponent(computationsCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(visualizationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(visualizationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(fullNameLabel)
                        .addComponent(fullName, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(addCompToggleButton))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        settingsPanel.addTab(org.openide.util.NbBundle.getMessage(VisualizationFactoryTopComponent.class, "VisualizationFactoryTopComponent.visualizationPanel.TabConstraints.tabTitle"), visualizationPanel); // NOI18N

        add(settingsPanel, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void addCompToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCompToggleButtonActionPerformed
        if (addCompToggleButton.isSelected()) {
            content.add(ComputationsVis.VISUALIZE);
            dbsupp.addcomputationsToTransitionsName(true);
            addCompToggleButton.setText("Remove computations from vis");
        } else {
            content.remove(ComputationsVis.VISUALIZE);
            dbsupp.addcomputationsToTransitionsName(false);
            addCompToggleButton.setText("Add computations to vis");
        }
}//GEN-LAST:event_addCompToggleButtonActionPerformed

    private void computationsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_computationsCheckBoxActionPerformed
        if (computationsCheckBox.isSelected()) {
            content.add(ComputationsVis.VISUALIZE);
            dbsupp.addcomputationsToTransitionsName(true);
        } else {
            content.remove(ComputationsVis.VISUALIZE);
            dbsupp.addcomputationsToTransitionsName(false);
        }
}//GEN-LAST:event_computationsCheckBoxActionPerformed

    private void setNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setNameButtonActionPerformed
        if (selectedWidget instanceof NodeWidget) {
            NameStruct selectedStruct = (NameStruct) namesComboBox.getSelectedItem();
            selectedWidget.setNameAndSid(selectedStruct.getName(), selectedStruct.getSid());
            selectedWidget.setLabel(selectedStruct.getSid());
            if (selectedWidget.isTransitionWidget()) {
                dbsupp.setSpeciesForReaction(selectedStruct);
            }
            content.remove(selectedWidget.getVisNode());
            content.add(selectedWidget.getVisNode());
        }
}//GEN-LAST:event_setNameButtonActionPerformed

    private void namesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namesComboBoxActionPerformed
        setNameButton.setEnabled(true);
}//GEN-LAST:event_namesComboBoxActionPerformed

    private void loadVisualizationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadVisualizationButtonActionPerformed
        String visName = (String) visualizationNamesComboBox.getSelectedItem();
        dbsupp.getVisualization(visName);
}//GEN-LAST:event_loadVisualizationButtonActionPerformed

    private void deleteVisualizationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteVisualizationButtonActionPerformed
        String visName = (String) visualizationNamesComboBox.getSelectedItem();
        dbsupp.removeVisualization(visName);
        this.updateVisualizationNamesComboBox();
}//GEN-LAST:event_deleteVisualizationButtonActionPerformed

    private void clearVisualizationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearVisualizationButtonActionPerformed
        dbsupp.clearVisualizatioon();
}//GEN-LAST:event_clearVisualizationButtonActionPerformed

    private void eraseAndSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eraseAndSaveButtonActionPerformed
        String visName = visualizationNameTextField.getText();
        if (visName == null || visName.equals("")) {
            SaveErrorsLabel.setText("set name for Visualization.");
            return;
        }
        try {
            dbsupp.eraseOldSaveNewVisualization(visName);
        } catch (RepeatedVisualizationNameException_Exception ex) {
            SaveErrorsLabel.setText(ex.getMessage());
        } catch (BadKeyInBufferStruct ex) {
            SaveErrorsLabel.setText(ex.getMessage());
        } catch (VisValidationException ex) {
            SaveErrorsLabel.setText(ex.getMessage());
        } catch (VisValidationException_Exception ex) {
            SaveErrorsLabel.setText(ex.getMessage());
        }
        this.updateVisualizationNamesComboBox();
}//GEN-LAST:event_eraseAndSaveButtonActionPerformed

    private void validateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validateButtonActionPerformed
        try {
            dbsupp.validateVisualization();
            SaveErrorsLabel.setText("Visualization is valid.");
        } catch (BadKeyInBufferStruct ex) {
            SaveErrorsLabel.setText(ex.getMessage());
        } catch (VisValidationException ex) {
            SaveErrorsLabel.setText(ex.getMessage());
        }
}//GEN-LAST:event_validateButtonActionPerformed

    private void saveVisToDBButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveVisToDBButtonActionPerformed

        String visName = visualizationNameTextField.getText();
        if (visName == null || visName.equals("")) {
            SaveErrorsLabel.setText("set name for Visualization.");
            return;
        }
        try {
            dbsupp.saveVisualization(visName);
        } catch (RepeatedVisualizationNameException_Exception ex) {
            SaveErrorsLabel.setText(ex.getMessage());
        } catch (BadKeyInBufferStruct ex) {
            SaveErrorsLabel.setText(ex.getMessage());
        } catch (VisValidationException ex) {
            SaveErrorsLabel.setText(ex.getMessage());
        } catch (VisValidationException_Exception ex) {
            SaveErrorsLabel.setText(ex.getMessage());
        }
        this.updateVisualizationNamesComboBox();
}//GEN-LAST:event_saveVisToDBButtonActionPerformed

    private void visualizationNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visualizationNameTextFieldActionPerformed
        saveVisToDBButton.setEnabled(true);
}//GEN-LAST:event_visualizationNameTextFieldActionPerformed

    private void setModelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setModelButtonActionPerformed
        String modelName = (String) modelsComboBox.getSelectedItem();
        boolean isProperName = dbsupp.setModelName(modelName);
        if (isProperName) {
            ModelNameLabel.setText("Your model is: " + modelName);

            enableButtons();
            updateVisualizationNamesComboBox();
            content.add(dbsupp);
        }
        setModelButton.setEnabled(false);
}//GEN-LAST:event_setModelButtonActionPerformed

    private void modelsComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modelsComboBoxActionPerformed
        setModelButton.setEnabled(true);
}//GEN-LAST:event_modelsComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ModelNameLabel;
    private javax.swing.JLabel SaveErrorsLabel;
    private javax.swing.JToggleButton addCompToggleButton;
    private javax.swing.JButton clearVisualizationButton;
    private javax.swing.JCheckBox computationsCheckBox;
    private javax.swing.JButton deleteVisualizationButton;
    private javax.swing.JButton eraseAndSaveButton;
    private javax.swing.JLabel fullName;
    private javax.swing.JLabel fullNameLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JButton loadVisualizationButton;
    private javax.swing.JComboBox modelsComboBox;
    private javax.swing.JComboBox namesComboBox;
    private javax.swing.JLabel nodeNameLabel;
    private javax.swing.JPanel programPanel;
    private javax.swing.JButton saveVisToDBButton;
    private javax.swing.JButton setModelButton;
    private javax.swing.JButton setNameButton;
    private javax.swing.JTabbedPane settingsPanel;
    private javax.swing.JPanel validSavePanel;
    private javax.swing.JButton validateButton;
    private javax.swing.JTextField visualizationNameTextField;
    private javax.swing.JComboBox visualizationNamesComboBox;
    private javax.swing.JComboBox visualizationNamesComboBox1;
    private javax.swing.JLabel visualizationNamesLabel;
    private javax.swing.JPanel visualizationPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized VisualizationFactoryTopComponent getDefault() {
        if (instance == null) {
            instance = new VisualizationFactoryTopComponent();
        }
        return instance;
    }

    private void updateVisualizationNamesComboBox() {
        List<String> visNames = dbsupp.getVisualizationNames();
        visualizationNamesComboBox.removeAllItems();
        for (String name : visNames) {
            visualizationNamesComboBox.addItem(name);
        }
    }

    private void enableButtons() {
        saveVisToDBButton.setEnabled(true);
        validateButton.setEnabled(true);
        visualizationNameTextField.setEnabled(true);
        visualizationNamesComboBox.setEnabled(true);
        loadVisualizationButton.setEnabled(true);
        deleteVisualizationButton.setEnabled(true);
    }

    /**
     * Obtain the VisualizationFactoryTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized VisualizationFactoryTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(VisualizationFactoryTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof VisualizationFactoryTopComponent) {
            return (VisualizationFactoryTopComponent) win;
        }
        Logger.getLogger(VisualizationFactoryTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
        }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
        }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return VisualizationFactoryTopComponent.getDefault();
        }
    }
}
