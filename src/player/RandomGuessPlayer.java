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
        this.isguessed = new boolean[this.rowSize][this.clnSize + (this.rowSize + 1) / 2];
        int var2 = 0;

        for (Iterator var3 = world.shipLocations.iterator(); var3.hasNext(); ++var2) {
            World.ShipLocation var4 = (World.ShipLocation) var3.next();
            this.ownShip[var2] = new RandomGuessPlayer.OwnShip();
            this.ownShip[var2].ship = var4.ship;

            for (int var5 = 0; var5 < this.ownShip[var2].ship.len(); ++var5) {
                this.ownShip[var2].rowCdns[var5] = ((World.Coordinate) var4.coordinates.get(var5)).row;
                this.ownShip[var2].clnCdns[var5] = ((World.Coordinate) var4.coordinates.get(var5)).column;
                this.ownShip[var2].isdown[var5] = false;
            }
        }
    } // end of initialisePlayer()

    @Override
    public Answer getAnswer(Guess guess) {
        Answer var2 = new Answer();

        for(int var3 = 0; var3 < 5; ++var3) {
            for(int var4 = 0; var4 < this.ownShip[var3].ship.len(); ++var4) {
                if(guess.row == this.ownShip[var3].rowCdns[var4] && guess.column == this.ownShip[var3].clnCdns[var4]) {
                    var2.isHit = true;
                    this.ownShip[var3].isdown[var4] = true;
                    boolean var5 = true;

                    for(int var6 = 0; var6 < this.ownShip[var3].ship.len(); ++var6) {
                        if(!this.ownShip[var3].isdown[var6]) {
                            var5 = false;
                        }
                    }

                    if(var5) {
                        var2.shipSunk = this.ownShip[var3].ship;
                    }

                    return var2;
                }
            }
        }

        return var2;
    } // end of getAnswer()


    @Override
    public Guess makeGuess() {
        Random var3 = new Random();

        int var1;
        int var2;
        do {
            var1 = var3.nextInt(this.rowSize);
            var2 = var3.nextInt(this.clnSize);
            if(this.isHex) {
                var2 += (var1 + 1) / 2;
            }
        }
        while(this.isguessed[var1][var2]);
        Guess var4 = new Guess();
        var4.row = var1;
        var4.column = var2;
        this.isguessed[var1][var2] = true;
        return var4;
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
