package barrysw19.calculon.site.lichess;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@ToString
public class Game {
    @JsonProperty
    private String fullId;
    @JsonProperty
    private String gameId;
    @JsonProperty
    private String fen;
    @JsonProperty
    private String color;
    @JsonProperty
    private String lastMove;
    @JsonProperty
    private String source;
    @JsonProperty("isMyTurn")
    private Boolean myTurn;
    @JsonProperty
    private String id;
//    "status": {
//      "id": 20,
//      "name": "started"
//    },
//    "variant": {
//      "key": "standard",
//      "name": "Standard"
//    },
//    "speed": "blitz",
//    "perf": "blitz",
//    "rated": false,
//    "hasMoved": false,
//    "opponent": {
//      "id": "rockwomble",
//      "username": "RockWomble",
//      "rating": 1500
//    },
//    "secondsLeft": 60,
//    "compat": {
//      "bot": true,
//      "board": true
//    },
}
