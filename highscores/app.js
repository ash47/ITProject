var express = require('express');
var app = express();
var httpServer = require('http').Server(app);
var fs = require('fs');

// Max number of highscores to store per map
var maxHighScores = 5;

app.get('/highscores/:mapName', function(req, res) {
    // Grab the mapName
    var mapName = req.params.mapName;

    // Ensure mapName only has letters and numbers!

    // Attempt to load the data
    try {
        var data = require('./data/'+mapName+'.json');

        // Send the data
        res.json(data);
    } catch(e) {
        console.log(e);
        // Send failure
        res.json({failure: true});
    }
});

app.get('/highscores/:mapName/:user/:highscore', function(req, res) {
    // Grab the data
    var mapName = req.params.mapName;
    var username = req.params.user;
    var highscore = parseInt(req.params.highscore);

    // Ensure mapName only has letters and numbers!

    // Attempt to load the data
    var data;
    try {
        // Load data
        data = require('./data/'+mapName+'.json');
    } catch(e) {
        // No data exists, create it
        data = {
            scores: []
        };
    }

    // Check if our highscore is larger
    var insertPos = data.scores.length;
    for(var i=0; i<data.scores.length; i++) {
        if(data.scores[i] == null || highscore > data.scores[i].score) {
            insertPos = i;
            break;
        }
    }

    // Do we insert?
    if(insertPos < maxHighScores) {
        // Copy other scores down
        for(var i=maxHighScores-2; i>= insertPos; i--) {
            data.scores[i+1] = data.scores[i];
        }

        // Insert new score
        data.scores[insertPos] = {
            user: username,
            score: highscore
        }

        // Save the new file
        fs.writeFile('./data/'+mapName+'.json', JSON.stringify(data), function(err) {
            if(err) {
                console.log(err);

                // Report failure
                res.json({failure: true});
            } else {
                // Report new scores
                res.json(data);
            }
        });
    } else {
        // Just report the scores
        res.json(data);
    }
});

httpServer.listen(80, function() {
    console.log('listening on *:80');
});
