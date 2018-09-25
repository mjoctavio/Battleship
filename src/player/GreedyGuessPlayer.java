package player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import world.World;

/**
 * Greedy guess player (task B).
 * Please implement this class.
 *
 * @author Youhan Xia, Jeffrey Chan
 */
public class GreedyGuessPlayer  implements Player{
    public List<Guess> patternGuesses;
    public List<Guess> greedyGuesses;
    public boolean[][] hitMatrix;

    @Override
    public void initialisePlayer(World world) {
        patternGuesses = new ArrayList<>();
        hitMatrix = new boolean[world.numColumn][world.numRow];
        enumeratePatternGuesses(patternGuesses, world);
    } // end of initialisePlayer()

    private void enumeratePatternGuesses(List<Guess> list, World world) {
        for(int row = 0; row < world.numRow; ++row){
            for(int col = row%2; col < world.numColumn; col+=2){
                Guess g = new Guess();
                g.row = row;
                g.column = col;
                list.add(g);
            }
        }
    }

    @Override
    public Answer getAnswer(Guess guess) {
        // To be implemented.

        // dummy return
        return null;
    } // end of getAnswer()


    @Override
    public Guess makeGuess() {
        Random random = new Random();
        // ** Targeting greedy mode **
        if(!greedyGuesses.isEmpty()){
            int index = random.nextInt(greedyGuesses.size());
            return greedyGuesses.remove(index);
        }


        int index = random.nextInt(patternGuesses.size());
        return patternGuesses.remove(index);
    } // end of makeGuess()


    @Override
    public void update(Guess guess, Answer answer) {
        // To be implemented.
        if(answer.isHit){
            hitMatrix[guess.column][guess.row] = true;

        }
    } // end of update()


    @Override
    public boolean noRemainingShips() {
        // To be implemented.

        // dummy return
        return true;
    } // end of noRemainingShips()

} // end of class GreedyGuessPlayer
