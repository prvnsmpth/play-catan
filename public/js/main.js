$(function () {
  var canvas = $('#game-canvas')[0];
  var playerControlArea = $('#player-controls')[0];

  canvas.width = document.documentElement.clientWidth;
  canvas.height = document.documentElement.clientHeight;

  $.ajax({
    url: '/state',
    type: 'GET',
    success: function(gameState) {
      Game.init(gameState);
    }
  });
});