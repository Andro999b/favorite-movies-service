package dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ModifyFavoritesListRequest {
    private String name;

    @JsonCreator
    public ModifyFavoritesListRequest(@JsonProperty("name") String name) {
        this.name = name;
    }

    public void setName(String name) {//just fo swagger :/
        this.name = name;
    }

    @ApiModelProperty(name = "name", required = true)
    public String getName() {
        return name;
    }
}
