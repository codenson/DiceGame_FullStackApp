/**
 * Player class 
 * */
class Player {
  constructor(name) {
    this.name = name;
    this.score = 0;
    this.wins = 0;
  }
  /**
   * score getter method. 
   * @returns player score. 
   */
  getScore() {
    return this.score;
  }
  /**
   * Setter to update player score 
   * @param {} points  points to be added to player score.
   */
  updateScore(points) {
    this.score += points;
  }
  /**
   * Getter to return player wins.
   * @returns  player wins.
   */
  getWins() {
    return this.wins;
  }
  /**
   * Setter to update player wins.
   */
  updateWins() {
    this.wins += 1;
  }
}
/**
 * Plater 1. 
 */
let p1 = new Player("P1");
/**
 * Player 2.
 */
let p2 = new Player("P2");
/**
 * Tracker gets triggered when the game is started.
 */
var gamestarted = 0;

/**
 * Fetch the life time score from the server when the game is started.
 */
if (gamestarted === 0) {

  fetchLifeTimeScore().then(lifeLongScore => {
    // Update the DOM or do any processing with lifeLongScore here
    document.querySelector(".totalPoints").textContent = lifeLongScore;
  }).catch(error => {
    console.error('Error:', error);
  });

  gamestarted++;
}

/**
 * Fetch the life time score from the server.
 * @returns Player's life time score.
 */
function fetchLifeTimeScore() {
  const url = '/getScore';
  return fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
  }).then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  }).then(lifeTimeScore => {
    return lifeTimeScore.score;
  });
}

/**
 * Dice roller fucntion gets triggered when the roll button is clicked.
 * Updates the  player score and player wins and displays the score on the screen.
 * sends the score to sendScore function. 
 */
function rollDice() {
  var andomNumber1 = Math.floor(Math.random() * 6 + 1);
  image1 = "dice" + andomNumber1 + ".png";

  document.querySelector("img.img1").setAttribute("src", image1);

  var andomNumber2 = Math.floor(Math.random() * 6 + 1);
  var imageName = andomNumber2 + ".png";
  image2 = "dice" + imageName;
  console.log("image2: " + image2);

  document.querySelector("img.img2").setAttribute("src", image2);

  if (andomNumber1 > andomNumber2) {
    document.querySelector("h1").textContent = "Player 1 wins";
    p1.updateWins();
  } else if (andomNumber1 < andomNumber2) {
    document.querySelector("h1").textContent = "Player 2 wins";
    p2.updateWins();
  } else {
    document.querySelector("h1").textContent = "It is a tie";
  }
  p1.updateScore(andomNumber1);
  p2.updateScore(andomNumber2);

  document.querySelectorAll("p.wins")[0].textContent = "Wins: " + p1.wins;
  document.querySelectorAll("p.wins")[1].textContent = "Wins: " + p2.wins;
  document.querySelectorAll("p.totalPoints")[0].textContent =
    "Points: " + p1.getScore();
  document.querySelectorAll("p.totalPoints")[1].textContent =
    "Points: " + p2.getScore();
  score = andomNumber1;
  //score = 100;
  sendScore(score);

}

/**
 * Function to update the player's lifetime score on the server.
 * @param {} score Player's round score. 
 */
function sendScore(score) {
  console.log("SCore ............... : " + score);
  const url = '/saveScore'; // Replace with your Spring Boot endpoint URL

  // Data to send to the server
  const data = {
    score: score
  };

  // Fetch POST request
  fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  })
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      console.log('Score sent successfully:', data);

    })
    .catch(error => {
      console.error('Error sending score:', error);
    });
}

/**
 * funtion  to display the login page when newUser button is clicked.
 */
function newUserPage() {

  var loginFrame = document.querySelector(".login");
  loginFrame.style.display = "none";

  var logginPage = document.querySelector(".signUpFormContainer");
  logginPage.style.display = "flex";

}