package com.campus.info.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 公开接口
                .antMatchers("/api/auth/**").permitAll()
                // 个人中心：所有认证用户可访问
                .antMatchers("/api/profile/**").authenticated()
                // 公告管理：增删改 仅 ADMIN（浏览量递增对所有认证用户开放）
                .antMatchers(HttpMethod.PUT, "/api/announcement/*/view").authenticated()
                .antMatchers(HttpMethod.POST, "/api/announcement/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/announcement/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/announcement/**").hasRole("ADMIN")
                // 分类管理：增删改 仅 ADMIN
                .antMatchers(HttpMethod.POST, "/api/category/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/category/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/category/**").hasRole("ADMIN")
                // 用户管理：增删改 仅 ADMIN
                .antMatchers(HttpMethod.POST, "/api/user/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/user/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/user/**").hasRole("ADMIN")
                // AI 接口：仅 ADMIN
                .antMatchers("/api/ai/**").hasRole("ADMIN")
                // 附件：删除仅 ADMIN
                .antMatchers(HttpMethod.DELETE, "/api/attachment/**").hasRole("ADMIN")
                // 其余 API 需认证
                .antMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
