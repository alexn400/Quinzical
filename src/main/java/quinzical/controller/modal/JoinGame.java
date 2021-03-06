package quinzical.controller.modal;

import java.util.ArrayList;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import quinzical.model.MultiplayerGame;
import quinzical.model.User;
import quinzical.util.Connect;
import quinzical.util.Modal;
import quinzical.util.Router;
import quinzical.controller.View;
import quinzical.model.Member;

/**
 * Controller for the dialog that allows users to join a multiplayer game
 * 
 * @author Alexander Nicholson, Peter Geodeke
 */
public class JoinGame {

    @FXML
    private TextField fxSlot1;

    @FXML
    private TextField fxSlot2;

    @FXML
    private TextField fxSlot3;

    @FXML
    private TextField fxSlot4;

    @FXML
    private TextField fxSlot5;

    @FXML
    private Label fxMessage;

    /**
     * The method to run when the fxml is initialised
     */
    public void initialize() {
        fxMessage.setText("");
        /*
         * Credit to:
         * https://stackoverflow.com/questions/15159988/javafx-2-2-textfield-maxlength
         * This ensures that no text field can ever have more than 1 character in it
         */
        UnaryOperator<Change> modifyChange = (c -> {
            if (c.isContentChange()) {
                c.setText(c.getControlNewText().toUpperCase());
                int newLength = c.getControlNewText().length();
                if (newLength > 1) {

                    String tail = c.getControlNewText().substring(newLength - 1, newLength);
                    c.setText(tail);

                    int oldLength = c.getControlText().length();
                    c.setRange(0, oldLength);

                }
                if (newLength >= 1) {
                    // move the cursor to the next input
                    String elemId = c.getControl().getId();

                    switch (elemId) {
                        case "fxSlot1":
                            fxSlot2.requestFocus();
                            break;
                        case "fxSlot2":
                            fxSlot3.requestFocus();
                            break;
                        case "fxSlot3":
                            fxSlot4.requestFocus();
                            break;
                        case "fxSlot4":
                            fxSlot5.requestFocus();
                            break;
                        default:
                            break;
                    }
                } else if (newLength == 0) {
                    // Move the cursor back one
                    String elemId = c.getControl().getId();

                    switch (elemId) {
                        case "fxSlot2":
                            fxSlot1.requestFocus();
                            break;
                        case "fxSlot3":
                            fxSlot2.requestFocus();
                            break;
                        case "fxSlot4":
                            fxSlot3.requestFocus();
                            break;
                        case "fxSlot5":
                            fxSlot4.requestFocus();
                            break;
                        default:
                            break;
                    }
                }
            }
            return c;
        });

        // Apply the formatter to all of the inputs
        fxSlot1.setTextFormatter(new TextFormatter<Change>(modifyChange));
        fxSlot2.setTextFormatter(new TextFormatter<Change>(modifyChange));
        fxSlot3.setTextFormatter(new TextFormatter<Change>(modifyChange));
        fxSlot4.setTextFormatter(new TextFormatter<Change>(modifyChange));
        fxSlot5.setTextFormatter(new TextFormatter<Change>(modifyChange));
    }

    /**
     * Method to handle when the user closes the dialog
     */
    public void handleClose() {
        Modal.hide();
    }

    /**
     * Helper method to join the inputs from each of the text fields
     * 
     * @return the 5 digit code entered by the user
     */
    private String getCode() {
        return fxSlot1.textProperty().get() + fxSlot2.textProperty().get() + fxSlot3.textProperty().get()
                + fxSlot4.textProperty().get() + fxSlot5.textProperty().get();
    }

    @FXML
    private void handleJoin() {
        String codeString = getCode();
        // check if valid
        if (codeString.length() < 5 || Pattern.matches("[a-zA-Z]+", codeString)) {
            fxMessage.setText("Invalid code");
        }
        int code = Integer.parseInt(codeString);
        Connect connect = Connect.getInstance();
        Member user = new Member(User.getInstance().getAvatar(), 0, User.getInstance().getName());

        JSONObject json = new JSONObject();
        try {
            json.put("code", code);
            json.put("user", user.toJSONObject());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        connect.emit("JOIN_LOBBY", json);
        connect.onMessage("LOBBY_JOINED", args -> {
            try {
                JSONObject obj = new JSONObject(args[0].toString());
                JSONArray membersRaw = obj.getJSONArray("members");

                ArrayList<Member> members = new ArrayList<Member>();
                for (int i = 0; i < membersRaw.length(); i++) {
                    members.add(Member.fromJSONObject(membersRaw.getString(i)));
                }

                MultiplayerGame.startGame(code, user);
                MultiplayerGame.getInstance().updateMembers(members);
                Router.show(View.LOBBY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        connect.onMessage("INVALID_LOBBY", args -> {
            fxMessage.setText("Invalid Code");
        });
    }
}
