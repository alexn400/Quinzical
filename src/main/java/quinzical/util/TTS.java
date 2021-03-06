package quinzical.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayDeque;

import javafx.concurrent.Task;

/**
 * TTS module to allow for the easy and non-overlapping playing of text to
 * speech text
 */
public class TTS implements Serializable {

    private static final long serialVersionUID = -7165475779564908787L;

    // Transient as we do not want the queue to be persisted between sessions
    private transient ArrayDeque<ProcessBuilder> processQueue;

    // singleton code
    private static TTS tts;
    private int volume;
    private int speed;
    private transient boolean speaking = false;

    /**
     * Private constructor
     */
    private TTS() {
        processQueue = new ArrayDeque<ProcessBuilder>();
        volume = 100;
        speed = 160;
    }

    /**
     * Get the singleton instance of the class
     * @return instance
     */
    public static TTS getInstance() {
        if (tts == null) {
            // Attempt to read state from file
            tts = IOManager.readState(State.TTS);
            if (tts == null) {
                tts = new TTS();
                persist();
            } else {
                tts.init();
            }
        }
        return tts;
    }

    /**
     * Internal method used by the public speak function to handle the actual
     * speaking of messages. The message which is spoken is the first in the queue.
     * Speaking of messages runs in a separate thread. When a message has finished
     * being spoken the message queue is checked to see whether there is another
     * message which must be spoken.
     * 
     * Does not speak if there are no messages in the queue. Messages must be added
     * to the queue with the speak method.
     */
    private void speakNext() {
        if (speaking || processQueue.peek() == null) {
            return;
        }

        speaking = true;

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                var builder = processQueue.poll();

                try {
                    Process p = builder.start();
                    try {
                        p.waitFor();
                        speaking = false;
                        speakNext();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * Add a message to the queue of messages to be spoken by espeak. If this is the
     * first message within the queue, then the message will be immediately spoken.
     * If it is not the first message, then the message will be spoken once the
     * messages earlier in the queue have been spoken.
     * 
     * Calling this function does not guarantee that the message will be spoken;
     * clearing the queue may remove the message before it is spoken.
     * 
     * @param text
     */
    public void speak(String text) {
        ProcessBuilder builder = new ProcessBuilder("espeak", "-a", Integer.toString(volume), "-s",
                Integer.toString(speed), text);
        processQueue.add(builder);
        if (!speaking) {
            speakNext();
        }
    }

    /**
     * Method to cancel all current TTS messages which were previously requested to
     * be voiced, but have not yet completed.
     */
    public void clearQueue() {
        processQueue.clear();
    }

    /**
     * Set the volume of the TTS
     * @param volume
     */
    public void setVolume(int volume) {
        this.volume = volume;
        persist();
    }

    /**
     * 
     * @return the volume
     */
    public int getVolume() {
        return volume;
    }

    /**
     * Set the speed of the TTS
     * @param speed
     */
    public void setSpeed(int speed) {
        if (speed < 80) {
            this.speed = 80;
        } else if (speed > 240) {
            this.speed = 240;
        } else {
            this.speed = speed;
        }
        persist();
    }

    /**
     * 
     * @return the speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Saves the current settings in state
     */
    private static void persist() {
        if (tts == null) {
            IOManager.clearState(State.TTS);
        } else {
            IOManager.writeState(State.TTS, tts);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    };

    /**
     * Init the TTS object
     */
    private void init() {
        processQueue = new ArrayDeque<ProcessBuilder>();
        speaking = false;
    }

}