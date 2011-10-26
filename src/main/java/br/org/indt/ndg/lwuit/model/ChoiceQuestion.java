/*
*  Nokia Data Gathering
*
*  Copyright (C) 2011 Nokia Corporation
*
*  This program is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public
*  License as published by the Free Software Foundation; either
*  version 2.1 of the License, or (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public License
*  along with this program.  If not, see <http://www.gnu.org/licenses/
*/

package br.org.indt.ndg.lwuit.model;

import java.util.Vector;

/**
 *
 * @author mluz
 */
public class ChoiceQuestion extends NDGQuestion {

    private Vector choices = new Vector();
    private Vector others = new Vector();
    private Vector othersText = new Vector();
    private Vector defaultAnswers = new Vector();

    private boolean exclusive;
    private boolean inverse;

    private int choiceItem;
    private int catTo;
    private int skipTo;
    private boolean skipEnabled;

    //get and set skip logic variable methods
    public void setSkipEnabled(boolean _bool) { skipEnabled = _bool; }
    public boolean getSkipEnabled() { return skipEnabled; }
    
    public void setCatTo(int _val) { catTo = _val; }
    public int getCatTo() {return catTo;}

    public void setSkipTo(int _val) { skipTo = _val; }
    public int getSkipTo() {return skipTo;}
    
    public void setChoiceItem(int _val) { choiceItem = _val; }
    public int getChoiceItem() {return choiceItem; }
    
    public void setInverse(boolean _val) { this.inverse = _val; }
    public boolean isInverse() { return inverse; }

    public void setDefaultAnswers(Vector _def){defaultAnswers = _def; }
    public Vector getDefaultAnswers(){return defaultAnswers; }

    /////////////////////////////////////////

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public Vector getChoices() {
        return choices;
    }

    public void addChoice(String choice) {
        this.choices.addElement(choice);
    }

    public void setChoices(Vector choices) {
        this.choices = choices;
    }

    public Vector getOthers() {
        return others;
    }

    public void addOther( String other ) {
        this.others.addElement(other);
    }

    public void setOthers(Vector others) {
        this.others = others;
    }

    public String getChoiceText(int index) {
        if(index<choices.size()){
            return (String)choices.elementAt(index);
        }
        return "";
    }

    public void addOthersText(String othersText) {
        this.othersText.addElement(othersText);
    }

    public void setOthersText(Vector othersText) {
        this.othersText = othersText;
    }

    public Vector getOthersText() {
        return othersText;
    }

    public NDGAnswer getAnswerModel() {
        ChoiceAnswer anwser = new ChoiceAnswer( );
        Vector vDefault = getDefaultAnswers();
        anwser.setSelectedIndex( vDefault );
        return anwser;
    }
}
