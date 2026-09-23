package com.campus.info.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.info.entity.BizAttachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BizAttachmentMapper extends BaseMapper<BizAttachment> {

    @Select("SELECT * FROM biz_attachment WHERE announcement_id = #{announcementId} ORDER BY create_time DESC")
    List<BizAttachment> selectByAnnouncementId(@Param("announcementId") Long announcementId);
}
