package me.sitech.apifort.domain.response.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetClientServiceRes {
    
    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("service_title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("service_path")
    private String Path;

    @JsonProperty("service_context")
    private String context;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_date")
    private Date createdDate ;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updated_date")
    private Date updatedDate;

}
