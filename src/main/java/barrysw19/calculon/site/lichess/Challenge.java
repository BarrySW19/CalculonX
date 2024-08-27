package barrysw19.calculon.site.lichess;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@ToString
public class Challenge {

    @JsonProperty
    private String id;
    @JsonProperty
    private String url;
    @JsonProperty
    private String status;
    @JsonProperty
    private User challenger;
    @JsonProperty
    private User destUser;
    @JsonProperty
    private Variant variant;
    @JsonProperty
    private Boolean rated;
    @JsonProperty
    private String speed;
    @JsonProperty
    private TimeControl timeControl;
    @JsonProperty
    private String color;
    @JsonProperty
    private String finalColor;
}
