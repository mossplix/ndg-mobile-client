package br.org.indt.ndg.lwuit.ui.openrosa;

import br.org.indt.ndg.lwuit.extended.CheckBox;
import br.org.indt.ndg.lwuit.extended.DateField;
import br.org.indt.ndg.lwuit.extended.DescriptiveField;
import br.org.indt.ndg.lwuit.extended.NumericField;
import br.org.indt.ndg.lwuit.extended.RadioButton;
import br.org.indt.ndg.lwuit.ui.GeneralAlert;
import br.org.indt.ndg.lwuit.ui.style.NDGStyleToolbox;
import br.org.indt.ndg.mobile.Resources;
import com.nokia.xfolite.xforms.dom.BoundElement;
import com.nokia.xfolite.xforms.dom.XFormsElement;
import com.nokia.xfolite.xforms.model.datatypes.DataTypeBase;
import com.nokia.xfolite.xml.dom.Element;
import com.nokia.xfolite.xml.dom.Node;
import com.nokia.xfolite.xml.dom.WidgetFactory;
import com.nokia.xfolite.xml.xpath.NodeSet;
import com.nokia.xfolite.xml.xpath.XPathNSResolver;
import com.sun.lwuit.ButtonGroup;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.FocusListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;
import java.util.Hashtable;
import java.util.Vector;




// ********************      helper classes     ******************************8
/**
 *
 * @author damian.janicki
 */
public class OpenRosaWidgetFactory implements WidgetFactory, XPathNSResolver {

    private Container rootContainer = null;
    private static OpenRosaResourceManager resourceManager = new OpenRosaResourceManager();
    private Vector createdContainers = new Vector();

    public OpenRosaWidgetFactory(Container cont) {
        rootContainer = cont;
        rootContainer.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        resourceManager.clear();
    }

    public static OpenRosaResourceManager getResoruceManager(){
        return resourceManager;
    }
    public boolean commitValues() {
        boolean result = true;
        for (int i = 0; i < createdContainers.size(); i++) {
            if (!((ContainerUI) createdContainers.elementAt(i)).validate()) {
                result = false;
                ((ContainerUI) createdContainers.elementAt(i)).showBadInputError();
                break;
            }
        }
        if(result == true) {
            for (int i = 0; i < createdContainers.size(); i++) {
                ((ContainerUI) createdContainers.elementAt(i)).commitValue();
            }
        }
        return result;
    }

    public void elementParsed(Element el) {
    }

    public void childrenParsed(Element el) {
        String tagName = el.getLocalName();
        BoundElement binding = null;
        if (el instanceof BoundElement) {
            binding = (BoundElement) el;
        }
        Component comp = null;

        if ("input" == tagName || "secret" == tagName) {
            comp = addInput(binding);
        } else if (tagName == "select") {
            comp = addSelect(binding);
        } else if ("range" == tagName) {
            addInput(binding);
        } else if ("output" == tagName) {
        } else if ("trigger" == tagName) {
        } else if ("switch" == tagName) {
        } else if ("upload" == tagName) {
            String mediatype = el.getAttribute("mediatype");
            if(mediatype!=null && mediatype.indexOf("image") > -1) {
                //addPhotoUI(binding, cont);
            }
        } else if ("submit" == tagName) {
        } else if ("select1" == tagName) {
            comp = addSelect1(binding);
        } else if ("img" == tagName) {
        } else if ("hr" == tagName) {
        } else if ("p" == tagName) {
        } else if ("table" == tagName) {
        } else if ("td" == tagName || "th" == tagName) {
        } else if (tagName == "value") {
            addTextValue(el);
        } else {
        }
        if(comp != null) {
            rootContainer.addComponent(comp);
            createdContainers.addElement(comp);
        }
    }

    public void addTextValue(Element el){
        Element parent = (Element)el.getParentNode();
        if(parent.getNodeName() != "text"){
            return;
        }

        String id = parent.getAttribute("id");
        String value = el.getText();

        resourceManager.put(id, value);
    }

    public void removingElement(Element el) {
    }

    public void elementInitialized(Element el) {
    }

    public void childrenInitialized(Element el) {
    }

    public String lookupNamespaceURI(String prefix) {
        return "";
    }

    private Component addInput(BoundElement bindElem) {
        DataTypeBase a = bindElem.getDataType();
        Component question = null;

        if (a != null) {
            switch (a.getBaseTypeID()) {
                case DataTypeBase.XML_SCHEMAS_DATE:
                    question = new XfoilDateFieldUI(bindElem);
                    break;
//                case DataTypeBase.XML_SCHEMAS_DATETIME:
//                    break;
                case DataTypeBase.XML_SCHEMAS_TIME:
                    //question = new XfoilTimeFieldUI(bindElem);
                    break;
                case DataTypeBase.XML_SCHEMAS_STRING:
//                case DataTypeBase.XML_SCHEMAS_ANYURI:
                    question = new XfoilDescriptiveFieldUI(bindElem);
                    break;
                case DataTypeBase.XML_SCHEMAS_DECIMAL:
                case DataTypeBase.XML_SCHEMAS_INTEGER:
                    question = new XfoilNumericFieldUI(bindElem);
                    break;
                default:
                case DataTypeBase.XML_SCHEMAS_UNKNOWN:
                    question = new XfoilMockComponent(bindElem);
            }
        }
       return question;
    }

    private Component addSelect(BoundElement bindElem) {
        Component question = new XfoilMultipleChoiceFieldUI(bindElem);
        return question;

    }

    private Component addSelect1(BoundElement bindElem) {
        Component question = new XfoilExclusiveChoiceFieldUI(bindElem);
        return question;
    }

}


abstract class ContainerUI extends Container implements FocusListener {

    protected TextArea qname;
    protected BoundElement element;

    protected void commitValue(String input) {
            element.setStringValue(input);
    }

    public abstract void commitValue();

    public abstract void setEnabled(boolean enabled);

    public void handleMoreDetails(Object cmd) {
    }

    public ContainerUI(BoundElement element) {
        getStyle().setBorder(Border.createBevelLowered(NDGStyleToolbox.getInstance().focusLostColor,
                NDGStyleToolbox.getInstance().focusLostColor,
                NDGStyleToolbox.getInstance().focusLostColor,
                NDGStyleToolbox.getInstance().focusLostColor));
        this.element = element;
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        addFocusListener(this);
    }

    protected void addQuestionName() {

        addComponent(new Label(""));
        XFormsElement labelEl = (XFormsElement) element.getUserData(XFormsElement.LABEL_KEY);
        TextArea questionName = new TextArea();
        questionName.setEditable(false);
        questionName.setFocusable(false);
        questionName.setRows(1);
        questionName.setGrowByContent(true);
//        questionName.setText(labelEl.getText());

        questionName.setText(OpenRosaWidgetFactory.getResoruceManager().tryGetLabelForElement(element));

        int pw = Display.getInstance().getDisplayWidth();
        int w = questionName.getStyle().getFont().stringWidth(labelEl.getText());
        if (w > pw) {
            questionName.setGrowByContent(true);
            questionName.setRows(2);
        } else {
            questionName.setGrowByContent(false);
            questionName.setRows(1);
        }
        this.addComponent(questionName);
    }

    protected abstract boolean validate();

    public BoundElement getElement() {
        return element;
    }

    public void showBadInputError() {
        String constraint = element.getConstraintString();
        GeneralAlert.getInstance().addCommand(GeneralAlert.DIALOG_OK, true);
        if (constraint != null) {
            GeneralAlert.getInstance().show(
                    Resources.CMD_SAVE,
                    "Valid input " + "from: " + OpenRosaConstraintHelper.getInstance().getLowConstraint(constraint) +
                    " to: " + OpenRosaConstraintHelper.getInstance().getHighConstraint(constraint),
                    GeneralAlert.WARNING); // TODO localization
        } else {
            GeneralAlert.getInstance().show(
                    Resources.CMD_SAVE,
                    "Invalid input ",
                    GeneralAlert.WARNING); // TODO localization
        }
    }

    public void focusGained(Component cmpnt) {
        getStyle().setBorder(Border.createBevelLowered(NDGStyleToolbox.getInstance().focusGainColor,
                NDGStyleToolbox.getInstance().focusGainColor,
                NDGStyleToolbox.getInstance().focusGainColor,
                NDGStyleToolbox.getInstance().focusGainColor), false);
        refreshTheme();
    }

    public void focusLost(Component cmpnt) {
        if (!validate()) {
            showBadInputError();
            cmpnt.requestFocus();

            return;
        }
        getStyle().setBorder(Border.createBevelLowered(NDGStyleToolbox.getInstance().focusLostColor,
                NDGStyleToolbox.getInstance().focusLostColor,
                NDGStyleToolbox.getInstance().focusLostColor,
                NDGStyleToolbox.getInstance().focusLostColor), false);
        refreshTheme();
    }

    protected String getValue(XFormsElement el) {
        XFormsElement valueEl = (XFormsElement) el.getUserData(XFormsElement.VALUE_KEY);
        if (valueEl != null) {
            return valueEl.getText();
        } else {
            return "";
        }
    }
}

class XfoilDescriptiveFieldUI extends ContainerUI {

    DescriptiveField tfDesc = null;

    public XfoilDescriptiveFieldUI(BoundElement element) {
        super(element);
        addQuestionName();
        addDescriptionQuestion(element);
    }

    public void commitValue() {
        commitValue(tfDesc.getText());
    }

    protected boolean validate() {
        return OpenRosaConstraintHelper.getInstance().
                validateConstraint(tfDesc.getText(), element);
    }

    public void setEnabled(boolean enabled) {
        //     throw new UnsupportedOperationException("Not supported yet.");
    }

    private void addDescriptionQuestion(BoundElement bindElem) {
        String strValue = bindElem.getStringValue().trim();

        tfDesc = new DescriptiveField(OpenRosaConstraintHelper.getInstance().
                getMaxStringLength(element));
        tfDesc.setInputMode("Abc");
        tfDesc.setEditable(true);
        tfDesc.setFocusable(true);
        if (strValue != null) {
            tfDesc.setText(strValue);
        }
        this.addComponent(tfDesc);
    }
}

class XfoilNumericFieldUI extends ContainerUI {

    NumericField nfNumber = null;

    public XfoilNumericFieldUI(BoundElement element) {
        super(element);
        addQuestionName();
        addNumericQuestion(element);
    }

    public void commitValue() {
        commitValue(nfNumber.getText());
    }

    protected boolean validate() {
        return OpenRosaConstraintHelper.getInstance().
                validateConstraint(nfNumber.getText(), element);
    }

    public void setEnabled(boolean enabled) {
     //   throw new UnsupportedOperationException("Not supported yet.");
    }

    private void addNumericQuestion(BoundElement bindElem) {
        String value = bindElem.getStringValue().trim();
        nfNumber = new NumericField(50, true);  // decimals allowed?
        nfNumber.setFocusable(true);
        if (value != null) {
            nfNumber.setText(value);
        }
        nfNumber.addFocusListener(this);
        this.addComponent(nfNumber);
    }
}

class XfoilDateFieldUI extends ContainerUI {
    DateField dfDate;

    public XfoilDateFieldUI(BoundElement element) {
        super(element);
        addQuestionName();
        addDateQuestion(element);
    }

    public void commitValue() {
        commitValue(dfDate.getText());
    }

    protected boolean validate() {
        return OpenRosaConstraintHelper.getInstance().
                validateConstraint(dfDate.getText(), element);
    }

    public void setEnabled(boolean enabled) {
      //  throw new UnsupportedOperationException("Not supported yet.");
    }

    private void addDateQuestion(BoundElement bindElem) {
        String value = bindElem.getStringValue().trim();
        if (value != null && value != "") {
            dfDate = new DateField(value, DateField.MMDDYYYY, '/');
        } else {
            dfDate = new DateField(DateField.MMDDYYYY);
        }
        dfDate.setEditable(true);
        dfDate.addFocusListener(this);
        addComponent(dfDate);
    }
}

//class XfoilTimeFieldUI extends ContainerUI {
//    TimeField dfTime;
//
//    public XfoilTimeFieldUI(BoundElement element) {
//        super(element);
//        addQuestionName();
//        addTimeQuestion(element);
//    }
//
//    public void commitValue() {
//         element.setStringValue(dfTime.getText());
//    }
//
//    protected boolean validate() {
//        return true;
//    }
//
//    public void setEnabled(boolean enabled) {
//     //   throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    private void addTimeQuestion(BoundElement bindElem) {
//        String value = bindElem.getStringValue().trim();
//        if (value != null && (value == null ? "" != null : !value.equals(""))) {
//            //dfTime = new TimeField(value, TimeField.HHMM, ':');
//
//        } else {
//            dfTime = new TimeField(TimeField.HHMM);
//        }
//        dfTime.setEditable(true);
//        addComponent(dfTime);
//    }
//}

class XfoilMultipleChoiceFieldUI extends ContainerUI {

    private Vector cbs = new Vector();

    public XfoilMultipleChoiceFieldUI(BoundElement element) {
        super(element);
        addQuestionName();
        addSelectQuestion(element);
    }

    public void commitValue() {
        String values = "";
        for (int i = 0; i < cbs.size(); i++) {
            CheckBox cb = (CheckBox)cbs.elementAt(i);
            if( cb.isSelected() ) {
                values = values.concat(cb.getText() + " ");
            }
        }
        element.setStringValue(values);
    }

    protected boolean validate() {
        // maybe required atttr
        return true;
    }

    public void setEnabled(boolean enabled) {
        //   throw new UnsupportedOperationException("Not supported yet.");
    }

    private void addSelectQuestion(BoundElement bindElem) {
        NodeSet choices = new NodeSet();

        int count = bindElem.getChildCount();
        for (int idx = 0; idx < count; idx++) {
            Node nodeItem = bindElem.getChild(idx);
            if (nodeItem.getLocalName() != null
                    && nodeItem.getLocalName().compareTo("item") == 0) {
                choices.AddNode(nodeItem);
            }
        }

        int length = choices.getLength();
        String[] names = new String[length];
        String[] values = new String[length];
        boolean[] selected = new boolean[length];


        String chosenVal = " " + bindElem.getStringValue() + " ";
        for (int i = 0; i < length; i++) {
            XFormsElement n = (XFormsElement) choices.item(i);
            String label = OpenRosaWidgetFactory.getResoruceManager().tryGetLabelForElement(n);
            String value = OpenRosaWidgetFactory.getResoruceManager().tryGetLabelForElement(n);

            if (!value.equals("") && chosenVal.indexOf(" " + value + " ") >= 0) {
                selected[i] = true;
            } else {
                selected[i] = false;
            }
            names[i] = label;
            values[i] = value;
        }

        for (int i = 0; i < length; i++) {
            CheckBox cb = new CheckBox(names[i]);
            cb.setSelected(selected[i]);
            cbs.addElement(cb);
            addComponent(cb);
        }
    }
}

class XfoilExclusiveChoiceFieldUI extends ContainerUI {

    private ButtonGroup groupButton;

    public XfoilExclusiveChoiceFieldUI(BoundElement element) {
        super(element);
        addQuestionName();
        addSelect1Question(element);
    }

    public void commitValue() {
        for (int i = 0; i < groupButton.getButtonCount(); i++) {
            RadioButton rb = (RadioButton) groupButton.getRadioButton(i);
            if (rb.isSelected()) {
                element.setStringValue(rb.getText());
                break;
            }
        }
    }

    protected boolean validate() {
        // maybe required atttr
        return true;
    }

    public void setEnabled(boolean enabled) {
      //  throw new UnsupportedOperationException("Not supported yet.");
    }

    private void addSelect1Question(BoundElement bindElem) {
        NodeSet choices = new NodeSet();

        int count = bindElem.getChildCount();
        for (int idx = 0; idx < count; idx++) {
            Node nodeItem = bindElem.getChild(idx);
            if (nodeItem.getLocalName() != null
                    && nodeItem.getLocalName().compareTo("item") == 0) {
                choices.AddNode(nodeItem);
            }
        }

        int length = choices.getLength();
        String[] names = new String[length];
        String[] values = new String[length];
        boolean[] selected = new boolean[length];

        String chosenVal = " " + bindElem.getStringValue() + " ";
        for (int i = 0; i < length; i++) {
            XFormsElement n = (XFormsElement) choices.item(i);
            String label = OpenRosaWidgetFactory.getResoruceManager().tryGetLabelForElement(n);
            String value = getValue(n);

            if (!value.equals("") && chosenVal.indexOf(" " + value + " ") >= 0) {
                selected[i] = true;
            } else {
                selected[i] = false;
            }
            names[i] = label;
            values[i] = value;
        }
        groupButton = new ButtonGroup();

        int totalChoices = names.length;
        String[] choicesStrings = new String[totalChoices];
        for (int i = 0; i < totalChoices; i++) {
            choicesStrings[i] = (String) names[i];
            RadioButton rb = new RadioButton(choicesStrings[i]);
            rb.setOther(values[i].equals("1"));
            rb.setOtherText(""); // Initializes with empty string
            rb.setSelected(selected[i]);
            //rb.addActionListener(new HandleMoreDetails()); // More Details
            //rb.addFocusListener(this); // Controls when changing to a new question
            groupButton.add(rb);
            addComponent(rb);
        }
    }
}

class XfoilMockComponent extends ContainerUI {

    public XfoilMockComponent(BoundElement element) {
        super(element);
        addQuestionName();
        addMockLabel();
    }

    public void commitValue() {
        // mock
    }

    protected boolean validate() {
        return true;
    }

    public void setEnabled(boolean enabled) {
     //   throw new UnsupportedOperationException("Not supported yet.");
    }

    private void addMockLabel() {
        addComponent(new Label("Unsupported type"));
    }
}