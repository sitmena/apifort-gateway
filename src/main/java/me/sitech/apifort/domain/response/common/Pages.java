package me.sitech.apifort.domain.response.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pages <T>{


    @JsonProperty("total")
    private long total;

    @JsonProperty("page_index")
    private long pageIndex;

    @JsonProperty("page_size")
    private long pageSize;

    @JsonProperty("data")
    List<T> t;
}
