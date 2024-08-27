package barrysw19.calculon.site.lichess;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@ToString
public class User {
    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty
    private Integer rating;
    @JsonProperty
    private String title;
    @JsonProperty
    private Boolean provisional;
    @JsonProperty
    private Boolean online;
    @JsonProperty
    private Integer lag;
}
