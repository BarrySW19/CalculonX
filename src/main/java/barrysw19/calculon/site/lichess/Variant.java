package barrysw19.calculon.site.lichess;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@ToString
public class Variant {
    @JsonProperty
    private String key;
    @JsonProperty
    private String name;
    @JsonProperty("short")
    private String shortName;
}
