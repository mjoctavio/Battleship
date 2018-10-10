package player;

import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

import ship.Ship;
import world.World;

/**
 * Random guess player (task A).
 * Please implement this class.
 *
 * @author Youhan Xia, Jeffrey Chan, Kristin Stenland
 */
public class RandomGuessPlayer implements Player {

    public int rowSize = 0;
    public int clnSize = 0;
    public boolean isHex = false;
    RandomGuessPlayer.OwnShip[] ownShips = new RandomGuessPlayer.OwnShip[5];
    boolean[][] isguessed;

    @Override
    public void initialisePlayer(World world) {
        this.rowSize = world.numRow;
        this.clnSize = world.numColumn;
        this.isHex = world.isHex;
        this.isguessed = new boolean[this.rowSize][this.clnSize];
        int count1 = 0;

        //place ships
        for (Iterator iterator = world.shipLocations.iterator(); iterator.hasNext(); ++count1) {
            World.ShipLocation shipLoc = (World.ShipLocation) iterator.next();
            this.ownShips[count1] = new RandomGuessPlayer.OwnShip();
            this.ownShips[count1].ship = shipLoc.ship;

            for (int i = 0; i < this.ownShips[count1].ship.len(); ++i) {
                this.ownShips[count1].rowCdns[i] = (shipLoc.coordinates.get(i)).row;
                this.ownShips[count1].clnCdns[i] = (shipLoc.coordinates.get(i)).column;
                this.ownShips[count1].isdown[i] = false;
            }
        }
    } // end of initialisePlayer()

    /**
     * Get a guess from the opponent and see whether own ship is hit or sunk
     *
     * @param guess from the opponent.
     *
     * @return
     */
    @Override
    public Answer getAnswer(Guess guess) {
        Answer answer = new Answer();

        for(int ship = 0; ship < 5; ++ship) {
            for(int i = 0; i < this.ownShips[ship].ship.len(); ++i) {
                if(guess.row == this.ownShips[ship].rowCdns[i] && guess.column == this.ownShips[ship].clnCdns[i]) {
                    answer.isHit = true;
                    this.ownShips[ship].isdown[i] = true;
                    boolean var5 = true;

                    for(int var6 = 0; var6 < this.ownShips[ship].ship.len(); ++var6) {
                        if(!this.ownShips[ship].isdown[var6]) {
                            var5 = false;
                        }
                    }

                    if(var5) {
                        answer.shipSunk = this.ownShips[ship].ship;
                    }

                    return answer;
                }
            }
        }

        return answer;
    } // end of getAnswer()


    /**
     * Make a guess in a random grid square
     *
     * @return
     */
    @Override
    public Guess makeGuess() {
        Random random = new Random();
        int i;
        int j;
        do {
            i = random.nextInt(this.rowSize);
            j = random.nextInt(this.clnSize);
        }
        while(this.isguessed[i][j]);
        Guess guess = new Guess();
        guess.row = i;
        guess.column = j;
        this.isguessed[i][j] = true;
        return guess;
    } // end of makeGuess()


    /**
     * Update - not used
     *
     * @param guess Guess of this player.
     * @param answer Answer to the guess from opponent.
     */
    @Override
    public void update(Guess guess, Answer answer) {
        // To be implemented.
    } // end of update()


    @Override
    public boolean noRemainingShips() {
        for(int var1 = 0; var1 < 5; ++var1) {
            for(int var2 = 0; var2 < this.ownShips[var1].ship.len(); ++var2) {
                if(!this.ownShips[var1].isdown[var2]) {
                    return false;
                }
            }
        }
        return true;
    } // end of noRemainingShips()

    /**
     * Check if all ships sunk
     *
     * @return
     */
    private class OwnShip {
        Ship ship;
        int[] rowCdns;
        int[] clnCdns;
        boolean[] isdown;

        private OwnShip() {
            this.ship = null;
            this.rowCdns = new int[]{-1, -1, -1, -1, -1};
            this.clnCdns = new int[]{-1, -1, -1, -1, -1};
            this.isdown = new boolean[]{true, true, true, true, true};
        }

    } // end of class RandomGuessPlayer
}
