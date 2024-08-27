package barrysw19.calculon.site.lichess;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@ToString
public class TimeControl {
    @JsonProperty
    private String type;
    @JsonProperty
    private Integer limit;
    @JsonProperty
    private Integer increment;
    @JsonProperty
    private String show;
}
