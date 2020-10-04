package quinzical.components;

import javafx.scene.control.Label;
import quinzical.controller.Views;
import quinzical.model.PracticeGame;
import quinzical.util.Router;
import javafx.fxml.FXML;

public class CategoryListItemController {

    private String category;

    @FXML
    private Label name;

    @FXML
    private Label count;

    public void config(String categoryName, int questionCount) {
        category = categoryName;
        name.textProperty().set(categoryName);
        count.textProperty().set(Integer.toString(questionCount));
    }

    @FXML
    public void handleButtonPress() {
        PracticeGame.getInstance().setCurrentCategory(category);
        Router.show(Views.PRACTICE_ANSWER_SCREEN);
    }
}
