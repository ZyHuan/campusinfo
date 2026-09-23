package com.campus.info.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.info.common.dto.AnnouncementDetailDTO;
import com.campus.info.entity.BizAnnouncement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BizAnnouncementMapper extends BaseMapper<BizAnnouncement> {

    @Select("<script>"
            + "SELECT a.id, a.title, a.content, a.user_id, u.username, u.nickname, "
            + "a.category_id, c.name AS category_name, a.status, a.view_count, "
            + "a.create_time, a.update_time "
            + "FROM biz_announcement a "
            + "LEFT JOIN sys_user u ON a.user_id = u.id "
            + "LEFT JOIN sys_category c ON a.category_id = c.id "
            + "<where>"
            + "  <if test='keyword != null and keyword != \"\"'>"
            + "    AND a.title LIKE CONCAT('%', #{keyword}, '%')"
            + "  </if>"
            + "  <if test='categoryId != null'>"
            + "    AND a.category_id = #{categoryId}"
            + "  </if>"
            + "  <if test='userId != null'>"
            + "    AND a.user_id = #{userId}"
            + "  </if>"
            + "</where>"
            + "ORDER BY a.create_time DESC"
            + "</script>")
    Page<AnnouncementDetailDTO> selectPageWithDetails(Page<BizAnnouncement> page,
                                                      @Param("keyword") String keyword,
                                                      @Param("categoryId") Long categoryId,
                                                      @Param("userId") Long userId);

    @Select("SELECT a.id, a.title, a.content, a.user_id, u.username, u.nickname, "
            + "a.category_id, c.name AS category_name, a.status, a.view_count, "
            + "a.create_time, a.update_time "
            + "FROM biz_announcement a "
            + "LEFT JOIN sys_user u ON a.user_id = u.id "
            + "LEFT JOIN sys_category c ON a.category_id = c.id "
            + "WHERE a.id = #{id}")
    AnnouncementDetailDTO selectDetailById(@Param("id") Long id);

    @Select("SELECT a.id, a.title, a.content, a.user_id, u.username, u.nickname, "
            + "a.category_id, c.name AS category_name, a.status, a.view_count, "
            + "a.create_time, a.update_time "
            + "FROM biz_announcement a "
            + "LEFT JOIN sys_user u ON a.user_id = u.id "
            + "LEFT JOIN sys_category c ON a.category_id = c.id "
            + "WHERE a.create_time BETWEEN #{startDate} AND #{endDate} "
            + "AND a.status = 1 "
            + "ORDER BY a.create_time DESC "
            + "LIMIT 100")
    java.util.List<AnnouncementDetailDTO> selectByDateRange(@Param("startDate") String startDate,
                                                             @Param("endDate") String endDate);
}
