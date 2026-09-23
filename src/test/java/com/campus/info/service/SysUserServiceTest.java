package com.campus.info.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.info.common.vo.UserQueryVO;
import com.campus.info.common.vo.UserSaveVO;
import com.campus.info.entity.SysUser;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SysUserServiceTest {

    @Resource
    private SysUserService sysUserService;

    private static Long createdUserId;

    @Test
    @Order(1)
    @Transactional
    @Rollback
    void testCreateUser() {
        UserSaveVO vo = new UserSaveVO();
        vo.setUsername("testuser");
        vo.setPassword("123456");
        vo.setNickname("测试用户");
        vo.setEmail("test@campus.com");
        vo.setPhone("13800138000");
        vo.setStatus(1);

        SysUser user = sysUserService.createUser(vo);
        assertNotNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("测试用户", user.getNickname());
        createdUserId = user.getId();
        System.out.println("=== testCreateUser 通过: userId=" + createdUserId);
    }

    @Test
    @Order(2)
    @Transactional
    @Rollback
    void testGetUserById() {
        // 先创建一个用户
        UserSaveVO vo = new UserSaveVO();
        vo.setUsername("getuser");
        vo.setPassword("123456");
        vo.setNickname("查询测试");
        vo.setStatus(1);
        SysUser created = sysUserService.createUser(vo);

        SysUser user = sysUserService.getUserById(created.getId());
        assertNotNull(user);
        assertEquals("getuser", user.getUsername());
        System.out.println("=== testGetUserById 通过");
    }

    @Test
    @Order(3)
    @Transactional
    @Rollback
    void testUpdateUser() {
        UserSaveVO createVo = new UserSaveVO();
        createVo.setUsername("updateuser");
        createVo.setPassword("123456");
        createVo.setNickname("原始昵称");
        createVo.setStatus(1);
        SysUser created = sysUserService.createUser(createVo);

        UserSaveVO updateVo = new UserSaveVO();
        updateVo.setUsername("updateuser");
        updateVo.setNickname("新昵称");
        updateVo.setEmail("new@campus.com");
        updateVo.setStatus(1);

        SysUser updated = sysUserService.updateUser(created.getId(), updateVo);
        assertEquals("新昵称", updated.getNickname());
        assertEquals("new@campus.com", updated.getEmail());
        System.out.println("=== testUpdateUser 通过");
    }

    @Test
    @Order(4)
    @Transactional
    @Rollback
    void testDeleteUser() {
        UserSaveVO vo = new UserSaveVO();
        vo.setUsername("deleteuser");
        vo.setPassword("123456");
        vo.setStatus(1);
        SysUser created = sysUserService.createUser(vo);

        sysUserService.deleteUser(created.getId());
        assertNull(sysUserService.getById(created.getId()));
        System.out.println("=== testDeleteUser 通过");
    }

    @Test
    @Order(5)
    @Transactional
    @Rollback
    void testListUsers() {
        // 创建多个用户
        for (int i = 1; i <= 3; i++) {
            UserSaveVO vo = new UserSaveVO();
            vo.setUsername("listuser" + i);
            vo.setPassword("123456");
            vo.setNickname("列表用户" + i);
            vo.setStatus(1);
            sysUserService.createUser(vo);
        }

        UserQueryVO query = new UserQueryVO();
        query.setPageNum(1);
        query.setPageSize(10);
        Page<SysUser> page = sysUserService.listUsers(query);
        assertTrue(page.getRecords().size() >= 3);
        System.out.println("=== testListUsers 通过: 查询到 " + page.getTotal() + " 条记录");
    }

    @Test
    @Order(6)
    @Transactional
    @Rollback
    void testListUsersWithKeyword() {
        UserSaveVO vo = new UserSaveVO();
        vo.setUsername("keyworduser");
        vo.setPassword("123456");
        vo.setNickname("关键字用户");
        vo.setStatus(1);
        sysUserService.createUser(vo);

        UserQueryVO query = new UserQueryVO();
        query.setKeyword("keyword");
        Page<SysUser> page = sysUserService.listUsers(query);
        assertTrue(page.getRecords().size() >= 1);
        System.out.println("=== testListUsersWithKeyword 通过");
    }
}
