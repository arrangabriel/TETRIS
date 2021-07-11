package project;

import javafx.animation.AnimationTimer;

/**
 * The game loop class handles timing of 'game ticks',
 * as well as calls to the game object.
 */
public class GameLoop extends AnimationTimer {
    private final Game game;
    private long frameLength;
    private long lastUpdate = 0;

    /**
     * @param game: The game to be called upon.
     */
    public GameLoop(Game game){
        this.game = game;
        this.frameLength = Game.FRAME_LENGTH;
    }

    @Override
    public void handle(long now) {
        if(now - this.lastUpdate >= frameLength){
            this.game.tick();
            this.lastUpdate = now;
            this.frameLength--;
        }
    }
}