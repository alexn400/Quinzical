package quinzical.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import quinzical.model.Category;
import quinzical.model.Question;
import quinzical.model.User;

/**
 * Helper class which serves as a link between the IOManager reading questions
 * from file and the controller class that wants to access specific questions
 * This class provides lists of questions and handles the 'random selection' of
 * questions
 * 
 * @author Alexander Nicholson, Peter Geodeke
 */
public class QuestionBank {

    private HashMap<String, Category> questionBank;
    private HashMap<String, Category> userQuestionBank;
    private Random _rand = new Random();

    private static QuestionBank instance;

    /**
     * @return the instance of this class, if no instance is found a new instance is
     *         created
     */
    public static QuestionBank getInstance() {
        if (instance == null) {
            instance = new QuestionBank();
        }
        return instance;
    }

    // Private constructor
    private QuestionBank() {
        questionBank = IOManager.getBaseQuestions();
        userQuestionBank = IOManager.getUserQuestions();
    }

    /**
     * Gets a given amount of random questions from the specified category
     * 
     * @param categoryName a name of a category - case sensitive. If the category
     *                     doesnt exist it will throw an error
     * @param amount       the number of questions to return.
     * @return a list of questions
     */
    public ArrayList<Question> getRandomQuestions(Category category, int amount, boolean allowDuplicates)
            throws IllegalArgumentException {
        // Get arraylist of questions

        // Check if category exists
        // Get the list of questions and create a copy of the array so the original is
        // not modified
        ArrayList<Question> questionList = new ArrayList<Question>(category.getQuestions());

        ArrayList<Question> results = new ArrayList<Question>();

        for (int i = 0; i < amount; i++) {
            int difficulty = i + 1;
            // Get all questions with difficulty i
            if (!allowDuplicates) {
                List<Question> filteredQs = questionList.stream().filter(p -> p.getDifficulty() == difficulty)
                        .collect(Collectors.toList());

                int index = _rand.nextInt(filteredQs.size());
                // Get a random question and remove it from the list

                results.add(new Question(filteredQs.get(index)));
            } else {

                int index = _rand.nextInt(questionList.size());
                // Get a random question and remove it from the list

                results.add(new Question(questionList.get(index)));
            }
        }

        return results;
    }

    /**
     * Gets a given amount of random questions from the specified category
     * 
     * @param categoryName    a name of a category - case sensitive. If the category
     *                        doesnt exist the method will throw an error
     * @param amount          the number of questions to return.
     * @param allowDuplicates specify if questions can be repeated or if all
     *                        questions must be unique
     * @return a list of random categories
     */
    public List<Category> getRandomCategories(int amount, boolean allowDuplicates) throws IllegalArgumentException {
        // Get arraylist of categories

        // Get the list of categories and create a copy of the array so the original is
        // not modified
        List<Category> categoryList = new ArrayList<Category>(getCategories());

        // Throw an error if there are not enough unique questions to match the amount
        // specified
        if (categoryList.size() < amount && !allowDuplicates) {
            throw new IllegalArgumentException(categoryList + " only has " + categoryList.size() + " questions but "
                    + amount + " was requested. Either add more questions to this category or allow duplicates");
        }

        // If international is locked then remove it from options
        if (!User.getInstance().getInternationalUnlocked()) {
            categoryList = categoryList.stream().filter(p -> !p.getName().equals("International"))
                    .collect(Collectors.toList());
        }

        ArrayList<Category> results = new ArrayList<Category>();

        for (int i = 0; i < amount; i++) {
            int index = _rand.nextInt(categoryList.size());
            // Get a random question and remove it from the list

            if (allowDuplicates) {
                results.add(categoryList.get(index));
            } else {
                // Remove question from list so we dont get duplicates
                results.add(categoryList.remove(index));
            }
        }

        return results;
    }

    /**
     * 
     * @param amount           the amount of categories to select
     * @param allowDuplicates  if categories should be allowed to be selected more
     *                         than once
     * @param minimumQuestions the minimum number of questions a category must have
     *                         to be selectable
     * @return a list of random categories
     * @throws IllegalArgumentException if the request could not be satisfied (there
     *                                  were not enough categories)
     */
    public ArrayList<Category> getRandomCategories(int amount, boolean allowDuplicates, int minimumQuestions)
            throws IllegalArgumentException {
        // Get arraylist of categories

        // Get the list of categories and create a copy of the array so the original is
        // not modified
        List<Category> categoryList = getCategories().stream().filter(p -> p.getQuestions().size() >= minimumQuestions)
                .collect(Collectors.toList());

        // Throw an error if there are not enough unique questions to match the amount
        // specified
        if (categoryList.size() < amount && !allowDuplicates) {
            throw new IllegalArgumentException(categoryList + " only has " + categoryList.size() + " questions but "
                    + amount + " was requested. Either add more questions to this category or allow duplicates");
        }

        ArrayList<Category> results = new ArrayList<Category>();

        for (int i = 0; i < amount; i++) {
            int index = _rand.nextInt(categoryList.size());
            // Get a random question and remove it from the list

            if (allowDuplicates) {
                results.add(categoryList.get(index));
            } else {
                // Remove question from list so we dont get duplicates
                results.add(categoryList.remove(index));
            }
        }

        return results;
    }

    /**
     * @return a list of all categories
     */
    public ArrayList<Category> getCategories() {

        ArrayList<Category> output = new ArrayList<Category>();

        output.addAll(userQuestionBank.values().stream().collect(Collectors.toList()));

        output.addAll(questionBank.values().stream().collect(Collectors.toList()));

        return output;

    }

    /**
     * @return a list of all categories the user has created
     */
    public ArrayList<Category> getUserCategories() {
        return (ArrayList<Category>) userQuestionBank.values().stream().collect(Collectors.toList());
    }

    /**
     * Sets the list of user categories to the specified hashmap
     * 
     * @param categories the hashmap of categories that the user has created
     */
    public void setUserCategories(HashMap<String, Category> categories) {
        userQuestionBank = categories;

        IOManager.saveUserQuestions(categories);
    }
}
