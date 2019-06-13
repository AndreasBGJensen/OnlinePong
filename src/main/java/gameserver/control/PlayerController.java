package gameserver.control;

import gameserver.model.Player;
import gameserver.view.Sender;
import java.util.ArrayList;


/**
 * Controller which handles players connected to the
 * game server.
 * It validates users via authentication (username/password)
 * and duplicate Player checks (usernameExists()), and maintains
 * information about validated users.
 *
 */
class PlayerController {

    private Sender sender;

    // Validated users
    private ArrayList<Player> players = new ArrayList<Player>();


    PlayerController(Sender sender){
        this.sender = sender;
    }


    /**
     * Validate the player and adds it to the list of validated
     * Players (players connected to the Game Server).
     *
     * @return Whether or not the player was validated
     */
    boolean addPlayer( Player player, String username, String password ){
        if( authenticatePlayer(username, password) ){

            if( !usernameExists(username)){
                players.add(player);
                return true;
            }else{
                System.out.println("Already logged in");
                sender.sendAlreadyLoggedIn(player);
            }
        }else{
            System.out.println("Wrong username password");
            sender.sendWrongUsernamePassword(player);
        }
        return false;
    }

    /**
     * Removes the player as a validated user
     */
    void removePlayer(Player player){
        players.remove(player);
    }


    /**
     * @return True if the player has been authenticated (via addPlayer())
     */
    boolean playerIsAuthenticated(Player player){
        if( players.contains(player)){
            return true;
        }else{
            sender.sendNotAuthenticated(player);
            return false;
        }
    }


    /**
     * Checks if username and password identifies a user within the
     * game database
     */
    private boolean authenticatePlayer(String username, String password){
        return true;
    }


    /**
     * Checks if a Player with a given username is already connected
     * to the game server.
     */
    private boolean usernameExists(String username){
        return false;
        // TODO: Turn this on
        /*for( Player player : players ){
            if( player.getUsername().equals(username) ){
                return true;
            }
        }
        return false;*/
    }
}