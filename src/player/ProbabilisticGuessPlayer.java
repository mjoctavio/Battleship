package player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import ship.Ship;
import world.World;

@SuppressWarnings("Duplicates")

/**
 * Probabilistic guess player (task C).
 * Please implement this class.
 *
 * @author Kristin Stenland
 * last update 9 Oct 18
 *
 *
 */
public class ProbabilisticGuessPlayer implements Player {

    public int noRows;
    public int noCols;
    public ProbabilisticGuessPlayer.OwnShip[] ownShips = new ProbabilisticGuessPlayer.OwnShip[5];
    public int count1 = 0;
    public boolean isGuessed[][];
    public boolean hunting = true;
    public Guess prevGuess;
    public Answer prevAnswer;
    public List<Guess> targetGuesses;
    public int[][] patrolProbTable = new int[noRows][noCols];
    public int[][] subProbTable = new int[noRows][noCols];
    public int[][] frigateProbTable = new int[noRows][noCols];
    public int[][] cruiserProbTable = new int[noRows][noCols];
    public int[][] carrierProbTableVert = new int[noRows][noCols];
    public int[][] carrierProbTableHoriz = new int[noRows][noCols];
    public int[][] finalProbTable = new int[noRows][noCols];

    /**
     *Initialises the world, places the players ship's
     * and produces a table of probabilities
     * */
    @Override
    public void initialisePlayer(World world) {
        noRows = world.numRow;
        noCols = world.numColumn;
        this.isGuessed = new boolean[this.noRows][this.noCols];

        //place ships
        for (Iterator iterator = world.shipLocations.iterator(); iterator.hasNext(); ++count1) {
            World.ShipLocation shipLoc = (World.ShipLocation) iterator.next();
            this.ownShips[count1] = new ProbabilisticGuessPlayer.OwnShip();
            this.ownShips[count1].ship = shipLoc.ship;

            for (int i = 0; i < this.ownShips[count1].ship.len(); ++i) {
                this.ownShips[count1].rowCdns[i] = (shipLoc.coordinates.get(i)).row;
                this.ownShips[count1].clnCdns[i] = (shipLoc.coordinates.get(i)).column;
                this.ownShips[count1].isdown[i] = false;
            }
        }
        //produce the probability table
        finalProbTable = produceProbTable();

        //print for troubleshooting
        printTable("Total Table", finalProbTable);
    } // end of initialisePlayer()


    /**
     * Produces a probability table for (A x 1) ships: Patrol Craft, Frigate and Submarine
     * For each square in the grid, check if the ship can fit in each direction.
     *
     * @param maxProb
     * @param shipLength
     * @return
     */
    public int[][] calcProb(int maxProb, int shipLength) {
        int[][] probTable = new int[noRows][noCols];
        for (int i = 0; i < noRows; i++) {
            for (int j = 0; j < noCols; j++) {
                int tempProb = maxProb;
                for (int k=1; k<shipLength; k++) {
                    //check if ship can fit up
                    if (i-k<0) {
                        tempProb--;
                    }
                    //check if ship can fit down
                    if (i+k>noRows-1) {
                        tempProb--;
                    }
                    //check if ship can fit to the left
                    if (j-k<0) {
                        tempProb--;
                    }
                    //check if ship can fit to the right
                    if (j+k>noCols-1) {
                        tempProb--;
                    }
                }
                probTable[i][j] = tempProb;
            }
        }
        return probTable;
    }

    /**
     * Produces a probability table of an Aircraft Carrier in the vertical orientation
     *
     * @param shipLength
     * @param shipHeight
     * @return
     */
    public int[][] calcCarrierProbVert(int shipLength, int shipHeight) {
        int[][] probTable = new int[noRows][noCols];
        for (int i=0; i<noRows; i++) {
            for (int j=0; j<noCols; j++) {
                int prob = 0;
                if (i+shipHeight-1<noRows && j+shipLength-1<noCols ) {
                    prob++;
                }
                if (i+shipHeight-2<noRows && i-1>-1 && j+shipLength-1<noCols) {
                    prob++;
                }
                if (i-shipHeight+1>-1 && j+shipLength-1<noCols) {
                    prob++;
                }
                if (i+shipHeight-1<noRows && j-shipLength+1>-1) {
                    prob++;
                }
                if (i+shipHeight-2<noRows && i-1>-1 && j-shipLength+1>-1){
                    prob++;
                }
                if (i-shipHeight+1>-1 && j-shipLength+1>-1){
                    prob++;
                }
                probTable[i][j] = prob;
            }
        }
        return probTable;
    }

    /**
     * Calculate the probability table for an Aircraft Carrier in the Horizontal Orientation
     *
     * @param shipLength
     * @param shipHeight
     * @return
     */
    public int[][] calcCarrierProbHoriz(int shipLength, int shipHeight) {
        int[][] probTable = new int[noRows][noCols];
        for (int i=0; i<noRows; i++) {
            for (int j = 0; j < noCols; j++) {
                int prob = 0;
                if (j+shipLength-1<noCols && i+shipHeight-1<noRows ) {
                    prob++;
                }
                if (j+shipLength-2<noCols && j-1>-1 && i+shipHeight-1<noRows) {
                    prob++;
                }
                if (j-shipLength+1>-1 && i+shipHeight-1<noRows) {
                    prob++;
                }
                if (j+shipLength-1<noCols && i-shipHeight+1>-1) {
                    prob++;
                }
                if (j+shipLength-2<noCols && j-1>-1 && i-shipHeight+1>-1){
                    prob++;
                }
                if (j-shipLength+1>-1 && i-shipHeight+1>-1){
                    prob++;
                }
                probTable[i][j] = prob;
            }
        }
        return probTable;
    }

    /**
     * Calculates the probability table for a Cruiser
     *
     * @param maxProb
     * @param shipLength
     * @param shipHeight
     * @return
     */

    public int[][] calcCruiserProb(int maxProb, int shipLength, int shipHeight) {
        int[][] probTable = new int[noRows][noCols];
        for (int i = 0; i < noRows; i++) {
            for (int j = 0; j < noCols; j++) {
                int tempProb = maxProb;
                //calc length
                for (int k=1; k<shipLength; k++) {
                    //check up
                    if (i-k<0) {
                        for (int l=1; l<shipHeight; l++) {
                            tempProb--;
                        }
                    }
                    //check down
                    if (i+k>noRows-1) {
                        tempProb--;
                    }
                    //check left
                    if (j-k<0) {
                        tempProb--;
                    }
                    //check right
                    if (j+k>noCols-1) {
                        tempProb--;
                    }
                    //calc height
                    for (int l=1; l<shipHeight; l++) {
                        //check up
                        if (i-l<0) {
                            tempProb--;
                        }
                        //check down
                        if (i+l>noRows-1) {
                            tempProb--;
                        }
                        //check left
                        if (j-l<0) {
                            tempProb--;
                        }
                        //check right
                        if (j+l>noCols-1) {
                            tempProb--;
                        }
                    }
                    //increment corners
                    if ((i==0 && j==0) || (i==noRows-1 && j==noCols-1) || (i==noRows-1 && j==0) || (i==0 && j==noCols-1)) {
                        tempProb++;
                    }
                }
                probTable[i][j] = tempProb;
            }
        }
        return probTable;
    }

    /**
     * Adds all the probability tables together to produce an overall probability table
     *
     * @return
     */
    public int[][] produceProbTable() {
        int[][] finalProbTable = new int[noRows][noCols];

        patrolProbTable = calcProb(4,2);
        //print for troubleshooting
        printTable("PatrolCraft", patrolProbTable);

        subProbTable = calcProb(6, 3);
        //print for troubleshooting
        printTable("Submarine", subProbTable);

        frigateProbTable = calcProb(8, 4);
        //print for troubleshooting
        printTable("Frigate", frigateProbTable);

        cruiserProbTable = calcCruiserProb(4, 2, 2);
        //print for troubleshooting
        printTable("Cruiser", cruiserProbTable);

        carrierProbTableVert = calcCarrierProbVert(2, 3);
        //print for troubleshooting
        printTable("Aircraft Carrier, Vertical", carrierProbTableVert);

        carrierProbTableHoriz = calcCarrierProbHoriz(3, 2);
        //print for troubleshooting
        printTable("Aircraft Carrier, Horizontal", carrierProbTableHoriz);

        for (int i=0; i<noRows; i++) {
            for (int j=0; j<noCols; j++) {
                finalProbTable[i][j] = patrolProbTable[i][j] + subProbTable[i][j] + frigateProbTable[i][j] + cruiserProbTable[i][j] + carrierProbTableVert[i][j] + carrierProbTableHoriz[i][j];
            }
        }
        return finalProbTable;
    }


    /**
     * Prints a probability table for troubleshooting
     *
     * @param name
     * @param table
     */
    public void printTable (String name, int[][] table) {
        System.out.println("Printing Prob Table for: " + name);
        for (int i=0; i<noRows; i++) {
            for (int j=0; j<noCols; j++) {
                System.out.print(table[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }


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
     * Make a guess, has hunting and targeting modes
     *
     * @return
     */
    @Override
    public Guess makeGuess() {

        Guess guess = new Guess();
        targetGuesses = new ArrayList<>();

        //Targeting mode
        if (!hunting) {
            int i = prevGuess.row;
            int j = prevGuess.column;
            //if all surround grid squares are guessed (and all grid squares are valid/within the grid) enter hunting mode
            //this requires fixing!!
            if (j - 1 > -1 && isGuessed[i][j - 1] && i - 1 > -1 && isGuessed[i - 1][j] && j + 1 < noCols && isGuessed[i][j + 1] && i + 1 < noRows && isGuessed[i + 1][j]) {
                hunting = true;
            }
            else {
                //check to the left, if valid and if not guessed, add to the list of potential guesses
                if (j - 1 > -1 && !isGuessed[i][j - 1]) {
                    Guess guess1 = new Guess();
                    guess1.row = i;
                    guess1.column = j - 1;
                    targetGuesses.add(guess1);
                }
                //check up
                if (i - 1 > -1 && !isGuessed[i - 1][j]) {
                    Guess guess2 = new Guess();
                    guess2.row = i - 1;
                    guess2.column = j;
                    targetGuesses.add(guess2);
                }
                //check right
                if (j + 1 < noCols && !isGuessed[i][j + 1]) {
                    Guess guess3 = new Guess();
                    guess3.row = i;
                    guess3.column = j + 1;
                    targetGuesses.add(guess3);
                }
                //check down
                if (i + 1 < noRows && !isGuessed[i + 1][j]) {
                    Guess guess4 = new Guess();
                    guess4.row = i + 1;
                    guess4.column = j;
                    targetGuesses.add(guess4);
                }
                //find the highest probability of potential guesses
                int highest = 0;
                List<Guess> highestGuesses = new ArrayList<>();
                for (Guess g : targetGuesses) {
                    int prob = finalProbTable[g.row][g.column];
                    if (prob >= highest) {
                        highest = prob;
                        highestGuesses.add(g);
                    }
                }
                if (highestGuesses.size()==0) {
                    hunting = true;
                }
                //if more than one highest probability, pick one at random
                else {
                    Random random = new Random();
                    int index = random.nextInt(highestGuesses.size());
                    guess = highestGuesses.get(index);
                    isGuessed[guess.row][guess.column] = true;
                    return guess;
                }
            }

        }
        //Hunting Mode
        //Find highest probability grids and pick one at random
        if (hunting) {
            List<Guess> potentialGuesses = findHighestProbs(finalProbTable);
            Random random = new Random();
            int index = random.nextInt(potentialGuesses.size());
            guess = potentialGuesses.get(index);
            isGuessed[guess.row][guess.column] = true;
            return guess;
        }

        return guess;

    } // end of makeGuess()

    /**
     * Return an Arraylist of the highest probability squares from the table
     *
     * @param table
     * @return
     */
    public List findHighestProbs(int[][] table) {
        List<Guess> highestProbs = new ArrayList();
        int highest =0;
        //find highest probability
        for (int i=0; i<noRows; i++) {
            for (int j=0; j<noCols; j++) {
                if (highest<table[i][j] && !isGuessed[i][j]) {
                    highest = table[i][j];
                }
            }
        }
        //add highest probability grids to an array list
        for (int i=0; i<noRows; i++) {
            for (int j=0; j<noCols; j++) {
                if (table[i][j] == highest && !isGuessed[i][j]) {
                    Guess guess = new Guess();
                    guess.row = i;
                    guess.column = j;
                    highestProbs.add(guess);
                }
            }
        }
        return highestProbs;
    }


    /**
     * Updates probability table and hunting/targeting mode based on the previous guess and returned answer
     *
     * @param guess Guess of this player.
     * @param answer Answer to the guess from opponent.
     */
    @Override
    public void update(Guess guess, Answer answer) {
        if (answer.isHit && answer.shipSunk==null) {
            hunting = false;
            prevGuess = guess;
            prevAnswer = answer;
        }
        else if (answer.isHit && answer.shipSunk!=null) {
            hunting = true;
            targetGuesses.clear();
            //prevGuess = guess;
            //prevAnswer = answer;
            if (answer.shipSunk.name().equals("AircraftCarrier")) {
                for (int i=0; i<noRows; i++) {
                    for (int j=0; j<noCols; j++) {
                        finalProbTable[i][j] = finalProbTable[i][j] - carrierProbTableVert[i][j] - carrierProbTableHoriz[i][j];
                    }
                }
            }
            if (answer.shipSunk.name().equals("Cruiser")) {
                for (int i=0; i<noRows; i++) {
                    for (int j=0; j<noCols; j++) {
                        finalProbTable[i][j] = finalProbTable[i][j] - cruiserProbTable[i][j];
                    }
                }
            }
            if (answer.shipSunk.name().equals("Frigate")) {
                for (int i=0; i<noRows; i++) {
                    for (int j=0; j<noCols; j++) {
                        finalProbTable[i][j] = finalProbTable[i][j] - frigateProbTable[i][j];
                    }
                }
            }
            if (answer.shipSunk.name().equals("PatrolBoat")) {
                for (int i=0; i<noRows; i++) {
                    for (int j=0; j<noCols; j++) {
                        finalProbTable[i][j] = finalProbTable[i][j] - patrolProbTable[i][j];
                    }
                }
            }
            if (answer.shipSunk.name().equals("Submarine")) {
                for (int i=0; i<noRows; i++) {
                    for (int j=0; j<noCols; j++) {
                        finalProbTable[i][j] = finalProbTable[i][j] - subProbTable[i][j];
                    }
                }
            }
        }
        /**
        else if (!answer.isHit && !hunting) {
            if (targetGuesses.size()!=0) {
                hunting = false;
            }
            else {
                hunting = true;
            }



            if (prevAnswer!=null) {
                if (!prevAnswer.isHit) {
                    hunting = true;
                }
                else {
                    hunting = false;
                }
            }

        }*/

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
    }

} // end of class ProbabilisticGuessPlayer
