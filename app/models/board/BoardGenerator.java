package models.board;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import models.Resource;
import models.board.coords.TileCoords;
import models.constants.GameConstants;


public class BoardGenerator {

  public static Board generate() {
    Board board = new Board();

    List<Resource> resources = GameConstants.NUM_HEXES_PER_RESOURCE.entrySet().stream()
        .flatMap(en -> IntStream.range(0, en.getValue()).mapToObj(i -> Resource.ofType(en.getKey())))
        .collect(Collectors.toList());
    Collections.shuffle(resources);

    List<Integer> numbers = IntStream.range(2, 13)
        .mapToObj(Integer::new)
        .flatMap(i -> (Math.abs(i - 7) == 5 ? Stream.of(i) : Stream.of(i, i)))
        .filter(i -> i != 7)
        .collect(Collectors.toList());
    Collections.shuffle(numbers);

    int k = 0;
    for (int i = 0; i < 7; i++) {
      for (int j = 0; j < 7; j++) {
        if (k == 18) break;
        if (i == 3 && j == 3) continue;
        TileCoords coords = new TileCoords(i, j);
        try {
          board.placeNumber(coords, numbers.get(k));
          board.setResource(coords, resources.get(k).getType());
        } catch (InvalidCoordsException e) {
          continue;
        }
        k++;
      }
    }

    return board;
  }

}
