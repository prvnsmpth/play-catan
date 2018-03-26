var Game = Backbone.Model.extend({
  urlRoot: '/game',

  initialize: function(options) {
    this.board = new Board({
      settings: options.settings,
      parent: this
    });
  },

  parse: function(gameState) {
    if (_.has(gameState, 'board')) {
      this.board.set(gameState.board);
    }
    delete gameState.board;
    return gameState;
  },

  /**
   * Checks if the current player can place a settlement/city/road at a vertex/edge.
   *
   * @param coord The vertex/edge where the player is attempting to place
   * @param buildingType Whether it is a settlement, city or road.
   */
  canPlace: function(coords, buildingType) {
    var currentPlayer = this.getCurrentPlayer();
    switch (buildingType) {
      case 'SETTLEMENT':
        var building = this.board.buildingAt(coords);
        if (building !== null) {
          return false;
        }
        var adjBuildings = coords.adjVertices
            .filter(v => this.board.isValidVertex(v))
            .map(v => this.board.buildingAt(v))
            .filter(building => building !== null);
        if (adjBuildings.length > 0) {
          return false;
        }
        break;
      case 'CITY':
        var building = this.board.buildingAt(coords);
        if (building == null ||
            building.color !== currentPlayer.color ||
            building.type !== 'SETTLEMENT') {
          return false;
        }
        break;
      case 'ROAD':
        break;
    }
    return true;
  },

  getCurrentPlayer: function() {
    return this.get('players')[this.get('currentPlayerPos')];
  }
});

var GameView = Backbone.View.extend({
  initialize: function (options) {
    this.listenTo(this.model, 'change', this.render);
    this.board = new BoardView({
      el: this.$('#game-canvas'),
      model: this.model.board,
      parent: this
    });
    this.template = options.template;
    this.assetQueue = this.loadAssets();

    this.on('buildingPlaced', this.buildingPlaced);
  },

  loadAssets: function() {
    var assetQueue = new createjs.LoadQueue();
    var settings = this.model.get('settings');
    var colors = settings.playerColors;
    var buildingTypes = settings.buildingTypes;
    var assetManifest = [];
    colors.forEach(c => {
      buildingTypes.forEach(type => {
        assetManifest.push({
          id: `${type}-${c}`,
          src: `/assets/images/${type}-${c}.png`
        })
      });
    });
    assetQueue.loadManifest(assetManifest);
    return assetQueue;
  },

  events: {
    'click .build-button': 'buildButtonClicked',
  },

  buildButtonClicked: function (evt) {
    var button = evt.target;
    if ($(button).hasClass('active')) {
      $('.build-button').prop('disabled', false);
      $('body').css({cursor: 'auto'});
      this.board.trigger('buildCancelled');
    } else {
      var currentPlayer = this.model.getCurrentPlayer();
      var playerColor = currentPlayer.color;
      var buildingType = $(evt.target).data('role');

      var icon = this.assetQueue.getItem(buildingType + '-' + playerColor);
      $('body').css({cursor: 'url(' + icon.src + ') 10 10, auto'});
      this.board.trigger('showBuildingSelector', {
        player: currentPlayer, buildingType: buildingType
      });
      // Disable other buttons
      $('.build-button').each(function () {
        if (this !== evt.target) {
          $(this).prop('disabled', true);
        }
      });
    }
    $(button).toggleClass('active');
  },

  /**
   * Handles the event triggered after a player places a building (settlement, city or road).
   */
  buildingPlaced: function() {
    $('body').css({cursor: 'auto'});
    $('.build-button').removeClass('active').prop('disabled', false);
    this.board.render();
  },

  render: function() {
    var player = this.model.getCurrentPlayer();
    var html = this.template(player);
    this.$('#player-controls').html(html);
    return this;
  }
});