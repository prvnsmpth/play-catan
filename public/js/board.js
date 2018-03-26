var Board = (function () {

  return Backbone.Model.extend({
    initialize: function (options) {
      this.parent = options.parent;
    },

    buildingAt: function (vertex) {
      if (!this.isValidVertex(vertex)) {
        return null;
      }
      return this.get('vertices')[vertex.i][vertex.j][vertex.k].building;
    },

    roadAt: function (edge) {
      if (!this.isValidEdge(edge)) {
        return null;
      }
      return this.get('edges')[edge.i][edge.j][edge.k].road;
    },

    /**
     * Checks if the given vertex coordinates correspond to a vertex that is part of the board.
     *
     * @param vertex An object containing vertex coordinates.
     */
    isValidVertex: function (vertex) {
      var tiles = this.get('tiles');
      var validSurroundingTiles = vertex.surroundingHexCoords
          .filter(c => this.isValidHexCoord(c));
      return validSurroundingTiles.length > 0;
    },

    isValidHexCoord: function (hexCoords) {
      try {
        return this.get('tiles')[hexCoords.i][hexCoords.j] !== null;
      } catch (e) {
        return false;
      }
    },

    isValidEdge: function (edge) {
      try {
        return this.get('edges')[edge.i][edge.j][edge.k] !== null;
      } catch (e) {
        return false;
      }
    },

    canPlace: function(vertex, buildingType) {
      return this.parent.canPlace(vertex, buildingType);
    }
  });

})();

var BoardView = (function () {
  var hexes = makeArray([7, 7]);

  function makeArray(dimensions) {
    var arr = new Array(dimensions[0]);
    if (dimensions.length == 1) {
      return arr;
    }
    for (var i = 0; i < dimensions[0]; i++) {
      arr[i] = makeArray(dimensions.slice(1));
    }
    return arr;
  }

  function makeCircle(x, y, radius) {
    var circle = new createjs.Shape();
    circle.graphics.s('black').f('#fffdb5').ss(2).dc(x, y, radius);
    return circle;
  }

  function makeText(x, y, text) {
    var textObj = new createjs.Text(text, 'bold 16px Arial', 'black');
    var bounds = textObj.getBounds();
    textObj.x = x - bounds.width/2; textObj.y = y - bounds.height/2;
    return textObj;
  }

  function makeSettlement(x, y, asset) {
    var settlement = new createjs.Bitmap(asset);
    var size = settlement.getBounds().width;
    settlement.x = x - size/2;
    settlement.y = y - size/2;
    return settlement;
  }

  function makeRoad(v1, v2, asset) {
    var road = new createjs.Bitmap(asset);
    var bounds = road.getBounds();
    var length = bounds.width;
    var breadth = bounds.height;
    road.x = (v1.x + v2.x)/2;
    road.y = (v1.y + v2.y)/2;
    road.regX = length/2;
    road.regY = breadth/2;
    road.rotation = 90 + ((v1.x > v2.x) - (v1.x < v2.x)) * 60;
    return road;
  }

  function getHexUnderCursor(mouseX, mouseY) {
    for (var i = 0; i < 7; i++) {
      for (var j = 0; j < 7; j++) {
        if (!hexes[i][j].ghost) {
          var pt = hexes[i][j].globalToLocal(mouseX, mouseY);
          if (hexes[i][j].hitTest(pt.x, pt.y)) {
            return hexes[i][j];
          }
        }
      }
    }
  }

  function getNearestVertex(mouseX, mouseY) {
    var hex = getHexUnderCursor(mouseX, mouseY);
    if (hex) {
      return hex.getNearestVertex(mouseX, mouseY);
    }
  }

  function getNearestEdge(mouseX, mouseY) {
    var hex = getHexUnderCursor(mouseX, mouseY);
    if (hex) {
      return hex.getNearestEdge(mouseX, mouseY);
    }
  }

  function getHexCenterCoordinates(boardX, boardY, hexRadius, i, j) {
    var alt = Math.sqrt(3)/2 * hexRadius;
    return {
      x: boardX + (j - 3) * 2 * alt - (i % 2 == 0 ? alt : 0),
      y: boardY + (i - 3) * 3 * hexRadius/2
    };
  }

  function placeHexes(stage, boardX, boardY, boardState) {
    var settings = boardState.settings;
    var tiles = boardState.tiles;
    for (var i = 0; i < 7; i++) {
      for (var j = 0; j < 7; j++) {
        delete hexes[i][j];
        var hexCenter = getHexCenterCoordinates(boardX, boardY, settings.hexRadius, i, j);
        if (tiles[i][j]) {
          var resourceType = tiles[i][j].resourceType;
          var color = settings.hexColors[resourceType];
          var hex = new Hex({
            i: i,
            j: j,
            x: hexCenter.x,
            y: hexCenter.y,
            radius: settings.hexRadius,
            color: color
          });
          hexes[i][j] = hex;
          stage.addChild(hex);
          if (tiles[i][j]['number'] > 0) {
            var numberToken = makeCircle(hex.x, hex.y, settings.tokenRadius);
            var number = makeText(hex.x, hex.y, tiles[i][j]['number']);
            stage.addChild(numberToken);
            stage.addChild(number);
          }
        } else {
          // Make a dummy hex to help with calculations of vertices at the left edge of the board
          hexes[i][j] = new Hex({
            i: i,
            j: j,
            x: hexCenter.x,
            y: hexCenter.y,
            radius: settings.hexRadius,
            color: 'white',
            ghost: true
          });
        }
      }
    }
  }

  function placeSettlements(stage, boardState, assetQueue) {
    var settings = boardState.settings;
    var vertices = boardState.vertices;
    for (var i = 0; i < 7; i++) {
      for (var j = 0; j < 7; j++) {
        for (var k = 0; k < 2; k++) {
          var vertexObj = vertices[i][j][k]['building'];
          if (!vertexObj) continue;
          var hex = hexes[i][j];
          var vertex = {
            x: hex.x + hex.alt,
            y: hex.y + (2*k - 1) * hex.radius/2
          };
          var asset = assetQueue.getResult('SETTLEMENT-' + vertexObj.color);
          var building = makeSettlement(vertex.x, vertex.y, asset);
          stage.addChild(building);
        }
      }
    }
  }

  function placeRoads(stage, boardState, assetQueue) {
    var settings = boardState.settings;
    var edges = boardState.edges;
    var hexRadius = settings.hexRadius;
    for (var i = 0; i < 7; i++) {
      for (var j = 0; j < 7; j++) {
        for (var k = 0; k < 3; k++) {
          if (edges[i][j][k]['road']) {
            var roadObj = edges[i][j][k]['road'];
            var hex = hexes[i][j];
            var alt = hex.alt;
            var v1 = {
              x: k > 0 ? hex.x + alt : hex.x,
              y: k > 0 ? hex.y + (2*k - 3) * hexRadius/2 : hex.y - hexRadius
            };
            var v2 = {
              x: k < 2 ? hex.x + alt : hex.x,
              y: k < 2 ? hex.y + (2*k - 1) * hexRadius/2 : hex.y + hexRadius
            };
            var asset = assetQueue.getResult('ROAD-' + roadObj.color);
            var roadShape = makeRoad(v1, v2, asset);
            stage.addChild(roadShape);
          }
        }
      }
    }
  }

  function makeVertexSelector(color, assetQueue) {
    var asset = assetQueue.getResult('SETTLEMENT-' + color);
    var vertexSelector = makeSettlement(0, 0, asset);
    vertexSelector.visible = false;
    vertexSelector.alpha = 0.5;
    return vertexSelector;
  }

  function makeEdgeSelector(color, assetQueue) {
    var asset = assetQueue.getResult('ROAD-' + color);
    var edgeSelector = makeRoad({x: 0, y: 0}, {x: 0, y: 0}, asset);
    edgeSelector.visible = false;
    edgeSelector.alpha = 0.5;
    return edgeSelector;
  }

  function makeBuildingSelector(color, type, assetQueue) {
    switch (type) {
      case 'SETTLEMENT':
        return makeVertexSelector(color, assetQueue);
      case 'CITY':
        return makeCitySelector(color, assetQueue);
      case 'ROAD':
        return makeEdgeSelector(color, assetQueue);
    }
  }

  function drawBoard(stage, x, y, boardState, assetQueue) {
    placeHexes(stage, x, y, boardState);
    placeSettlements(stage, boardState, assetQueue);
    placeRoads(stage, boardState, assetQueue);
    stage.update();
  }

  return Backbone.View.extend({
    initialize: function(options) {
      this.parent = options.parent;
      this.canvas = this.el;
      this.stage = new createjs.Stage(this.canvas);
      this.stage.enableMouseOver(10);
      this.buildingSelectors = this.makeBuildingSelectors();

      // Events
      this.on('showBuildingSelector', this.showBuildingSelector);
      this.on('buildCancelled', this.buildCancelled);
      this.listenTo(this.model, 'change', this.render);
    },

    showBuildingSelector: function(ctx) {
      var player = ctx.player;
      var buildingType = ctx.buildingType;
      var settings = this.model.get('settings');

      if (!this.buildingSelectors[buildingType]) {
        this.buildingSelectors[buildingType] = {};
      }
      if (!this.buildingSelectors[buildingType][player.color]) {
        var selector = makeBuildingSelector(player.color, buildingType,
            this.parent.assetQueue);
        this.buildingSelectors[buildingType][player.color] = selector;
        this.stage.addChild(selector);
      }

      this.moveListener = this.stage.on('stagemousemove', (evt) => {
        var selector = this.buildingSelectors[buildingType][player.color];
        if (buildingType == 'SETTLEMENT') {
          var vertex = getNearestVertex(evt.stageX, evt.stageY);
          if (vertex && vertex.isWithinBounds(evt.stageX, evt.stageY, settings.distThreshold) &&
              this.model.canPlace(vertex, 'SETTLEMENT')) {
            selector.x = vertex.x - settings.settlementSize/2;
            selector.y = vertex.y - settings.settlementSize/2;
            selector.vertex = vertex;
            selector.visible = true;
          } else {
            selector.visible = false;
          }
        } else if (buildingType == 'ROAD') {
          var edge = getNearestEdge(evt.stageX, evt.stageY);
          if (edge) {
            selector.x = edge.midX;
            selector.y = edge.midY;
            selector.rotation = 90 + ((edge.v1.x > edge.v2.x) - (edge.v1.x < edge.v2.x)) * 60;
            selector.edge = edge;
            selector.visible = true;
          } else {
            selector.visible = false;
          }
        }
        this.stage.update();
      });

      this.clickListener = this.stage.on('click', () => {
        var selector = this.buildingSelectors[buildingType][player.color];
        if (selector.visible) {
          switch (buildingType) {
            case 'SETTLEMENT':
              var v = selector.vertex;
              var vertices = this.model.get('vertices');
              vertices[v.i][v.j][v.k].building = {
                type: 'SETTLEMENT',
                color: player.color
              };
              break;
            case 'ROAD':
              var e = selector.edge;
              this.model.get('edges')[e.i][e.j][e.k].road = {
                type: 'ROAD',
                color: player.color
              };
              break;
          }
          this.parent.trigger('buildingPlaced');
          this.stage.off('stagemousemove', this.moveListener);
          this.stage.off('click', this.clickListener);
        }
      });
    },

    // Called when the player aborts while trying to place a building
    buildCancelled: function (ctx) {
      this.stage.off('stagemousemove', this.moveListener);
      this.stage.off('click', this.clickListener);
    },

    makeBuildingSelectors: function () {
      var settings = this.model.get('settings');
      var colors = settings.playerColors;
      var buildingTpes = settings.buildingTypes;
      var buildingSelectors = {};
      buildingTypes.forEach(type => {
        colors.forEach(color => {
          var key = `${type}-${color}`;
          buildingSelectors[key] = makeBuildingSelector(color, type, this.parent.assetQueue);
        });
      });
      return buildingSelectors;
    },

    render: function () {
      console.log('Rendering board...');
      var board = this.model;
      var settings = board.get('settings');
      var draw = function() {
        this.stage.removeAllChildren();
        this.stage.clear();
        drawBoard(this.stage, this.canvas.width/2, this.canvas.height/2, board.toJSON(), this.parent.assetQueue);
        this.stage.update();
      };
      if (this.parent.assetQueue.loaded) {
        draw.call(this);
      } else {
        var queueLoadedListener = this.parent.assetQueue.on('complete', function () {
          draw.call(this);
          this.parent.assetQueue.off('complete', queueLoadedListener);
        }.bind(this));
      }
    }
  });
})();

