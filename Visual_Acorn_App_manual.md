# Introduction #

This wiki describes a few usecases of Visual Acorn Application and some hints.

## Dictionary ##
  * VAApp - Visual Acorn Application
  * Reaction - it is black rectangle in visualization. It will be also named Transition (in respect of Petri Net)
  * Species - it is circle in visualization. It will be also named Place.
  * Node - it's Transition or Place.
  * Task - it's task computed by amkfba. You can see this computations near name of transitions in visualization (if task is FBA and done)
  * Drawing panel - panel in VAApp, where you draw visualizations

# UseCases #
For running VAApp do:
  * unzip visualacornapplication.zip. Enter _visualacornapplication/bin_ directory. Run visualacornapplication(Linux) or visualacornapplication.exe (Windows).
> or
  * follow Acorn\_Installation\_on\_Remote\_Server wiki

  * In application you can find "VisualizationFactory Window" window with panels:
    * Program settings" - you set model for which you will create visualization
    * Validate/Save/Delete Vis" - you validate, save, delete, load visualizations
    * Visualization settings" - you set name for Species nad Reacitons/ add computations to visualizations of FBA Task which is done.
  * In panel "Programs settings" choose model. Click "Set model for visualization".

## Opening Visualiztion Factory Window ##
  * If you close "VisualizationFactory Window" you can reopen it.
  * **ALT+W** or click Window menu in menu bar and choose "Visualization Factory" item.

## Adding transition or place ##
  * Right mouse button click (left if you are lefthanded) on screen where visualization will be created.
  * Choose "Add place" or "Add transition".

## Adding arrow to Place and Transition ##
  * Hold **CTRL** key, click left mouse button on Node (where arrow will be stared).
  * Hold **CTRL** key and mouse button
  * Take mouse arrow to the Node which you want to connect.
  * Let loose **CTRL** key and mouse button.

## Removing arrow ##
  * Click on arrow.
  * Remove one of its edge from node - click mouse arrow on its edge and hold  it. Move mouse arrow. Let loose mouse.
OR:
  * click right mouse button on arrow - popup menu emerges
  * click "Delete arrow"

## Naming Nodes ##
  * Click on Node. Blue reactangle should appear.
  * Select "Visualization settings" panel.
  * Select name from drop down list(near label: "Name of transition/place"). Transition and place is named by its SID.
  * Click "Set name" button.

## Zooming in/out and moving drawing panel ##
  * Hold **CTRL** key, use the mouse wheel to zoom in and out drawing panel
  * Hold down the mouse wheel and then scroll(move) in any direction to move visible part of drawing panel
  * You can find satellite view of your vizualization on the left hand. There is also rectangle - the part of visualziation that you see. You can move it by dragging it by mouse

## Adding computations (done by amkfba) to visualiztion ##
  * Two radio buttons are on right hand In "Visualization settings" panel
  * If Task which model you selected is FBA and done these buttons are active.
  * Select this radio which you want.

## Saving/Resaving ##
  * In panel "validate/Save/Delete Vis" in text field near "visualization's name" label write name of visualization
  * Click "Save to database"for saving or "Erase old and Save" for resaving.

## Validating Visualization / clearing visualization ##
  * In panel "validate/Save/Delete Vis" click "Validate visualization" button for validation. If visualization is not valid hints, how to make it valid, will emerge.
  * Click "Clear Visualization" button if you want to remove visualziation from drawing panel (clear it).

## Loading / Removing visualizations from Acorn web system ##
  * In panel "validate/Save/Delete Vis" on right hand from drop down list select visualization name.
  * Click "Load Visualization" Button if you want to edit visualization
  * Click "Delete Visualization" if you want to delete it from Acorn web system

## Generating reactants/products source/target reactions ##
  * Right click on named node. Menu emerges.
  * Click "Add source nodes" for generating reactants (for reaction) or source reactions (for species)
  * Click "Add target nodes" for generating products (for reaction) or target reactions (for species)

## Moving multi nodes ##
  * Select nodes you want to move (by holding left mouse button).
  * Catch one of selected nodes and move it. Other nodes also move.

# Useful hints #
  * How to create visualization:
    1. Draw transitions and places
    1. Connect them
    1. Name them
It prevents from drawing invalid visualization.(if you connect place with transition,
then you start to name them, VAApp will know how to proper name place and transition)

  * If mouse arrow doesn't move for a while tooltips emerge. They help to use VAApp.