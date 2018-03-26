$(function () {
  var gameContainer = $("#game-container")[0];
  var canvas = $('#game-canvas')[0];

  canvas.width = document.documentElement.clientWidth;
  canvas.height = document.documentElement.clientHeight * 0.8;

  var settings = {
    hexRadius: 75,
    tokenRadius: 20,
    settlementSize: 36,
    roadLength: 40,
    roadThickness: 20,
    distThreshold: 40,
    hexColors: {
      GRAIN: 'yellow',
      ORE: 'grey',
      BRICK: 'red',
      LUMBER: '#05c40f',
      WOOL: '#9bffa0'
    },

    playerColors: ['RED', 'BLUE', 'ORANGE', 'WHITE'],
    buildingTypes: ['SETTLEMENT', 'ROAD', 'CITY']
  };

  game = new Game({id: 1, settings: settings});
  new GameView({
    el: gameContainer,
    model: game,
    template: _.template($('#game-view-template').html())
  });
  game.fetch();
});