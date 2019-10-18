package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.ArrayList;

/**
 * Created by Parker on 1/27/18.
 */


public class EvilHangmanGame implements IEvilHangmanGame {

    Boolean wordGuessed;
    int wordLength;
    int guesses;
    Set<String> currentDictionary = null;
    Set<String> guessedLetters = null;
    String currentWord = null;

    public static void main(String[] args)
    {
        if(args.length == 3)
        {
            EvilHangmanGame EHG = new EvilHangmanGame();

            //java [your main class name]  dictionary wordLength guesses
            EHG.setWordLength(Integer.valueOf(args[1]));
            EHG.setGuesses(Integer.valueOf(args[2]));

            if(EHG.getWordLength() >= 2 && EHG.getGuesses() >= 1)
            {
                //this should just initialize everything
                EHG.startGame(new File(args[0]), EHG.getWordLength());

                Scanner userInput = new Scanner(System.in);

                while(EHG.getGuesses() > 0)
                {
                    try{
                        EHG.printState();
                        //gonna point at the new set.
                        String input = userInput.next();
                        input = input.toLowerCase();

                        EHG.currentDictionary = EHG.makeGuess(input.charAt(0));

                        //check for the letter...

                        //have to add the letter to the current word...
                    }
                    catch (GuessAlreadyMadeException e)
                    {
                        System.out.print("That guess was already made!");
                    }
                    catch (Exception e)
                    {
                        System.out.print(e.getMessage());
                    }

                    if(EHG.wordGuessed == true)
                    {
                        System.out.print("\n\nYou Win!\n\n");
                        EHG.guesses = 0;
                    }
                }

                System.out.print("The word was " + EHG.currentDictionary.toArray()[0]);

            }
            else
            {
                System.out.print("Usage: java [your main class name]  dictionary wordLength guesses");
            }
        }



    }

    //constructor should not take any arguments
    public EvilHangmanGame()
    {
        guessedLetters = new HashSet<String>();
        wordGuessed = false;
    }

    public void readDictionary(File dictionaryFile)
    {
        Scanner scanner = null;

        try{
            scanner = new Scanner(dictionaryFile);
            while(scanner.hasNext())
            {
                String wordDict = scanner.next();
                if(wordDict.length() == wordLength)
                {
                    currentDictionary.add(wordDict);
                }
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.print("File not found exception!");
        }
        catch (Exception e)
        {
            System.out.print(e.getMessage());
        }
        finally
        {
            scanner.close();
        }


    }

    /**
     * Starts a new game of evil hangman using words from <code>dictionary</code>
     * with length <code>wordLength</code>.
     *	<p>
     *	This method should set up everything required to play the game,
     *	but should not actually play the game. (ie. There should not be
     *	a loop to prompt for input from the user.)
     *
     * @param dictionary Dictionary of words to use for the game
     * @param wordLength Number of characters in the word to guess
     */
    public void startGame(File dictionary, int wordLength)
    {
        this.wordLength = wordLength;
        initializeWord();
        currentDictionary = new HashSet<String>();
        //read in the dictionary
        readDictionary(dictionary);
    }

    /**
     * Make a guess in the current game.
     *
     * @param guess The character being guessed
     *
     * @return The set of strings that satisfy all the guesses made so far
     * in the game, including the guess made in this call. The game could claim
     * that any of these words had been the secret word for the whole game.
     *
     * @throws IEvilHangmanGame.GuessAlreadyMadeException If the character <code>guess</code>
     * has already been guessed in this game.
     */

    public Set<String> makeGuess(char guess) throws IEvilHangmanGame.GuessAlreadyMadeException
    {

        //if the char is already in the guessed values
        if(guessedLetters.contains((String.valueOf(guess))))
        {
            throw new GuessAlreadyMadeException();
        }

        //do everything if the char is in the alphabet
        if(Character.isAlphabetic(guess))
        {
            //this is where we create the map of dirtiness.
            //parse through the current dictionary to make the map stuff
            //create the hashmap to return the new set that is the biggest
            HashMap<String, HashSet<String>> mapSet = new HashMap();

            for(String word : currentDictionary)
            {
                String pattern = getPattern(word, guess);

                //check patter in map...
                //if the pattern is already there... add the value to the set
                if(mapSet.containsKey(pattern))
                {
                    //gets the pointer to the set in the map... then add the word to it.
                    HashSet<String> tempSet = mapSet.get(pattern);
                    tempSet.add(word);
                }
                else // create the key in the map and add the word associated with it in the set
                {
                    HashSet<String> tempSet = new HashSet();
                    tempSet.add(word);
                    mapSet.put(pattern, tempSet);
                }
            }

            //somehow have to return the biggest set as the new dictionary...
            String greatestKey = setChecker(mapSet);

            //add the newly guessed character to the set
            guessedLetters.add(String.valueOf(guess));

            //decrement turns in checkPattern function
            int numberOfLetters = checkPattern(greatestKey);
            if(numberOfLetters > 0)
            {
                System.out.print("\nYes, there is  " + numberOfLetters + " " + guess + "\'s\n");
            }
            else
            {
                System.out.print("\nSorry, there are no " + guess + "\'s\n");
                setGuesses(getGuesses()-1);
            }

            //checks to see if the whole word has been guessed
            updateCurrentWord(greatestKey);

            if(checkCurrentWord(currentWord) == 0)
            {
                wordGuessed = true;
            }

            currentDictionary = mapSet.get(greatestKey);
            return mapSet.get(greatestKey);
        }
        else
        {
            System.out.print("\nInvalid input\n");
            return currentDictionary;
        }

        //do all the stuff to partition the set... depending on the letter. and parse
        //through the current dictionary.
    }

    public String setChecker(HashMap<String, HashSet<String>> mapSet)
    {
        int tempSize = 0;
        int greatestSize = 0;
        String greatestKey = "";
        ArrayList<String> arrayEqualSize = new ArrayList<String>();


        //this can check for size... but maybe i should put them into an array or something...
        for(String pattern: mapSet.keySet())
        {
            tempSize = mapSet.get(pattern).size();
            if(tempSize > greatestSize)
            {
                greatestSize = tempSize;
            }
        }
        //now i have the greatest size... so now i can use that size and parse through again.


        //parse through to add the equal sized sets to the array.
        for(String pattern: mapSet.keySet())
        {
            if(mapSet.get(pattern).size() == greatestSize)
            {
                arrayEqualSize.add(pattern);
            }
        }

        //step 0
        //if there is only one greatest size array
        if(arrayEqualSize.size() <= 1)
        {
            return arrayEqualSize.get(0);
        }

        //step 1 choose where letter doesn't appear at all
        for(String pattern: arrayEqualSize)
        {
            if(checkCurrentWord(pattern) == pattern.length())
            {
                return pattern;
            }
        }

        //////////step 2 if each group has the letter, choose the fewest
        int tempSize2 = 0;
        int greatestSize2 = 0;

        //this is to find the greatest size... now i have to add them to a new array list
        for(String pattern: arrayEqualSize)
        {
            //checkCurrentWord() returns the count of spaces in the pattern
            tempSize2 = checkCurrentWord(pattern);
            if(tempSize2 > greatestSize2)
                greatestSize2 = tempSize2;
        }

        ArrayList<String> arrayEqualSizeUpdated = new ArrayList<String>();
        for(int i = 0; i < arrayEqualSize.size(); i++)
        {
            if(checkCurrentWord(arrayEqualSize.get(i)) == greatestSize2)
                arrayEqualSizeUpdated.add(arrayEqualSize.get(i));
        }

        if(arrayEqualSizeUpdated.size() == 1)
            return arrayEqualSizeUpdated.get(0);

        //////////end step 2

        ////////////step 3

        for(int i = arrayEqualSizeUpdated.get(0).length() - 1; i >= 0; i--)
        {
            arrayEqualSizeUpdated = step3(arrayEqualSizeUpdated, i);

            if(arrayEqualSizeUpdated.size() == 1)
            {
                return arrayEqualSizeUpdated.get(0);
            }
        }
        ////////////end step 3

        return greatestKey;
    }

    public ArrayList<String> step3(ArrayList<String> currentSet, int indexChar)
    {
        ArrayList<String> lastSets = new ArrayList<String>();
        Boolean oneInSet = false;

        for(String pattern: currentSet)
        {
            Character tempChar = pattern.charAt(indexChar);

            if (!tempChar.equals('_'))
            {
                lastSets.add(pattern);
                oneInSet = true;
            }
        }
        if(oneInSet == true)
        {
            return lastSets;
        }
        return currentSet;
    }

    public int checkCurrentWord(String word)
    {
        int spaceChecker = 0;
        for(int i = 0; i < word.length(); i++)
        {
            // if it's not equal to a '_' then add it to the currentWord
            Character letter = Character.valueOf(word.charAt(i));
            if(letter.equals('_'))
            {
                spaceChecker++;
            }
        }
        return spaceChecker;
    }

    public int checkPattern(String greatestKey)
    {
        int counter = 0;
        for(int i = 0; i < greatestKey.length(); i++)
        {
            if(!Character.valueOf(greatestKey.charAt(i)).equals('_'))
                counter++;
        }
        return counter;
    }

    public int updateCurrentWord(String greatestKey)
    {
        StringBuilder SB = new StringBuilder(currentWord);
        int spaceChecker = 0;

        for(int i = 0; i < greatestKey.length(); i++)
        {
            // if it's not equal to a '_' then add it to the currentWord
            Character letter = Character.valueOf(greatestKey.charAt(i));
            if(!letter.equals('_'))
            {
                SB.setCharAt(i, letter);
            }
            else
            {
                spaceChecker++;
            }
        }
        currentWord = SB.toString();

        return spaceChecker;
    }

    public String getPattern(String word, Character letter)
    {
        StringBuilder SB = new StringBuilder();

        for(int i = 0; i < word.length(); i++)
        {
            //if the letter is equal then add the letter to the string builder
            if(letter.equals(word.charAt(i)))
            {
                SB.append(letter);
            }
            else
            {
                SB.append("_");
            }
        }
        return SB.toString();
    }

    public void printState()
    {
        System.out.print("\nRemaining Guesses: " + guesses);
        System.out.print("\nGuessed Letters: " + guessedLettersToString());
        System.out.print("\nCurrent Word: " + getCurrentWord());
        System.out.print("\n");
    }

    public String guessedLettersToString()
    {
        StringBuilder SB = new StringBuilder("");
        for(String letter: getGuessedLetters())
        {
            SB.append(letter + " ");
        }
        return SB.toString();
    }

    public void initializeWord()
    {
        StringBuilder SB = new StringBuilder("");
        for(int i = 0; i < getWordLength(); i++)
        {
            SB.append("_");
        }
        setCurrentWord(SB.toString());
    }

    public void setCurrentWord(String wordIn)
    {
        this.currentWord = wordIn;
    }

    public String getCurrentWord(){
        return currentWord;
    }

    public Set<String> getGuessedLetters(){
        return guessedLetters;
    }

    public int getWordLength() {
        return wordLength;
    }

    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }

    public int getGuesses() {
        return guesses;
    }

    public void setGuesses(int guesses) {
        this.guesses = guesses;
    }



}
