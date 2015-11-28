
/**
 * Created by Nikolay on 27.11.2015 г..
 */
import java.util.*;
public class CharacterList {
    private List<CharacterItem> characterList = new ArrayList<CharacterItem>();
    private String characterListAsString;

    private boolean iterable = false;
    private int loopCounter = 0;


    public CharacterList(Map<Character, Double> characters) {

        //Create a List from a HashMap
        for (Map.Entry<Character, Double> entry : characters.entrySet()) {
            characterList.add(new CharacterItem(entry.getKey(), entry.getValue()));
        }

        //Sort the List
        Collections.sort(characterList);

        //Create a StringBuilder with all characters
        characterListAsString = convertListContentsToString();



    }

    public int size() {
        return characterList.size();
    }

    public void iterate(){
        iterable = true;
        loopCounter = 0;
    }

    public boolean hasNext(){
        if(loopCounter >= characterList.size())
            iterable = false;
        return iterable;
    }

    public String getNext(){
        if(!iterable) return null;
        return characterList.get(loopCounter++).toString();
    }


    //Converts a List to a StringBuilder
    private String convertListContentsToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (CharacterItem item : characterList) {
            stringBuilder.append(item.getCharacter());
        }
        return stringBuilder.toString();
    }

    //Returns a substring of characters from startIndex to endIndex from the List
    public String getString(int startIndex, int endIndex) throws Exception {
        return characterListAsString.substring(startIndex, endIndex + 1);
    }

    //Finds a middle index based on the probability of characters in the List
    //The middle index is the last index of the first half
    public int findMiddleIndexBasedOnProbability(int startIndex, int endIndex) throws Exception {
        int middleIndex = 0;

        //Valid input check
        if (startIndex < 0 || endIndex < 0)
            throw new Exception("An index must be a possitive number");
        if (endIndex > (characterList.size() - 1) || startIndex > (characterList.size() - 1))
            throw new IndexOutOfBoundsException("Index exceeds list size");

        if (startIndex > endIndex) return -1;
        else if (startIndex == endIndex || startIndex == endIndex - 1) return startIndex;

        double totalProbability = 0;
        for (int i = startIndex; i <= endIndex; i++) {
            totalProbability += characterList.get(i).getProbability();
        }

        double tempProbability = 0;
        for (int i = startIndex; i <= endIndex; i++) {
            tempProbability += characterList.get(i).getProbability();
            if (tempProbability >= totalProbability / 2) {
                middleIndex = ((tempProbability - (totalProbability / 2)) <
                        (totalProbability / 2 - (tempProbability - characterList.get(i - 1).getProbability()))) ? i : i - 1;
                break;
            }

        }

        return middleIndex;
    }

    private static class CharacterItem implements Comparable<CharacterItem> {
        private char character;
        private double probability;

        public CharacterItem(char character, double probability) {
            this.character = character;
            this.probability = probability;
        }

        public char getCharacter() {
            return character;
        }

        public double getProbability() {
            return probability;
        }

        @Override
        public String toString() {
            return String.format("%c -> [%.6f]",this.getCharacter(), this.getProbability());
        }

        @Override
        public int compareTo(CharacterItem characterItem2) {
            if ((characterItem2.getProbability() - this.getProbability()) > 0)
                return 1;
            else if ((characterItem2.getProbability() - this.getProbability()) < 0)
                return -1;
            else return 0;
        }
    }

}
