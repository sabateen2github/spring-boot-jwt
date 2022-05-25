package murraco.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import murraco.model.AppUserRole;

import java.util.List;

@Data
@NoArgsConstructor
public class UserDataDTO {

    @ApiModelProperty(position = 4)
    List<AppUserRole> appUserRoles;
    @ApiModelProperty(position = 0)
    private String username;
    @ApiModelProperty(position = 1)
    private String password;
    @ApiModelProperty(position = 2)
    private String instituteId;


}
