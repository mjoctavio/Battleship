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
 * @author Youhan Xia, Jeffrey Chan
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

    public List<Guess> potentialGuesses;
    public int[][] patrolProbTable = new int[noRows][noCols];
    public int[][] subProbTable = new int[noRows][noCols];
    public int[][] frigateProbTable = new int[noRows][noCols];
    public int[][] cruiserProbTable = new int[noRows][noCols];
    public int[][] carrierProbTableVert = new int[noRows][noCols];
    public int[][] carrierProbTableHoriz = new int[noRows][noCols];
    public int[][] finalProbTable = new int[noRows][noCols];


    @Override
    public void initialisePlayer(World world) {
        noRows = world.numRow;
        noCols = world.numColumn;
        this.isGuessed = new boolean[this.noRows][this.noCols];
        potentialGuesses = new ArrayList<>();

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
        finalProbTable = produceProbTable();
        //print for troubleshooting
        printTable("Total Table", finalProbTable);


    } // end of initialisePlayer()


    public int[][] calcProb(int maxProb, int shipLength) {
        int[][] probTable = new int[noRows][noCols];
        for (int i = 0; i < noRows; i++) {
            for (int j = 0; j < noCols; j++) {
                int tempProb = maxProb;
                for (int k=1; k<shipLength; k++) {
                    //left
                    if (i-k<0) {
                        tempProb--;
                    }
                    //right
                    if (i+k>noRows-1) {
                        tempProb--;
                    }
                    //up
                    if (j-k<0) {
                        tempProb--;
                    }
                    //down
                    if (j+k>noCols-1) {
                        tempProb--;
                    }
                }
                probTable[i][j] = tempProb;
            }
        }
        return probTable;
    }

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

    public int[][] calcCruiserProb(int maxProb, int shipLength, int shipHeight) {
        int[][] probTable = new int[noRows][noCols];
        for (int i = 0; i < noRows; i++) {
            for (int j = 0; j < noCols; j++) {
                int tempProb = maxProb;
                for (int k=1; k<shipLength; k++) {
                    //left
                    if (i-k<0) {
                        for (int l=1; l<shipHeight; l++) {
                            tempProb--;
                        }
                    }
                    //right
                    if (i+k>noRows-1) {
                        tempProb--;
                    }
                    //up
                    if (j-k<0) {
                        tempProb--;
                    }
                    //down
                    if (j+k>noCols-1) {
                        tempProb--;
                    }
                    for (int l=1; l<shipHeight; l++) {
                        if (i-l<0) {
                            tempProb--;
                        }
                        if (i+l>noRows-1) {
                            tempProb--;
                        }
                        if (j-l<0) {
                            tempProb--;
                        }
                        if (j+l>noCols-1) {
                            tempProb--;
                        }
                    }
                    if ((i==0 && j==0) || (i==noRows-1 && j==noCols-1) || (i==noRows-1 && j==0) || (i==0 && j==noCols-1)) {
                        tempProb++;
                    }
                }
                probTable[i][j] = tempProb;
            }
        }
        return probTable;
    }

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

    public List findHighestProbs(int[][] table) {
        List<Guess> highestProbs = new ArrayList();
        int highest =0;
        for (int i=0; i<noRows; i++) {
            for (int j=0; j<noCols; j++) {
                if (highest<table[i][j] && !isGuessed[i][j]) {
                    highest = table[i][j];
                }
            }
        }
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


    @Override
    public Guess makeGuess() {
        //Hunting Mode
        Guess guess = new Guess();

        //Targeting mode
        if (!hunting) {
            int i = prevGuess.row;
            int j = prevGuess.column;
            if (j - 1 > -1 && isGuessed[i][j - 1] && i - 1 > -1 && isGuessed[i - 1][j] && j + 1 < noCols && isGuessed[i][j + 1] && i + 1 < noRows && isGuessed[i + 1][j]) {
                hunting = true;
            }
            else {
                List<Guess> potentialGuesses = new ArrayList<>();
                if (j - 1 > -1 && !isGuessed[i][j - 1]) {
                    Guess guess1 = new Guess();
                    guess1.row = i;
                    guess1.column = j - 1;
                    potentialGuesses.add(guess1);
                }
                if (i - 1 > -1 && !isGuessed[i - 1][j]) {
                    Guess guess2 = new Guess();
                    guess2.row = i - 1;
                    guess2.column = j;
                    potentialGuesses.add(guess2);
                }
                if (j + 1 < noCols && !isGuessed[i][j + 1]) {
                    Guess guess3 = new Guess();
                    guess3.row = i;
                    guess3.column = j + 1;
                    potentialGuesses.add(guess3);
                }
                if (i + 1 < noRows && !isGuessed[i + 1][j]) {
                    Guess guess4 = new Guess();
                    guess4.row = i + 1;
                    guess4.column = j;
                    potentialGuesses.add(guess4);
                }
                int highest = 0;
                List<Guess> highestGuesses = new ArrayList<>();
                for (Guess g : potentialGuesses) {
                    int prob = finalProbTable[g.row][g.column];
                    if (prob >= highest) {
                        highest = prob;
                        highestGuesses.add(g);
                    }
                }
                if (highestGuesses.size()==0) {
                    hunting = true;
                }
                else {
                    Random random = new Random();
                    int index = random.nextInt(highestGuesses.size());
                    guess = highestGuesses.get(index);
                    isGuessed[guess.row][guess.column] = true;
                    return guess;
                }
            }

        }

        if (hunting) {
            List<Guess> potentialGuesses = findHighestProbs(finalProbTable);
            Random random = new Random();
            int index = random.nextInt(potentialGuesses.size());
            guess = potentialGuesses.get(index);
            isGuessed[guess.row][guess.column] = true;
            return guess;
        }

            /**

            int prob1 = 0;
            int prob2 = 0;
            int prob3 = 0;
            int prob4 = 0;
            boolean isGuessed1 = false;
            boolean isGuessed2 = false;
            boolean isGuessed3 = false;
            boolean isGuessed4 = false;
            List<Guess> adjGuesses = new ArrayList<>();
            if (j-1>-1) {
                prob1 = finalProbTable[i][j-1];
                isGuessed1 = isGuessed[i][j-1];
            }
            if (i-1>-1) {
                prob2 = finalProbTable[i-1][j];
                isGuessed2 = isGuessed[i-1][j];
            }
            if (j+1<noCols) {
                prob3 = finalProbTable[i][j+1];
                isGuessed3 = isGuessed[i][j+1];
            }
            if (i+1<noRows) {
                prob4 = finalProbTable[i+1][j];
                isGuessed4 = isGuessed[i+1][j];
            }


            if (prob1 >= prob2 && prob1 >= prob3 && prob1 >= prob4 && !isGuessed1 && j-1>-1) {
                Guess guessAdj1 = new Guess();
                guessAdj1.row = i;
                guessAdj1.column = j-1;
                adjGuesses.add(guessAdj1);
            }
            if (prob2 >= prob1 && prob2 >= prob3 && prob2 >= prob4 && !isGuessed2 && i-1>-1) {
                Guess guessAdj2 = new Guess();
                guessAdj2.row = i-1;
                guessAdj2.column = j;
                adjGuesses.add(guessAdj2);
            }
            if (prob3 >= prob1 && prob3 >= prob2 && prob3 >= prob4 && !isGuessed3 && j+1<noCols) {
                Guess guessAdj3 = new Guess();
                guessAdj3.row = i;
                guessAdj3.column = j+1;
                adjGuesses.add(guessAdj3);
            }
            if (prob4 >= prob1 && prob4 >= prob2 && prob4 >= prob3 && !isGuessed4 && i+1<noRows) {
                Guess guessAdj4 = new Guess();
                guessAdj4.row = i+1;
                guessAdj4.column = j;
                adjGuesses.add(guessAdj4);
            }
            Random random = new Random();
            int index = random.nextInt(adjGuesses.size());
            guess = adjGuesses.get(index);
            isGuessed[guess.row][guess.column] = true;
            return guess;
        }
             */

        return guess;

    } // end of makeGuess()

    public void hunting() {

    }




    @Override
    public void update(Guess guess, Answer answer) {
        if (answer.isHit && answer.shipSunk==null) {
            hunting = false;
            prevGuess = guess;
            prevAnswer = answer;
        }
        else if (answer.isHit && answer.shipSunk!=null) {
            hunting = true;
            prevGuess = guess;
            prevAnswer = answer;
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
        else if (!answer.isHit) {
            if (prevAnswer!=null) {
                if (!prevAnswer.isHit) {
                    hunting = true;
                }
                else {
                    hunting = false;
                }
            }
        }
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
