var Game = (function (Board) {
  return {
    init: function init(gameState) {
      Board.init(gameState['board']);
    }
  };
}(Board));