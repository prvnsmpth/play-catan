var Game = Backbone.Model.extend({
  urlRoot: '/game',

  initialize: function() {
    var settings = {
      hexRadius: 75,
      tokenRadius: 20,
      settlementSize: 36,
      roadLength: 45,
      roadThickness: 12,
      distThreshold: 40,
      hexColors: {
        GRAIN: 'yellow',
        ORE: 'grey',
        BRICK: 'red',
        LUMBER: '#05c40f',
        WOOL: '#9bffa0'
      }
    };
    this.board = new Board({
      settings: settings,
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
   * Checks if the current player can place a settlement/city at a vertex.
   *
   * @param vertex The vertex where the player is attempting to place
   * @param buildingType Whether it is a settlement or a city.
   */
  canPlace: function(vertex, buildingType) {
    var currentPlayer = this.getCurrentPlayer();
    var building = this.board.buildingAt(vertex);
    switch (buildingType) {
      case 'SETTLEMENT':
        if (building !== null) {
          return false;
        }
        var adjBuildings = vertex.adjVertices
            .filter(v => this.board.isValidVertex(v))
            .map(v => this.board.buildingAt(v))
            .filter(building => building !== null);
        if (adjBuildings.length > 0) {
          return false;
        }
        break;
      case 'CITY':
        if (building == null ||
            building.color !== currentPlayer.color ||
            building.type !== 'SETTLEMENT') {
          return false;
        }
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
  },

  events: {
    'click .settlement-button': 'settlementButtonClicked'
  },

  settlementButtonClicked: function () {
    $('body').css({cursor: 'url(/assets/images/settlement-red.png) 10 10, auto'});
  },

  render: function() {
    var currentPlayerPos = this.model.get('currentPlayerPos');
    var player = this.model.get('players')[currentPlayerPos];
    var html = this.template(player);
    this.$('#player-controls').html(html);
    return this;
  }
});