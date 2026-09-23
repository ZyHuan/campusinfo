package com.campus.info.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.info.common.dto.AnnouncementDetailDTO;
import com.campus.info.common.vo.AnnouncementQueryVO;
import com.campus.info.common.vo.AnnouncementSaveVO;
import com.campus.info.common.vo.UserSaveVO;
import com.campus.info.entity.BizAnnouncement;
import com.campus.info.entity.SysCategory;
import com.campus.info.entity.SysUser;
import com.campus.info.mapper.SysCategoryMapper;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BizAnnouncementServiceTest {

    @Resource
    private BizAnnouncementService bizAnnouncementService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysCategoryMapper sysCategoryMapper;

    private static Long testUserId;
    private static Long testCategoryId;
    private static Long createdAnnouncementId;

    /**
     * 准备测试数据：创建用户和分类
     */
    private void prepareTestData() {
        // 创建测试用户
        UserSaveVO userVo = new UserSaveVO();
        userVo.setUsername("author");
        userVo.setPassword("123456");
        userVo.setNickname("公告作者");
        userVo.setStatus(1);
        SysUser user = sysUserService.createUser(userVo);
        testUserId = user.getId();

        // 创建测试分类
        SysCategory category = new SysCategory();
        category.setName("校园通知");
        category.setDescription("校园相关通知公告");
        category.setSortOrder(1);
        category.setStatus(1);
        sysCategoryMapper.insert(category);
        testCategoryId = category.getId();
    }

    @Test
    @Order(1)
    @Transactional
    @Rollback
    void testCreateAnnouncement() {
        prepareTestData();

        AnnouncementSaveVO vo = new AnnouncementSaveVO();
        vo.setTitle("关于期末考试安排的通知");
        vo.setContent("期末考试将于2026年6月15日开始，请同学们做好准备。");
        vo.setUserId(testUserId);
        vo.setCategoryId(testCategoryId);
        vo.setStatus(1);

        BizAnnouncement announcement = bizAnnouncementService.createAnnouncement(vo);
        assertNotNull(announcement.getId());
        assertEquals("关于期末考试安排的通知", announcement.getTitle());
        assertEquals(testUserId, announcement.getUserId());
        assertEquals(testCategoryId, announcement.getCategoryId());
        createdAnnouncementId = announcement.getId();
        System.out.println("=== testCreateAnnouncement 通过: announcementId=" + createdAnnouncementId);
    }

    @Test
    @Order(2)
    @Transactional
    @Rollback
    void testGetAnnouncementDetailWithJoin() {
        prepareTestData();

        // 创建公告
        AnnouncementSaveVO vo = new AnnouncementSaveVO();
        vo.setTitle("三表联查测试公告");
        vo.setContent("验证LEFT JOIN查询用户和分类信息");
        vo.setUserId(testUserId);
        vo.setCategoryId(testCategoryId);
        vo.setStatus(1);
        BizAnnouncement created = bizAnnouncementService.createAnnouncement(vo);

        // 查询详情 — 此处执行三表联查
        AnnouncementDetailDTO detail = bizAnnouncementService.getAnnouncementDetail(created.getId());
        assertNotNull(detail);
        assertEquals("三表联查测试公告", detail.getTitle());
        assertEquals(testUserId, detail.getUserId());
        // 验证联查结果：用户名字段有值
        assertEquals("author", detail.getUsername());
        // 验证联查结果：分类名称字段有值
        assertEquals("校园通知", detail.getCategoryName());
        System.out.println("=== testGetAnnouncementDetailWithJoin 通过");
        System.out.println("    公告标题: " + detail.getTitle());
        System.out.println("    发布用户: " + detail.getUsername());
        System.out.println("    所属分类: " + detail.getCategoryName());
    }

    @Test
    @Order(3)
    @Transactional
    @Rollback
    void testUpdateAnnouncement() {
        prepareTestData();

        AnnouncementSaveVO createVo = new AnnouncementSaveVO();
        createVo.setTitle("原始标题");
        createVo.setContent("原始内容");
        createVo.setUserId(testUserId);
        createVo.setCategoryId(testCategoryId);
        createVo.setStatus(1);
        BizAnnouncement created = bizAnnouncementService.createAnnouncement(createVo);

        AnnouncementSaveVO updateVo = new AnnouncementSaveVO();
        updateVo.setTitle("修改后的标题");
        updateVo.setContent("修改后的内容");
        updateVo.setUserId(testUserId);
        updateVo.setCategoryId(testCategoryId);
        updateVo.setStatus(1);

        BizAnnouncement updated = bizAnnouncementService.updateAnnouncement(created.getId(), updateVo);
        assertEquals("修改后的标题", updated.getTitle());
        assertEquals("修改后的内容", updated.getContent());
        System.out.println("=== testUpdateAnnouncement 通过");
    }

    @Test
    @Order(4)
    @Transactional
    @Rollback
    void testDeleteAnnouncement() {
        prepareTestData();

        AnnouncementSaveVO vo = new AnnouncementSaveVO();
        vo.setTitle("待删除公告");
        vo.setContent("这条公告将被删除");
        vo.setUserId(testUserId);
        vo.setCategoryId(testCategoryId);
        vo.setStatus(1);
        BizAnnouncement created = bizAnnouncementService.createAnnouncement(vo);

        bizAnnouncementService.deleteAnnouncement(created.getId());
        assertNull(bizAnnouncementService.getById(created.getId()));
        System.out.println("=== testDeleteAnnouncement 通过");
    }

    @Test
    @Order(5)
    @Transactional
    @Rollback
    void testListAnnouncementsWithJoin() {
        prepareTestData();

        // 创建多条公告
        for (int i = 1; i <= 3; i++) {
            AnnouncementSaveVO vo = new AnnouncementSaveVO();
            vo.setTitle("联查列表测试公告" + i);
            vo.setContent("内容" + i);
            vo.setUserId(testUserId);
            vo.setCategoryId(testCategoryId);
            vo.setStatus(1);
            bizAnnouncementService.createAnnouncement(vo);
        }

        // 分页查询 — 此处执行三表联查
        AnnouncementQueryVO query = new AnnouncementQueryVO();
        query.setPageNum(1);
        query.setPageSize(10);
        query.setUserId(testUserId);
        Page<AnnouncementDetailDTO> page = bizAnnouncementService.listAnnouncements(query);
        assertTrue(page.getRecords().size() >= 3);

        // 验证每条记录的联查字段都有值
        for (AnnouncementDetailDTO dto : page.getRecords()) {
            assertNotNull(dto.getUsername());
            assertNotNull(dto.getCategoryName());
        }
        System.out.println("=== testListAnnouncementsWithJoin 通过");
        System.out.println("    总记录数: " + page.getTotal());
        page.getRecords().forEach(dto ->
                System.out.println("    [" + dto.getId() + "] " + dto.getTitle()
                        + " | 作者: " + dto.getUsername()
                        + " | 分类: " + dto.getCategoryName())
        );
    }

    @Test
    @Order(6)
    @Transactional
    @Rollback
    void testListAnnouncementsFilterByCategory() {
        prepareTestData();

        // 创建第二个分类
        SysCategory category2 = new SysCategory();
        category2.setName("学术讲座");
        category2.setDescription("学术相关讲座");
        category2.setSortOrder(2);
        category2.setStatus(1);
        sysCategoryMapper.insert(category2);

        // 在不同分类下创建公告
        AnnouncementSaveVO vo1 = new AnnouncementSaveVO();
        vo1.setTitle("通知类公告");
        vo1.setContent("属于校园通知分类");
        vo1.setUserId(testUserId);
        vo1.setCategoryId(testCategoryId);
        vo1.setStatus(1);
        bizAnnouncementService.createAnnouncement(vo1);

        AnnouncementSaveVO vo2 = new AnnouncementSaveVO();
        vo2.setTitle("讲座类公告");
        vo2.setContent("属于学术讲座分类");
        vo2.setUserId(testUserId);
        vo2.setCategoryId(category2.getId());
        vo2.setStatus(1);
        bizAnnouncementService.createAnnouncement(vo2);

        // 按分类筛选查询
        AnnouncementQueryVO query = new AnnouncementQueryVO();
        query.setCategoryId(testCategoryId);
        Page<AnnouncementDetailDTO> page = bizAnnouncementService.listAnnouncements(query);
        for (AnnouncementDetailDTO dto : page.getRecords()) {
            assertEquals("校园通知", dto.getCategoryName());
        }
        System.out.println("=== testListAnnouncementsFilterByCategory 通过: 筛选到 " + page.getTotal() + " 条");
    }
}
