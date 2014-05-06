/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Core;

import javax.swing.Icon;

/**
 *
 * @author Rostunov_Sergey
 */
public class TextAndIcon {
    private int guid;
    private String username;
    private String title;
    private String psm;
    private int status;
    private Icon icon;

    public TextAndIcon(String username, Icon icon) {
        this.username = username;
        this.icon = icon;
    }

    public String getText() {
        return username;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setText(String username) {
        this.username = username;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }
}
