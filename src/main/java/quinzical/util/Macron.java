package quinzical.util;

import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Class which takes a text field and adds macron capability to it, allowing
 * users to press the up arrow when the cursor is after a vowel to add a macron
 * to that vowel.
 */
public class Macron {
    private static Macron instance;
    private HashMap<Character, Character> pairs;
    private TextField fxInput;

    /**
     * Private constructor
     */
    private Macron() {
        // characters which can have macrons and their respective macron characters
        pairs = new HashMap<Character, Character>();
        pairs.put('a', 'ā');
        pairs.put('e', 'ē');
        pairs.put('i', 'ī');
        pairs.put('o', 'ō');
        pairs.put('u', 'ū');
        pairs.put('A', 'Ā');
        pairs.put('E', 'Ē');
        pairs.put('I', 'Ī');
        pairs.put('O', 'Ō');
        pairs.put('U', 'Ū');
    }

    /**
     * Get the singleton instance of the class
     * @return instance
     */
    public static Macron getInstance() {
        if (instance == null) {
            instance = new Macron();
        }
        return instance;
    }

    /**
     * Method to add macron support to a given JavaFx input field
     * 
     * @param fxInput        the input field
     * @param fxMacronLetter the element which displays the macron result
     * @param fxMacronPopup  the element which wraps fxMacronLetter and provides
     *                       visibility
     */
    public void bind(TextField fxInput, Label fxMacronLetter, VBox fxMacronPopup) {
        fxMacronPopup.setVisible(false);
        this.fxInput = fxInput;

        fxInput.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.UP) {
                    keyEvent.consume();

                    // if get the character at the cursor can have a macron, add it
                    int index = fxInput.getCaretPosition();
                    if (index != 0) {
                        Character atCursor = fxInput.getText().charAt(index - 1);

                        if (pairs.get(atCursor) != null) {
                            fxInput.replaceText(index - 1, index, Character.toString(pairs.get(atCursor)));
                            fxMacronPopup.setVisible(false);
                        }
                    }
                }
            }
        });

        // change the alignment of the macron popup to be above the cursor and display
        // it if the character at the cursor can have a macron
        fxInput.setOnKeyReleased(e -> {
            int index = fxInput.getCaretPosition();
            Character macron;
            if (index != 0 && (macron = pairs.get(fxInput.getText().charAt(index - 1))) != null) {
                positionGUI(fxMacronPopup, computeTextWidth(fxInput.getText().substring(0, index)) - 4);
                fxMacronLetter.setText(Character.toString(macron));
                fxMacronPopup.setVisible(true);
            } else {
                fxMacronPopup.setVisible(false);
            }
        });
    }

    /**
     * Get the width of JavaFX text. Method taken from:
     * https://stackoverflow.com/questions/41336447/determining-width-and-height-of-a-javafx-font/53206980
     * 
     * @param text The text to compute the width of
     * @return The width of the text
     */
    private double computeTextWidth(String text) {
        double wrappingWidth = Double.MAX_VALUE;
        Font font = fxInput.getFont();

        Text helper = new Text();
        helper.setFont(font);
        helper.setText(text);
        // Note that the wrapping width needs to be set to zero before
        // getting the text's real preferred width.
        helper.setWrappingWidth(0);
        helper.setLineSpacing(0);
        double w = Math.min(helper.prefWidth(-1), wrappingWidth);
        helper.setWrappingWidth((int) Math.ceil(w));
        double textWidth = Math.ceil(helper.getLayoutBounds().getWidth());
        return textWidth;
    }

    /**
     * Set the position of the macron display element
     * @param fxMacronPopup The element to move
     * @param location The horizontal alignment to set of the macron display
     */
    private void positionGUI(VBox fxMacronPopup, double location) {
        VBox.setMargin(fxMacronPopup, new Insets(0, 0, 0, location));
    }
}
