/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.view;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.Border;

/**
 *  no border - border for non selected nodes
 * @author markos
 */
public class NoSelectBorder implements Border{

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(0,0,0,0);
    }

    public boolean isBorderOpaque() {
        return false;
    }

}
