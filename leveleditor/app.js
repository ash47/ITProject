var PNG = require('png-js');
var fs = require('fs');

// The directory to save compiled maps into
var srcDir = './mapSRC/';
var mapDir = './../android/assets/maps/';
var mapDir2 = './../desktop/bin/maps/';

// The size of a screen
var levelWidth = 48;
var levelHeight = 27

// The scale we are using for this map
var levelScale = 4;

// The size of tiles
tileWidth = 1;
tileHeight = 1;

// Creates a hashable string
function toColor(red, green, blue, alpha) {
    return '('+red+','+green+','+blue+','+alpha+')';
}

function compiler() {
    // Grab a reference to the compiler
    var me = this;

    // This holds patterns for building the physics meshes
    this.worldPatterns = {};

    // Will contain physics data
    this.physicsData = [];

    // Will contain tile data
    this.tileData = [];

    // Will contain the visual data for what to draw in a given position
    this.visualData = [];

    // This will contain the pixel data
    this.pixelData = [];

    // An array of rules to apply to build the map
    this.processingRules = [];

    // Settings for the loaded map
    this.mapSettings = {};

    // Adds a wall
    function addWall(x, y, verts) {
        // Store the wall
        me.physicsData.push({
            sort: 'wall',
            verts: verts,
            x: x,
            y: y
        });
    }

    // Add a tile
    function addTile(x, y, sort, data) {
        var toAdd = {
            sort: sort
        };

        // Check if there is data
        if(data != null) {
            // Copy the data in
            for(var key in data) {
                toAdd[key] = data[key];
            }
        }

        // Store coords
        toAdd.x = x;
        toAdd.y = y;

        // Stores tile data
        me.tileData.push(toAdd);
    }

    // Adds a rule to the processing chain
    function registerRule(rule) {
        // Store the rule
        me.processingRules.push(rule);
    }

    // Returns the color at the given position
    function getColor(x, y) {
        // Grab the index
        var idx = (levelWidth * y * levelScale + x) << 2;

        // Grab the info on this pixel
        var red = me.pixelData[idx];
        var green = me.pixelData[idx+1];
        var blue = me.pixelData[idx+2];
        var alpha = me.pixelData[idx+3];

        // Contert to a color
        return toColor(red, green, blue, alpha);
    }

    // Takes the given colour and
    function processPosition(x, y) {
        // Convert to propper coords
        var rx = x * levelScale;
        var ry = y * levelScale;

        // Apply all the processing rules
        for(var i=0; i<me.processingRules.length; i++) {
            me.processingRules[i](x, y, rx, ry);
        }
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
    this.compileMap = function(mapName, callback) {
        // Check if the map even exists
        if(!fs.existsSync(srcDir+mapName+'.png')) {
            callback('does not exist');
            return;
        }

        PNG.decode(srcDir+mapName+'.png', function(pixels) {
            // Store the pixel data
            me.pixelData = pixels;

            // Attempt to load map settings
            me.mapSettings = {};
            if(fs.existsSync(srcDir+mapName+'.json')) {
                me.mapSettings = require(srcDir+mapName+'.json');
            }

            // Ensure fields exist
            me.mapSettings.mapTitle = me.mapSettings.mapTitle || 'Untitled Map';
            me.mapSettings.sheepToWin = me.mapSettings.sheepToWin || 1;
            me.mapSettings.interactiveObjects = me.mapSettings.interactiveObjects || [];

            // Reset the physics data container
            me.physicsData = [];
            me.tileData = [];
            me.visualData = [];

            // Reset visual data
            for(var y=0; y<levelHeight; y++) {
                for(var x=0; x<levelWidth; x++) {
                    me.visualData[x + y*levelWidth] = '';
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
                    var image = me.visualData[x + y*levelWidth];

                    var imageAbove = me.visualData[x + (y-1)*levelWidth];

                    if(image == 'Tiles/grassMid' && imageAbove.indexOf('grass') != -1) {
                        if(imageAbove == 'Tiles/grassHillRight') {
                            me.visualData[x + y*levelWidth] = 'Tiles/grassHillRight2';
                        } else if(imageAbove == 'Tiles/grassHillLeft') {
                            me.visualData[x + y*levelWidth] = 'Tiles/grassHillLeft2';
                        } else {
                            me.visualData[x + y*levelWidth] = 'Tiles/grassCenter';
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

            // Save Map Settings
            fs.writeFile(srcDir+mapName+'.json', JSON.stringify(me.mapSettings), function(err) {
                if(err) {
                    console.log('Error while saving settings '+mapName+':');
                    console.log(err);
                } else {
                    console.log(mapName+' settings were saved!');
                }
            });

            // Return the data
            callback(null, {
                screen: {
                    physicsData: me.physicsData,
                    tileData: me.tileData,
                    visualData: me.visualData
                }, settings: {
                    mapName: me.mapSettings.mapName,
                    sheepToWin: me.mapSettings.sheepToWin,
                    mapTitle: me.mapSettings.mapTitle
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
            me.visualData[x+y*levelWidth] = 'Tiles/grassMid';
        } else if(shapeRampUpLeft.match(rx, ry, wallColor)) {
            // The size of the wall
            wallWidth = tileWidth;
            wallHeight = tileHeight;

            // Add the wall
            addWall(x, y, [0, 0, wallWidth, -wallHeight, 0, -wallHeight]);

            // Add the visual part
            me.visualData[x+y*levelWidth] = 'Tiles/grassHillRight';
        } else if(shapeRampUpRight.match(rx, ry, wallColor)) {
            // The size of the wall
            wallWidth = tileWidth;
            wallHeight = tileHeight;

            // Add the wall
            addWall(x, y, [wallWidth, 0, wallWidth, -wallHeight, 0, -wallHeight]);

            // Add the visual part
            me.visualData[x+y*levelWidth] = 'Tiles/grassHillLeft';
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
        var coinColor = toColor(255, 255, 0, 255);
        var interactiveColor = toColor(255, 0, 0, 255);

        // Check for objects at the given position
        if(shapeGoal.match(rx, ry, goalColor)) {
            // Add the goal
            addTile(x, y, 'goal');
        } else if(shapeSheep.match(rx, ry, sheepColor)) {
            // Add the sheep
            addTile(x, y, 'sheep');
        } else if(shapeCoin.match(rx, ry, coinColor)) {
            // Add the sheep
            addTile(x, y, 'coin');
        } else if(shapeBlockFull.match(rx, ry, interactiveColor)) {
            // Grab settings for this object
            var settings = {
                sort: 'ramp',
                width: 1,
                height: 1,
                x: x,
                y: y,
                shiftX: 0,
                shiftY: 0,
                originX: 0,
                originY: 0,
                initialAngle: 0,
                finalAngle: 3.14,
                clockwise: true
            };

            var found = null;

            for(var key in me.mapSettings.interactiveObjects) {
                var io = me.mapSettings.interactiveObjects[key];

                // Is this the one we are looking at?
                if(io.x == x && io.y == y) {
                    // Found it
                    found = key;

                    // Yep, copy over settings
                    for(var kk in io) {
                        settings[kk] = io[kk];
                    }

                    break;
                }
            }

            // Update the settings file
            if(found != null) {
                me.mapSettings.interactiveObjects[found] = settings;
            } else {
                me.mapSettings.interactiveObjects.push(settings);
            }

            // Add the sheep
            addTile(x+settings.shiftX, y+settings.shiftY, 'interactive', settings);
        }
    });
}

// Compile all maps
fs.readdir(srcDir, function(err, files) {
    if(err) throw err;

    for(var i=0; i<files.length; i++) {
        // Grab the name
        var name = files[i];

        // Skip bad files
        if(name == '.') continue;
        if(name == '..') continue;

        // No underscores
        if(name.indexOf('_') != -1) continue;

        // Ensure it is a valid map
        if(name.indexOf('.png') == name.length-4) {
            (function() {
                // Grab the name of this map
                var mapName = name.substr(0, name.length-4);

                // Create a new compiler
                var cmp = new compiler();

                // Compile it
                cmp.compileMap(mapName, function(err, data) {
                    // Store compiled data
                    var output = data.settings;
                    output[1] = data.screen;

                    var kk = 2;
                    var screensNeeded = 1;

                    function next(err, data) {
                        if(err) {
                            // Store how many screens were used
                            output.totalScreens = screensNeeded;

                            // Create the map file
                            mapFile = JSON.stringify(output);

                            // Save the output
                            fs.writeFile(mapDir+mapName+'.json', mapFile, function(err) {
                                if(err) {
                                    console.log('Error while compiling '+mapName+':');
                                    console.log(err);
                                } else {
                                    console.log(mapName+' was compiled successfully!');
                                }
                            });

                            // Copy to desktop output
                            fs.writeFile(mapDir2+mapName+'.json', mapFile, function(err) {
                                if(err) {
                                    console.log('Error while compiling (2) '+mapName+':');
                                    console.log(err);
                                } else {
                                    console.log(mapName+' was compiled (2) successfully!');
                                }
                            });
                        } else {
                            // Store the data
                            output[kk++] = data.screen;

                            // We used one more screen
                            screensNeeded++;

                            // Compile next screen
                            cmp.compileMap(mapName+'_'+kk, next);
                        }
                    }

                    // Begin compile loop
                    cmp.compileMap(mapName+'_2', next);
                });
            })();
        }
    }
});
