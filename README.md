# minesweeper
Martín Rodriguez Technical Challenge

The REST API is available in an SpringBoot Server running in an AWS EC2 Instance:
URL:
http://ec2-174-129-114-7.compute-1.amazonaws.com:8080

PostMan collection provided in
MineSweeper.postman_collection.json file

A {{host}} variable is set in the collection to test the api in local or in the ec2



SpringBoot was used to provide the REST Endpoints for simplicity puroposes
H2 Database is used for persistency, also for simplicity reasons

The game is playable with the use of 8 endpoints

#Create a new user
/createUser
Create a user with a userId (Key) , name (String) and lastName (String)
POST BODY:
{
    "userId":"cobian",
    "name":"Martín",
    "lastName":"Cobián"
}

Once a user is created a newGame can be accessed
/newGame
Receives rows,cols,mines,user in the POST BODY as a JSON
POST BODY:
{
    "rows":5,
    "cols":5,
    "mines":1,
    "user":"cobian"
}

Response:
{
    "message": "rog8oIkJGi",
    "code": 200
}

In message the "gameId" value to use to play the newly created game

#DisplayBoard
Displays the full NOT USER VIEWABLE board, containing the mines,adjacent mines counter, covered cells, flags, question and uncovered cells.

/displayBoard?gameId=[gameId from createGame]&userId=[userId sent in createUser]
Response
Human readable board
,,,,
,,,,
,,1,1,1
,,1,CM,C1
,,1,C1,C1

#Full Game Info
Find info of the given game.
The board given is the "x" axis representation of the x,y human readable board.
/fullGameInfo?gameId=[gameId]&userId=[userId]

Response
{
    "cols": 5,
    "rows": 5,
    "mines": 1,
    "id": "rog8oIkJGi",
    "user": "cobian",
    "playable": true,
    "board": [
        "C",
        "C",
        "C1",
        "C1",
        "C1",
        "C",
        "C",
        "C1",
        "CM",
        "C1",
        "C",
        "C",
        "C1",
        "C1",
        "C1",
        "C",
        "C",
        "C",
        "C",
        "C",
        "C",
        "C",
        "C",
        "C",
        "C"
    ],
    "elapsedTime": 0
}

#Playable Board
The board showing only Covered,Question,Cleared,Flag,Adjacent counter, Mine cells.
Can be shown to the user.
/playableBoard?gameId=[gameId]&userId=[userId]

Response:
C,C,C,C,C
C,C,C,C,C
C,C,C,C,C
C,C,C,C,C
C,C,C,C,C


#Make move
/makeMove
Make a "clear" move on x,y position
PostBody:
{
    "userId":"userId",
    "gameId":"gameId",
    "x":int x position,
    "y":int y position
}

Rules
-if a Mine is found, user looses
-If adjacent mine counter is found, it shows it
-If nothing is found, clears the cell and all nearby cells until finding an adjacent counter
-If question or flag are set, nothing happens
-If all mines have been marked or remain covered with no more freeable cells, user wins
-Only valid x,y positions apply
-Once cleared, a position cannot be replayed

Sample response The result of the last move in human readable form, used to play the game with the REST API, response should change when used by another app:
 , , , , 
 , , , , 
 , ,1,1,1
 , ,1,C,C
 , ,1,C,C

Meaning there are clear cells and some pending mines to find

FLAG
Put a flag in given position
/markFlag

PostBody:
{
    "userId":"userId",
    "gameId":"gameId",
    "x":int x position,
    "y":int y position
}

Sample response: The result of the flag setting in human readable form, used to play the game with the REST API, response should change when used by another app:
 , , , , 
 , , , , 
 , ,1,1,1
 , ,1,F,C
 , ,1,C,C

Meaning a flag is set on x=3,y=3

Note: When a flag is set, a Question or another flag in the same position will remove it.

Question
Put a Question in given position
/markQuestion

PostBody:
{
    "userId":"userId",
    "gameId":"gameId",
    "x":int x position,
    "y":int y position
}

Sample response: The result of the flag setting in human readable form, used to play the game with the REST API, response should change when used by another app:
 , , , , 
 , , , , 
 , ,1,1,1
 , ,1,Q,C
 , ,1,C,C

Meaning a Question is set on x=3,y=3

Note: When a question is set, a Question or another flag in the same position will remove it.

Once a game is lost it cannot be replayed.

#The elapsed time counter starts when the game is created and changes when a move is made. When a game is ended (win or loose) the elapsed time is shown in the response.












