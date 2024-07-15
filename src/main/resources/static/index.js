class Player {
  constructor(name) {
    this.name = name;
    this.score = 0;
    this.wins = 0;
  }
  getScore() {
    return this.score;
  }
  updateScore(points) {
    this.score += points;
  }
  getWins() {
    return this.wins;
  }
  updateWins() {
    this.wins += 1;
  }
}
let p1 = new Player("P1");
let p2 = new Player("P2");

var gamestarted = 0; 

if (gamestarted === 0){
//    var lifeLongScore = fetchLifeTimeScore(); 
//    document.querySelector(".totalPoints").textContent = lifeLongScore; 
//       document.querySelector(".totalPoints").textContent = "lifeLongScore"; 
//    console.log("final score : "+lifeLongScore);
    
    
     fetchLifeTimeScore().then(lifeLongScore => {
        // Update the DOM or do any processing with lifeLongScore here
        document.querySelector(".totalPoints").textContent = lifeLongScore;
        console.log("final score *************: " + lifeLongScore);
    }).catch(error => {
        console.error('Error:', error);
    });

    gamestarted ++; 
    
    
    
    
    
}
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
//function fetchLifeTimeScore(){
//    const url = '/getScore';
//    fetch(url, {
//        method: 'POST',
//        headers: {
//            'Content-Type': 'application/json'
//        },
//       
//    }).then(response => {
//        if (!response.ok) {
//            throw new Error('Network response was not ok');
//        }
//        return response.json();
//    }).then(lifeTimeScore => {
//        JSON.parse(lifeTimeScore); 
//       return  lifeTimeScore.score; 
//        
//    }).catch(error => {
//    console.error('Error:', error);
//  });
//    
//}

function rollDice() {
  var andomNumber1 = Math.floor(Math.random() * 6 + 1);
  image1 = "dice" + andomNumber1 + ".png";

  // document.querySelector(" img.img1").src = andomNumber1;

  document.querySelector("img.img1").setAttribute("src", image1);

  var andomNumber2 = Math.floor(Math.random() * 6 + 1);
  var imageName = andomNumber2+".png";
  image2 = "dice" + imageName;
  console.log("image2: "+image2 ); 

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
  //   document.querySelectorAll("totalPoints")[0].textContent =
  //     "TotalPoints: " + p1.getScore();
  document.querySelectorAll("p.totalPoints")[0].textContent =
    "Points: " + p1.getScore();
  document.querySelectorAll("p.totalPoints")[1].textContent =
    "Points: " + p2.getScore();
    sendScore(score); 
 
}

function sendScore(score) {
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
        // Handle success if needed
    })
    .catch(error => {
        console.error('Error sending score:', error);
        // Handle error
    });
}

// alert("ok"); 
alert("Hello! I am an alert box!!");
function newUserPage() {
//   var loginPage = document.getElementById("loginFormContainer");
// loginPage.style.display = "block";
      var loginFrame = document.querySelector(".login");
    loginFrame.style.display = "none";

    var logginPage  = document.querySelector(".signUpFormContainer"); 
    logginPage.style.display = "flex";




    // var box = document.querySelector(".loginBox");
    // box.style.display = "none";

    // var btn = document.querySelector(".loginBtn");
    // btn.style.display = "none";
}