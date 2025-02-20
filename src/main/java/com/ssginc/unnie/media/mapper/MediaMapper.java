package com.ssginc.unnie.media.mapper;

import com.ssginc.unnie.media.dto.MediaRequest;
import com.ssginc.unnie.media.vo.Media;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MediaMapper {

    int insert(MediaRequest media);

}
