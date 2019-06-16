var connection = null;

var chosenScore = 10;


function createConnection() {

    connection = new WebSocket("ws://localhost:8080/gameserver");

    connection.onopen = function () {
        initializingMessage001();
    };

    connection.onmessage = function (event) {
        var obj = JSON.parse(event.data);
        decodeEvent(obj);
    }
}




function decodeEvent(jsonObject){

    switch (jsonObject.code) {

        case 101:
            findingGame(jsonObject);
            break;

        case 102:
            acceptGame002();
            initializeGame();
            //initialize(chosenScore);
            break;

        case 103: sendGameState103and010();
            break;

        case 10:
            gameDataUpdatedata(jsonObject);
            break;

        case 104:
            console.log("code 104 ending game");
            finishedGame(jsonObject);
            break;

        case 201:
            wrongUserNameOrPassword();
            break;

        case 202: userAlreadyLoggedIn();
            break;

        case 203: unableToAuthendizise();
            break;
        case 210:
            opponentDisconected();
            break;


    }

}


function gameDataUpdatedata(jsonObject){
    console.log(jsonObject);
    player2Movement(jsonObject.paddle);    //Update the opponent paddle
    ballMovement(jsonObject.ball);         //Update the ball
    playerScores(jsonObject.scores);       //Update the scores
    sendGameState103and010();
}


function findingGame(jsonObject){
    document.getElementById("messagesFromServer").innerHTML = "Awating an opponent.\n Estimated to wait for a game is "+jsonObject.timeEstimate+"\n\n Please wait..."
    //whileLoading(true);
}


function initializeGame(){
    startButton.style.display = 'none';
    document.getElementById("loading").innerHTML = "A game has been found...";
    canvas.style.display = 'inline';
    setupGame(chosenScore);
    animate(runGame);
}


function acceptGame002(){
    var obj = {
        "code": 002
    };
    var jsonString = JSON.stringify(obj);
    connection.send(jsonString);
}

function sendGameState103and010(){
    var gsObj = new GameStateObject(10, player1.paddle, ball, [player1.score.score, player2.score.score]);
    if(player2.score.score===chosenScore){
        console.log("check for winner");
        checkForWinner();
    }
    else{
        console.log("NO WINNER YET;");
        connection.send(JSON.stringify(gsObj));
    }


}


function opponentDisconected(){
    document.getElementById("messagesFromServer").innerHTML = "Opponent has been disconnected from the game\n You have won";
    document.getElementById("loading").innerHTML = "";
    //canvas.style.display ="none";
    //animate(endGame());
    connection.close();
}

function userAlreadyLoggedIn(){
    document.getElementById("loading").innerHTML = "You are already logged in... Please Wait"
}

function unableToAuthendizise(){
    document.getElementById("messagesFromServer").innerHTML = "Unable to make authendication\n Please try again lator";
    connection.close();
}

function wrongUserNameOrPassword(){
    document.getElementById("messagesFromServer").innerHTML = "Wrong username or password\n Please try again";
    connection.close();


}

function initializingMessage001(){
    var user = {
        "code": 1,
        "username": document.forms["loginForm"]["Username"].value,
        "password": document.forms["loginForm"]["Password"].value

    };
    console.log(user);
    connection.send(JSON.stringify(user));
    startButton.style.display = 'none';
}

function finishedGame(jsonObject){
console.log("Finishing Game");
    endGame();
    if(jsonObject.hasWon===true) {
            document.getElementById("messagesFromServer").innerHTML = "Congrats, YOU WON THE HAME!!!!";
    }else{    document.getElementById("messagesFromServer").innerHTML = "Sorry, you have lost the game:(";}

document.getElementById("loading").innerHTML ="Rating cgange is: "+ jsonObject.ratingChange+", Upp rating change:  "+jsonObject.oppRatingChange;
console.log("Should have printed");

}

function  checkForWinner(){

    if(player2.score.score === chosenScore) {
        var winner = {"code": 11};
        var jsonString = JSON.stringify(winner);
        connection.send(jsonString);
    }

}

/**
 * Updates the opponents paddle position and movement when he begins to move or stops moving.
 *
 * @param oppPaddle     The opponents paddle
 */
function player2Movement(oppPaddle) {
    if(player2.paddle.y_speed !== oppPaddle.y_speed) {
        player2.paddle.y = oppPaddle.y;
        player2.paddle.y_speed = oppPaddle.y_speed;
    }
}

/**
 *
 *
 * @param oppBall   The balls position as the player sees it. This is only updated when it is at the opponents side, to
 * secure a smooth experience when the player has to catch the ball
 */
function ballMovement(oppBall) {
    if(ball.x > 400) {
        ball.speed = oppBall.speed;
        ball.x = width - oppBall.x;
        ball.y = oppBall.y;
        ball.x_speed = -oppBall.x_speed;
        ball.y_speed = oppBall.y_speed;
    }
}

/**
 * Updates the score for the player if the opponent says either of them have a higher score. The control is to secure
 * the points don't reset/gets cancelled
 *
 * @param scores    State of the scores as opponent sees them
 */
function playerScores(scores) {
    if(scores[1] > player1.score.score || scores[0] > player2.score.score) {
        player1.score.score = scores[1];
        player2.score.score = scores[0];
    }
}