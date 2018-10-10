package player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ship.Ship;
import world.World;

/**
 * Greedy guess player (task B).
 * Please implement this class.
 *
 * @author Youhan Xia, Jeffrey Chan
 */
public class GreedyGuessPlayer  implements Player{

    public List<Guess> patternGuesses;
    public List<Guess> patternGuessesOpposite;
    public List<Guess> greedyGuesses;
    public HitType[][] hitMatrix;
    private enum HitType{
        HIT,
        MISFIRE,
        FRESH
    }

    int rowSize = 0;
    int clnSize = 0;
    boolean isHex = false;
    GreedyGuessPlayer.OwnShip[] ownShip = new GreedyGuessPlayer.OwnShip[5];
    boolean[][] isguessed;

    @Override
    public void initialisePlayer(World world) {
        patternGuesses = new ArrayList<>();
        patternGuessesOpposite = new ArrayList<>();
        greedyGuesses = new ArrayList<>();
        hitMatrix = new HitType[world.numRow][world.numColumn];
        for (HitType[] row: hitMatrix)
            Arrays.fill(row, HitType.FRESH);

        enumeratePatternGuesses(world);

        this.rowSize = world.numRow;
        this.clnSize = world.numColumn;
        this.isHex = world.isHex;
        this.isguessed = new boolean[this.rowSize][this.clnSize];
        int var2 = 0;

        for (Iterator var3 = world.shipLocations.iterator(); var3.hasNext(); ++var2) {
            World.ShipLocation var4 = (World.ShipLocation) var3.next();
            this.ownShip[var2] = new GreedyGuessPlayer.OwnShip();
            this.ownShip[var2].ship = var4.ship;

            for (int var5 = 0; var5 < this.ownShip[var2].ship.len(); ++var5) {
                this.ownShip[var2].rowCdns[var5] = (var4.coordinates.get(var5)).row;
                this.ownShip[var2].clnCdns[var5] = (var4.coordinates.get(var5)).column;
                this.ownShip[var2].isdown[var5] = false;
            }
        }
    } // end of initialisePlayer()

    private void enumeratePatternGuesses(World world) {
        for(int row = 0; row < world.numRow; ++row){
            for(int col = row%2; col < world.numColumn; col+=2){
                Guess g = new Guess();
                Guess oppositeGuess = new Guess();
                g.row = row;
                g.column = col;
                oppositeGuess.row = row +1;
                oppositeGuess.column = col;
                patternGuesses.add(g);
                if(oppositeGuess.row < world.numRow){
                    patternGuessesOpposite.add(oppositeGuess);
                }
            }
        }
    }

    @Override
    public Answer getAnswer(Guess guess) {
        // To be implemented.
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
        // ** Targeting greedy mode **

        int index;

        if(!greedyGuesses.isEmpty()){
            while (true){
                index = random.nextInt(greedyGuesses.size());
                Guess greedyGuess = greedyGuesses.get(index);
                if(hitMatrix[greedyGuess.row][greedyGuess.column] == HitType.FRESH){
                    return greedyGuesses.remove(index);
                } else {
                    greedyGuesses.remove(index);
                }
            }
        }

        if(patternGuesses.isEmpty()){
            while (true){
                index = random.nextInt(patternGuessesOpposite.size());
                Guess patternGuess = patternGuessesOpposite.get(index);
                if(hitMatrix[patternGuess.row][patternGuess.column] == HitType.FRESH){
                    return patternGuessesOpposite.remove(index);
                } else {
                    patternGuessesOpposite.remove(index);
                }
            }
        }

        while(true){
            index = random.nextInt(patternGuesses.size());
            Guess patternGuess = patternGuesses.get(index);
            if(hitMatrix[patternGuess.row][patternGuess.column] == HitType.FRESH){
                break;
            }
            else patternGuesses.remove(index);
        }

        return patternGuesses.remove(index);
    } // end of makeGuess()


    @Override
    public void update(Guess guess, Answer answer) {
        // To be implemented.
        hitMatrix[guess.row][guess.column] = answer.isHit ? HitType.HIT : HitType.MISFIRE;

        if(answer.isHit){
            greedyGuesses.addAll(
                    Stream.of(
                            CreateGuess(guess.row + 1, guess.column),
                            CreateGuess(guess.row - 1, guess.column),
                            CreateGuess(guess.row, guess.column + 1),
                            CreateGuess(guess.row, guess.column - 1)
                    ).filter(newGuess -> {
                        if(newGuess.row < 0 || newGuess.row > this.rowSize - 1){
                            return false;
                        }

                        if(newGuess.column < 0 || newGuess.column > this.clnSize - 1){
                            return false;
                        }

                        return hitMatrix[newGuess.row][newGuess.column] == HitType.FRESH;
                    }).collect(Collectors.toList())
            );
        }
    } // end of update()


    @Override
    public boolean noRemainingShips() {
        // To be implemented.
        for(int var1 = 0; var1 < 5; ++var1) {
            for(int var2 = 0; var2 < this.ownShip[var1].ship.len(); ++var2) {
                if(!this.ownShip[var1].isdown[var2]) {
                    return false;
                }
            }
        }
        return true;
        // dummy return
    } // end of noRemainingShips()

    private Guess CreateGuess(int row, int column){
        Guess newGuess = new Guess();
        newGuess.row = row;
        newGuess.column = column;
        return newGuess;
    }

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

} // end of class GreedyGuessPlayer
