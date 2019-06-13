package gameserver.control;

import gameserver.model.Match;
import gameserver.model.Player;
import gameserver.view.Sender;

import java.util.HashMap;
import java.util.Random;


/**
 * Controller which handles an ONGOING match, initialized by
 * the Matchmaker.
 */
class MatchController {

    private Sender sender;

    // A map of Players and the match they participate in
    private HashMap<Player, Match> playerGame = new HashMap<Player, Match>();

    MatchController(Sender sender){
        this.sender = sender;
    }


    /**
     * Sets a match as started by adding it to the
     * ongoing match list (playerGame)
     */
    void startMatch(Match match){
        playerGame.put(match.getPlayer(1), match);
        playerGame.put(match.getPlayer(2), match);

        // Randomize the initializing player
        Random rnd = new Random();
        boolean player1Starting = rnd.nextBoolean();

        sender.sendStartGame(match.getPlayer(1), player1Starting);
        sender.sendStartGame(match.getPlayer(1), !player1Starting);
    }


    /**
     * Forwards data recieved via message code 010 from a
     * Player participating in a match to the opponent
     * of that player.
     */
    void dataRecieved( Player player, String dataMsg ){
        Match match = playerGame.get(player);
        Player opponent = match.getOpponent(player);
        if(opponent != null ){
            sender.sendMessage(opponent, dataMsg );
        }
    }


    /**
     * Removes a Player from its match, if its
     * participating in one.
     *
     * Called when a player loses connection
     */
    void removePlayer(Player player){
        Match match = playerGame.get(player);

        if( match != null ){
            Player opponent = match.getOpponent(player);
            sender.sendOpponentDisconnected(opponent);
            stopMatch(match);
        }
    }


    /**
     * Stops the match, and disconnect players
     * still participating in the match.
     *
     * Is called by removePlayer()
     * @param match
     */
    private void stopMatch(Match match){
        Player player = match.getPlayer(1);
        if(player != null){
            playerGame.remove(player);
        }
        player = match.getPlayer(2);
        if( player != null){
            playerGame.remove(player);
        }
    }
}