package com.ssginc.unnie.media.mapper;

import com.ssginc.unnie.media.vo.Media;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MediaMapper {

    int insert(@Param("targetType") String targetType,
                     @Param("targetId") long targetId,
                     @Param("fileUrn") String fileUrn,
                     @Param("fileOriginalName") String fileOriginalName,
                     @Param("newFileName") String newFileName);

}
