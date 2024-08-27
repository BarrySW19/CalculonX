package barrysw19.calculon.site.lichess;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@ToString
public class Event {

    @JsonProperty
    private String type;
    @JsonProperty
    private Challenge challenge;
    @JsonProperty
    private Game game;
    @JsonProperty
    private String moves;
}
