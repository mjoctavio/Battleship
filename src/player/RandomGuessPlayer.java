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
 * @author Youhan Xia, Jeffrey Chan
 */
public class RandomGuessPlayer implements Player {

    int rowSize = 0;
    int clnSize = 0;
    boolean isHex = false;
    RandomGuessPlayer.OwnShip[] ownShip = new RandomGuessPlayer.OwnShip[5];
    boolean[][] isguessed;

    @Override
    public void initialisePlayer(World world) {
        this.rowSize = world.numRow;
        this.clnSize = world.numColumn;
        this.isHex = world.isHex;
        this.isguessed = new boolean[this.rowSize][this.clnSize];
        int var2 = 0;

        for (Iterator var3 = world.shipLocations.iterator(); var3.hasNext(); ++var2) {
            World.ShipLocation var4 = (World.ShipLocation) var3.next();
            this.ownShip[var2] = new RandomGuessPlayer.OwnShip();
            this.ownShip[var2].ship = var4.ship;

            for (int var5 = 0; var5 < this.ownShip[var2].ship.len(); ++var5) {
                this.ownShip[var2].rowCdns[var5] = (var4.coordinates.get(var5)).row;
                this.ownShip[var2].clnCdns[var5] = (var4.coordinates.get(var5)).column;
                this.ownShip[var2].isdown[var5] = false;
            }
        }
    } // end of initialisePlayer()

    @Override
    public Answer getAnswer(Guess guess) {
        Answer answer = new Answer();

        for(int ship = 0; ship < 5; ++ship) {
            for(int i = 0; i < this.ownShip[ship].ship.len(); ++i) {
                if(guess.row == this.ownShip[ship].rowCdns[i] && guess.column == this.ownShip[ship].clnCdns[i]) {
                    answer.isHit = true;
                    this.ownShip[ship].isdown[i] = true;
                    boolean var5 = true;

                    for(int var6 = 0; var6 < this.ownShip[ship].ship.len(); ++var6) {
                        if(!this.ownShip[ship].isdown[var6]) {
                            var5 = false;
                        }
                    }

                    if(var5) {
                        answer.shipSunk = this.ownShip[ship].ship;
                    }

                    return answer;
                }
            }
        }

        return answer;
    } // end of getAnswer()


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


    @Override
    public void update(Guess guess, Answer answer) {
        // To be implemented.
    } // end of update()


    @Override
    public boolean noRemainingShips() {

        for(int var1 = 0; var1 < 5; ++var1) {
            for(int var2 = 0; var2 < this.ownShip[var1].ship.len(); ++var2) {
                if(!this.ownShip[var1].isdown[var2]) {
                    return false;
                }
            }
        }
        return true;
    } // end of noRemainingShips()

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
