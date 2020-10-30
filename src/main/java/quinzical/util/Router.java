package quinzical.util;

import java.util.ArrayDeque;
import java.util.Deque;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import quinzical.App;
import quinzical.controller.View;
import quinzical.controller.menu.CategorySelectGame;

/**
 * Class to navigate from view to view in the JavaFX application. JavaFX does
 * not have built in support for changing views which has a nice interface, so
 * this class acts as an interface to view changing.
 */
public class Router {
    // the container containing the entire application
    private static BorderPane container;

    // Represents the history of the pages the user has visited
    private static Deque<View> history = new ArrayDeque<View>();

    /**
     * Navigate the user to the last page in history and remove it from the history.
     */
    public static void navigateBack() {
        if (history.peekLast() == null) {
            show(View.MAIN_MENU);
            return;
        }
        history.removeLast(); // current page

        View lastPage = history.peekLast();
        if (lastPage == null) {
            show(View.MAIN_MENU);
            return;
        }
        show(lastPage, false); // show last page without adding to history
    }

    /**
     * Sets the scene to show the specified fxml file.
     * 
     * @param fxml         the path to the fxml file, relative to App.java
     * @param addToHistory whether to add the navigation to the history
     */
    public static void show(View fxml, boolean addToHistory) {
        App.setState(fxml.getState());
        TTS.getInstance().clearQueue();
        Timer.getInstance().stop();

        FadeTransition ft = new FadeTransition(Duration.millis(300), container);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        // Place the content into the container
        container.setCenter(loadFXML(fxml.getCenter(), fxml.getController()));
        container.setTop(loadFXML(fxml.getTop()));
        container.setRight(loadFXML(fxml.getRight()));
        container.setBottom(loadFXML(fxml.getBottom()));
        container.setLeft(loadFXML(fxml.getLeft()));

        if (addToHistory) {
            history.add(fxml);
        }
    }

    /**
     * Call the show method without adding to history
     * 
     * @param fxml
     */
    public static void show(View fxml) {
        show(fxml, true);
    }

    /**
     * Check whether the current view is equal to the parameter.
     * 
     * @param view the view to compare
     * @return whether the views are equal
     */
    public static boolean currentViewIs(View view) {
        return history.peekLast() == view;
    }

    /**
     * Sets the container where content should be placed
     * 
     * @param p A borderPane which should contain the content to display
     */
    public static void setContainer(BorderPane p) {
        container = p;
    }

    /**
     * Loads the specified fxml file
     * 
     * @param fxml the path to the fxml file
     * @return a javafx node hierarchy
     */
    public static Node loadFXML(String fxml, Object controller) {
        if (fxml != null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                if (controller != null) {
                    loader.setController(controller);
                }
                loader.setLocation(App.class.getResource(fxml.toString()));
                Node node = (Node) loader.load();

                return node;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Node loadFXML(String fxml) {
        return loadFXML(fxml, null);
    }

    /**
     * Used when a reference to the controller is also required
     * 
     * @param fxml the path to the fxml file
     * @return the fxmlloader which can then be used to access the fxml as well as
     *         the controller object
     */
    public static FXMLLoader manualLoad(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource(fxml.toString()));
            return loader;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return whether the game is in practice mode
     */
    public static boolean isGameMode() {
        return history.peekLast().getController() instanceof CategorySelectGame;
    }
}
