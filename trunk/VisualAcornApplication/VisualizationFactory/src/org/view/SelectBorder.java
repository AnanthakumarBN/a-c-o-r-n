/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.Border;

/**
 *
 * @author markos
 */
public class SelectBorder implements Border{

    private int bottom;
    private int left;
    private int right;
    private int top;

    private static final int margin = 5;

    public SelectBorder(int bottom, int left, int right, int top) {
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.top = top;
    }

    public SelectBorder() {
        bottom = margin;
        left = margin;
        right = margin;
        top = margin;
    }
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.BLUE);
        g.drawRect(x, y, width, height);
        g.drawRect(x+1, y+1, width-2, height-2);
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(top,left,bottom,right);
    }

    public boolean isBorderOpaque() {
        return false;
    }

}
