package quinzical.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import quinzical.model.Game;
import quinzical.model.Question;
import quinzical.model.QuinzicalGame.Status;
import quinzical.util.Router;
import quinzical.util.TTS;

public class AnswerScreen extends BaseAnswerScreen {
    @FXML
    private Label fxCategoryName;
    @FXML
    private Label fxValueText;

    private Game game;

    @Override
    void onLoad() {
        game = Game.getInstance();
        game.setStatus(Status.ANSWERING);
        fxCategoryName.setText(game.getCurrentCategory());
        fxValueText.setText(Integer.toString(game.getCurrentQuestion().getValue()));
    }

    @Override
    Question setQuestion() {
        return game.getCurrentQuestion();
    }

    @Override
    void onCorrectAnswer(Question question) {
        TTS.getInstance().speak("That is correct");
        game.addScore(question.getValue());
        game.setStatus(Status.SUCCESS);
        showAlert("Congratulations", "Correct", "Your current score is: " + game.getScore().intValue(), onFinished);
    }

    @Override
    void onWrongAnswer(Question question) {
        TTS.getInstance().speak("That is incorrect");
        TTS.getInstance().speak("The correct answer was " + question.getAnswer());

        showAlert("Oops", "Answer was: " + question.getAnswer(), "Your current score is: " + game.getScore().intValue(),
                onFinished);
    }

    @Override
    void forceWrongAnswer(Question question, boolean wasTimerExpire) {
        TTS.getInstance().speak("The correct answer was " + question.getAnswer());
        if(wasTimerExpire){
            game.setStatus(Status.OUT_OF_TIME);
        } else {
            game.setStatus(Status.SKIP);
        }
        showAlert("Oops", "Answer was: " + question.getAnswer(), "Your current score is: " + game.getScore().intValue(),
                onFinished);
    }

    private EventHandler<DialogEvent> onFinished = new EventHandler<DialogEvent>() {
        @Override
        public void handle(DialogEvent event) {
            // Navigate to the 'reward screen' only if all questions are answered
            if (game.getRemainingQuestions() == 0) {
                Router.show(View.REWARD_SCREEN);
                game.setStatus(Status.REWARD);
            } else {
                Router.show(View.GAME_BOARD);
                game.setStatus(Status.GAME_BOARD);
            }
        }
    };
}
