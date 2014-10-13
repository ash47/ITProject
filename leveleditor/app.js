var PNG = require('png-js');

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
    PNG.decode('mapSRC/'+mapName+'.png', function(pixels) {
        // Store the pixel data
        pixelData = pixels;

        // Reset the physics data container
        physicsData = [];

        for(var y=0; y<levelHeight; y++) {
            for(var x=0; x<levelWidth; x++) {
                // Process this position
                processPosition(x, y);
            }
        }
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
        addWall(x, y, [0, 0, wallWidth, 0, wallWidth, wallHeight, 0, wallHeight]);
    } else if(shapeRampUpLeft.match(rx, ry, wallColor)) {
        // The size of the wall
        wallWidth = tileWidth;
        wallHeight = tileHeight;

        // Add the wall
        addWall(x, y, [0, 0, wallWidth, wallHeight, 0, wallHeight]);
    } else if(shapeRampUpRight.match(rx, ry, wallColor)) {
        // The size of the wall
        wallWidth = tileWidth;
        wallHeight = tileHeight;

        // Add the wall
        addWall(x, y, [wallWidth, 0, wallWidth, wallHeight, 0, wallHeight]);
    } else {
        // Handle raw data (hopefully they don't have too much!)

    }
});

// Compile a map
compileMap('level1');
