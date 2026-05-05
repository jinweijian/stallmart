package com.stallmart.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PersistenceMigrationTest {

    private final JdbcTemplate jdbcTemplate;

    PersistenceMigrationTest(@Autowired DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    void shouldApplyFlywayMigrationsAndSeedDemoData() {
        Integer styleCount = jdbcTemplate.queryForObject(
                "select count(*) from store_style where code = 'forestFruitTeaCrayon'",
                Integer.class
        );
        Integer storeCount = jdbcTemplate.queryForObject(
                "select count(*) from store where app_id = 'wx-stallmart-demo'",
                Integer.class
        );
        String passwordHash = jdbcTemplate.queryForObject(
                "select password_hash from admin_account where account = 'vendor'",
                String.class
        );

        assertThat(styleCount).isEqualTo(1);
        assertThat(storeCount).isEqualTo(1);
        assertThat(passwordHash).startsWith("$2");
        assertThat(passwordHash).doesNotContain("vendor123");
    }
}
