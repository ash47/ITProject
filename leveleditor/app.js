var PNG = require('png-js');
var fs = require('fs');

// The directory to save compiled maps into
var srcDir = 'mapSRC/';
var mapDir = '../android/assets/maps/';

// The size of a screen
var levelWidth = 48;
var levelHeight = 27

// The scale we are using for this map
var levelScale = 4;

// The size of tiles
tileWidth = 1;
tileHeight = 1;

// This holds patterns for building the physics meshes
var worldPatterns = {};

// Will contain physics data
var physicsData = [];

// Will contain tile data
var tileData = [];

// Will contain the visual data for what to draw in a given position
var visualData = [];

// This will contain the pixel data
var pixelData = [];

// An array of rules to apply to build the map
var processingRules = [];

// Adds a wall
function addWall(x, y, verts) {
    // Store the wall
    physicsData.push({
        sort: 'wall',
        verts: verts,
        x: x,
        y: y
    });
}

// Add a tile
function addTile(x, y, sort) {
    // Stores tile data
    tileData.push({
        x: x,
        y: y,
        sort: sort
    });
}

// Adds a rule to the processing chain
function registerRule(rule) {
    // Store the rule
    processingRules.push(rule);
}

// Returns the color at the given position
function getColor(x, y) {
    // Grab the index
    var idx = (levelWidth * y * levelScale + x) << 2;

    // Grab the info on this pixel
    var red = pixelData[idx];
    var green = pixelData[idx+1];
    var blue = pixelData[idx+2];
    var alpha = pixelData[idx+3];

    // Contert to a color
    return toColor(red, green, blue, alpha);
}

// Takes the given colour and
function processPosition(x, y) {
    // Convert to propper coords
    var rx = x * levelScale;
    var ry = y * levelScale;

    // Apply all the processing rules
    for(var i=0; i<processingRules.length; i++) {
        processingRules[i](x, y, rx, ry);
    }
}

// Creates a hashable string
function toColor(red, green, blue, alpha) {
    return '('+red+','+green+','+blue+','+alpha+')';
}

// Creates a shape to be used with shape matching
function shape(width, height, verts) {
    this.width = width;
    this.height = height;
    this.verts = verts;
}

// Returns true if this shape can be found at the given position
shape.prototype.match = function(rx, ry, color) {
    // Check for mismatches
    for(var yy=0; yy<this.height; yy++) {
        for(var xx=0; xx<this.width; xx++) {
            if(this.verts[this.width*yy + xx] && getColor(rx+xx, ry+yy) != color) {
                return false;
            }
        }
    }

    // None found, this shape exists at the point!
    return true;
}

// Compiles the given map
function compileMap(mapName) {
    PNG.decode(srcDir+mapName+'.png', function(pixels) {
        // Store the pixel data
        pixelData = pixels;

        // Reset the physics data container
        physicsData = [];
        tileData = [];
        visualData = [];

        // Reset visual data
        for(var y=0; y<levelHeight; y++) {
            for(var x=0; x<levelWidth; x++) {
                visualData[x + y*levelWidth] = '';
            }
        }

        // Process every position
        for(var y=0; y<levelHeight; y++) {
            for(var x=0; x<levelWidth; x++) {
                // Process this position
                processPosition(x, y);
            }
        }

        // Add the dirt
        for(var y=1; y<levelHeight; y++) {
            for(var x=0; x<levelWidth; x++) {
                // Grab the image
                var image = visualData[x + y*levelWidth];

                var imageAbove = visualData[x + (y-1)*levelWidth];

                if(image == 'Tiles/grassMid' && imageAbove.indexOf('grass') != -1) {
                    if(imageAbove == 'Tiles/grassHillRight') {
                        visualData[x + y*levelWidth] = 'Tiles/grassHillRight2';
                    } else if(imageAbove == 'Tiles/grassHillLeft') {
                        visualData[x + y*levelWidth] = 'Tiles/grassHillLeft2';
                    } else {
                        visualData[x + y*levelWidth] = 'Tiles/grassCenter';
                    }
                }
            }
        }

        // Add the rounded edges
        /*for(var y=0; y<levelHeight; y++) {
            for(var x=0; x<levelWidth; x++) {
                // Grab the image
                var image = visualData[x + y*levelWidth];

                var imageAbove = visualData[x + (y-1)*levelWidth];

                if(image == 'Tiles/grassMid' && imageAbove == 'Tiles/grassMid') {
                    visualData[x + y*levelWidth] = 'Tiles/grassCenter';
                }
            }
        }*/

        // Create the map file
        mapFile = JSON.stringify({
            physicsData: physicsData,
            tileData: tileData,
            visualData: visualData
        });

        // Save the output
        fs.writeFile(mapDir+mapName+'.json', mapFile, function(err) {
            if(err) {
                console.log('Error while compiling '+mapName+':');
                console.log(err);
            } else {
                console.log(mapName+' was compiled successfully!');
            }
        });
    });
}

/*
    Useful shapes
*/

// A solid 4x4 block
var shapeBlockFull = new shape(4, 4, [
    1, 1, 1, 1,
    1, 1, 1, 1,
    1, 1, 1, 1,
    1, 1, 1, 1
]);

// A ramp that goes up and to the left
var shapeRampUpLeft = new shape(4, 4, [
    1, 0, 0, 0,
    1, 1, 0, 0,
    1, 1, 1, 0,
    1, 1, 1, 1
]);

// A ramp that goes up and to the right
var shapeRampUpRight = new shape(4, 4, [
    0, 0, 0, 1,
    0, 0, 1, 1,
    0, 1, 1, 1,
    1, 1, 1, 1
]);

// A sheep
var shapeSheep = new shape(4, 4, [
    0, 1, 1, 0,
    1, 1, 1, 1,
    1, 1, 1, 1,
    0, 1, 1, 0
]);

// A coin
var shapeCoin = new shape(4, 4, [
    0, 0, 0, 0,
    0, 1, 1, 0,
    0, 1, 1, 0,
    0, 0, 0, 0
]);

// The goal
shapeGoal = shapeBlockFull;

/*
    Register patterns
*/

// Level data builder
registerRule(function(x, y, rx, ry) {
    // The color of walls
    var wallColor = toColor(0, 0, 0, 255);

    // Check if there is a wall at the given position
    if(shapeBlockFull.match(rx, ry, wallColor)) {
        // The size of the wall
        wallWidth = tileWidth;
        wallHeight = tileHeight;

        // Add the wall
        addWall(x, y, [0, 0, wallWidth, 0, wallWidth, -wallHeight, 0, -wallHeight]);

        // Add the visual part
        visualData[x+y*levelWidth] = 'Tiles/grassMid';
    } else if(shapeRampUpLeft.match(rx, ry, wallColor)) {
        // The size of the wall
        wallWidth = tileWidth;
        wallHeight = tileHeight;

        // Add the wall
        addWall(x, y, [0, 0, wallWidth, -wallHeight, 0, -wallHeight]);

        // Add the visual part
        visualData[x+y*levelWidth] = 'Tiles/grassHillRight';
    } else if(shapeRampUpRight.match(rx, ry, wallColor)) {
        // The size of the wall
        wallWidth = tileWidth;
        wallHeight = tileHeight;

        // Add the wall
        addWall(x, y, [wallWidth, 0, wallWidth, -wallHeight, 0, -wallHeight]);

        // Add the visual part
        visualData[x+y*levelWidth] = 'Tiles/grassHillLeft';
    } else {
        // Handle raw data (hopefully they don't have too much!)

        var tw1 = tileWidth/levelScale;
        var th1 = tileHeight/levelScale;

        for(var xx=0; xx<levelScale; xx++) {
            for(var yy=0; yy<levelScale; yy++) {
                var c = getColor(rx+xx, ry+yy)

                // Check if we should create a wall here
                if(c == wallColor) {
                    addWall(x+xx*tw1, y+yy*th1, [0, 0, tw1, 0, tw1, -th1, 0, -th1]);
                }
            }
        }
    }
});

// Tile finder
registerRule(function(x, y, rx, ry) {
    // The color of things
    var sheepColor = toColor(0, 255, 0, 255);
    var goalColor = toColor(0, 255, 255, 255);

    // Check for objects at the given position
    if(shapeGoal.match(rx, ry, goalColor)) {
        // Add the goal
        addTile(x, y, 'goal');
    } else if(shapeSheep.match(rx, ry, sheepColor)) {
        // Add the sheep
        addTile(x, y, 'sheep');
    } else if(shapeCoin.match(rx, ry, sheepColor)) {
        // Add the sheep
        addTile(x, y, 'coin');
    }
});

// Compile all maps
fs.readdir(srcDir, function(err, files) {
    if(err) throw err;

    for(var i=0; i<files.length; i++) {
        // Grab the name
        var name = files[i];

        // Skip bad files
        if(name == '.') continue;
        if(name == '..') continue;

        // Ensure it is a valid map
        if(name.indexOf('.png') == name.length-4) {
            // Grab the name of this map
            mapName = name.substr(0, name.length-4);

            // Compile it
            compileMap(mapName);
        }
    }
});
