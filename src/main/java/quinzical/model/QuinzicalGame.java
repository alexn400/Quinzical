package quinzical.model;

import quinzical.util.QuestionBank;

/**
 * A parent class representing both the practice and game modes. This contains
 * all of the logic that is common to both the practice and game modes
 */

public abstract class QuinzicalGame {
    /**
     * Enum for the status of the game
     */
    public enum Status {
        GAME_BOARD, 
        ANSWERING, 
        SUCCESS,
        OUT_OF_TIME, 
        SKIP, 
        FAILURE, 
        REWARD
    }

    private Status status;

    /**
     * Set the status of the game
     * 
     * @param s
     */
    public void setStatus(Status s) {
        status = s;
    }

    /**
     * 
     * @return the status of the game
     */
    public Status getStatus() {
        return status;
    }

    private Question currentQuestion;
    protected QuestionBank questionBank = QuestionBank.getInstance();

    // Returns the current question for the game
    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    /**
     * Sets the current question for the game. This is not called directly by the
     * controller, but called by the child classes
     * 
     * @see setCurrentQuestion in the Game model
     * @param question
     */
    protected void setCurrentQuestion(Question question) {
        currentQuestion = question;
    }
}
