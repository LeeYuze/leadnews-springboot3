package com.leadnews.model.wemedia.dtos;

import com.leadnews.model.common.dtos.PageRequestDTO;
import lombok.Data;

/**
 * @author lihaohui
 * @date 2023/8/17
 */
@Data
public class WmMaterialDTO extends PageRequestDTO {

    /**
     * 1 收藏
     * 0 未收藏
     */
    private Integer isCollection;
}
