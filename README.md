Dicee Game
Dicee Game is a simple web-based game where two players roll dice and compete to see who rolls the higher number. The game keeps track of each player's wins and total points scored.

Technologies Used
HTML5
CSS3
JavaScript (ES6+)
Spring Boot (Java)
Features
Gameplay: Players click the "Play" button to roll their dice.
Player Stats: Displays each player's name, current wins, and total points.
Score Saving: Automatically saves and retrieves scores using a Spring Boot backend.
Responsive Design: Designed to work well on both desktop and mobile browsers.
How to Play
Click Play: Click the "Play" button to start rolling the dice.
Roll Dice: Each player's dice roll result is displayed.
Determine Winner: The player with the higher dice roll wins. In case of a tie, it's declared as such.
Score Update: Scores are updated in real-time for both players.
Lifetime Score: Displays the lifetime score of the current user fetched from the backend.
Setup Instructions
Clone the repository:
bash
Copy code
git clone https://github.com/yourusername/dicee-game.git
Navigate to the project directory.
Open index.html in your web browser to play the game locally.
Backend Configuration (Spring Boot)
The backend is powered by Spring Boot, providing REST endpoints for score saving and retrieval.
Ensure you have Java and Maven installed.
Update the userDataPath property in application.properties to specify the directory for storing user data.
Contributors
Your Name: Developer
Your Email: developer@example.com
License
This project is licensed under the MIT License - see the LICENSE file for details.