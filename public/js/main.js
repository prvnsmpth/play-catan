$(function () {
  var gameContainer = $("#game-container")[0];
  var canvas = $('#game-canvas')[0];

  canvas.width = document.documentElement.clientWidth;
  canvas.height = document.documentElement.clientHeight * 0.8;

  game = new Game({id: 1});
  new GameView({
    el: gameContainer,
    model: game,
    template: _.template($('#game-view-template').html())
  });
  game.fetch();
});