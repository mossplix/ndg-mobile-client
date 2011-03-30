/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.org.indt.ndg.lwuit.ui;

import br.org.indt.ndg.lwuit.ui.style.NDGStyleToolbox;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.plaf.UIManager;

/**
 *
 * @author mluz
 */
public class WaitingScreen {

    private static WaitingScreen instance;
    private Dialog dialog;

    public static void show(String status) {
        UIManager.getInstance().getLookAndFeel().setDefaultDialogTransitionIn(null);
        UIManager.getInstance().getLookAndFeel().setDefaultDialogTransitionOut(null);
        instance = new WaitingScreen(status);
        instance.showModeless();
    }

    public static boolean isShowed() {
        if (instance != null)
            return instance.dialog.isVisible();
        else
            return false;
    }

    public static void dispose() {
        instance.dialog.dispose();
        UIManager.getInstance().getLookAndFeel().setDefaultDialogTransitionIn(CommonTransitions.createSlide(CommonTransitions.SLIDE_VERTICAL, false, 500));
        UIManager.getInstance().getLookAndFeel().setDefaultDialogTransitionOut(CommonTransitions.createSlide(CommonTransitions.SLIDE_VERTICAL, true, 500));
    }

    private void showModeless() {
        Container cont1 = dialog.getContentPane();
        int hi = 0;
        int wi = cont1.getPreferredW();

        for( int i = 0 ; i< dialog.getComponentCount() ; i ++ ){
            hi += dialog.getComponentAt(i).getPreferredH();
        }

        int disH = Display.getInstance().getDisplayHeight();
        int disW = Display.getInstance().getDisplayWidth();

        dialog.show( (disH - hi)/2, (disH - hi)/2 , (disW - wi)/2, (disW - wi)/2, false, false );
        //dialog.showPacked( BorderLayout.CENTER, false);
    }


    private WaitingScreen(String status) {
        dialog = new Dialog();
        dialog.setDialogStyle( NDGStyleToolbox.getInstance().menuStyle.getBaseStyle() );
        dialog.getDialogStyle().setBgColor( NDGStyleToolbox.getInstance().menuStyle.bgUnselectedColor );
        dialog.setLayout(new BorderLayout());
        Image image = Screen.getRes().getImage("wait");

        Label label = new Label(image);
        label.setAlignment(Component.CENTER);

        TextArea msg = new TextArea( status );
        msg.setSingleLineTextArea(true);
        msg.setAlignment( Component.CENTER );
        msg.getSelectedStyle().setFgColor( NDGStyleToolbox.getInstance().menuStyle.unselectedFontColor );
        msg.getSelectedStyle().setBgTransparency(0x00);
        msg.setEditable(false);
        msg.setPreferredW(msg.getSelectedStyle().getFont().stringWidth( status ) + 5);
        msg.setIsScrollVisible(false);

        dialog.addComponent(BorderLayout.NORTH ,label);
        dialog.addComponent(BorderLayout.CENTER, msg);
    }
}