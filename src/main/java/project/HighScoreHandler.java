package project;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The high score handler class uses local storage to keep track of a list of high scores.
 */
public class HighScoreHandler implements FileIO{
    private File file = null;

    /**
     * Uses the passed filename to create project relative path.
     * @param filename: File relative to resource folder.
     */
    public HighScoreHandler(String filename){
        try {
            this.file = new File(getClass().getResource(filename).toURI());
        }catch(NullPointerException nullPointerException){
            nullPointerException.printStackTrace();
        }catch(Exception ignored){}
    }

    /**
     * @return: The list of integers contained in the file, all zeros if file is corrupted.
     */
    @Override
    public List<Integer> read() {
        String content = "";
        try(BufferedReader reader = new BufferedReader(new FileReader(this.file))){
            content = reader.readLine();
        }catch(Exception e){
            System.out.println("Error when reading file.");
            e.printStackTrace();
        }
        content = (content == null) ? "" : content;
        return parseText(content);
    }

    /**
     * @param score: Score to be written to scoreboard file.
     */
    @Override
    public void write(int score) {
        if(score < 0){
            throw new IllegalArgumentException("Score cannot be negative.");
        }
        List<Integer> currentScores = this.read();
        scoreListSorter(currentScores, score);
        StringBuilder scoreString = new StringBuilder();
        for(int highScore : currentScores){
            scoreString.append(highScore).append(",");
        }
        try{
            scoreString.deleteCharAt(scoreString.lastIndexOf(","));
        }catch(StringIndexOutOfBoundsException ignored){}

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(this.file))){
            writer.write(scoreString.toString());
        }catch(Exception e){
            System.out.println("File could not be found nor created.");
        }
    }

    /**
     * Tries to parse a string to a list of integers.
     * @param text: String of comma-separated, integers (preferably).
     * @return: Returns the string parsed to integers.
     */
    private List<Integer> parseText(String text){
        String[] textArray = text.split(",");
        List<Integer> intArray = new ArrayList<>();
        for(String score : textArray){
            if(!score.equals("")){
                try{
                    intArray.add(Integer.parseInt(score));
                }catch(NumberFormatException e){
                    System.out.println("Error in high score file formatting.");
                }
            }
        }
        if(intArray.size() == 0){
            for(int i = 0; i < 10; i++){
                intArray.add(0);
            }
        }
        return intArray;
    }

    /**
     * Formats the list of high scores to have 10 entries and be sorted from high to low.
     * @param currentScores: The current high scores as list.
     * @param score: The score to be compared/added.
     */
    private void scoreListSorter(List<Integer> currentScores, int score){
        if(currentScores.size() >= 10){
            currentScores.add(score);
            int minIndex = 0;
            while(currentScores.size() > 10){
                for(int i = 1; i < currentScores.size(); i++){
                    if(currentScores.get(i) < currentScores.get(minIndex)){
                        minIndex = i;
                    }
                }
                currentScores.remove(minIndex);
            }
        }else{
            currentScores.add(score);
        }
        Collections.sort(currentScores);
        Collections.reverse(currentScores);
    }
}