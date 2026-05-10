package com.dinesh.portfolio.repository.impl;

import com.dinesh.portfolio.entity.ContactUs;
import com.dinesh.portfolio.exception.DatabaseOperationException;
import com.dinesh.portfolio.repository.ContactUsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.slf4j.MDC;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Repository
public class ContactUsRepositoryImpl implements ContactUsRepository {

    private static final String INSERT_QUERY = """
            INSERT INTO contact_us
            (name, email, subject, message, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String SELECT_ALL_QUERY = """
            SELECT id, name, email, subject, message, created_at, updated_at
            FROM contact_us
            ORDER BY id DESC
            """;

    private static final String SELECT_BY_ID_QUERY = """
            SELECT id, name, email, subject, message, created_at, updated_at
            FROM contact_us
            WHERE id = ?
            """;

    private static final String DELETE_BY_ID_QUERY = """
            DELETE FROM contact_us
            WHERE id = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<ContactUs> contactUsRowMapper = (rs, rowNum) -> ContactUs.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .email(rs.getString("email"))
            .subject(rs.getString("subject"))
            .message(rs.getString("message"))
            .createdAt(rs.getTimestamp("created_at").toInstant())
            .updatedAt(rs.getTimestamp("updated_at").toInstant())
            .build();

    @Override
    public ContactUs save(ContactUs contactUs) {
        String traceId = MDC.get("traceId");
        long startTime = System.currentTimeMillis();
        log.debug("Entering save repository | traceId={}, email={}", traceId, contactUs.getEmail());

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rowsAffected = jdbcTemplate.update(connection -> {

                PreparedStatement ps = connection.prepareStatement(
                        INSERT_QUERY,
                        Statement.RETURN_GENERATED_KEYS
                );

                ps.setString(1, contactUs.getName());
                ps.setString(2, contactUs.getEmail());
                ps.setString(3, contactUs.getSubject());
                ps.setString(4, contactUs.getMessage());
                ps.setTimestamp(5, Timestamp.from(contactUs.getCreatedAt()));
                ps.setTimestamp(6, Timestamp.from(contactUs.getUpdatedAt()));
                return ps;
            }, keyHolder);

            if (rowsAffected == 0) {
                log.error("Database insert failed | traceId={}, email={}", traceId, contactUs.getEmail());
                throw new DatabaseOperationException("Unable to save contact request");
            }

            Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
            long duration = System.currentTimeMillis() - startTime;
            log.info("Contact request saved successfully | traceId={}, id={}, duration={}ms", traceId, generatedId, duration);
            log.debug("Exiting save repository | traceId={}", traceId);

            contactUs.setId(generatedId);
            return contactUs;
        } catch (DataAccessException ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Database error saving contact request | traceId={}, email={}, duration={}ms, error={}",
                      traceId, contactUs.getEmail(), duration, ex.getMessage(), ex);
            throw new DatabaseOperationException("Database error occurred while saving contact request", ex);
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Unexpected error saving contact request | traceId={}, email={}, duration={}ms, error={}",
                      traceId, contactUs.getEmail(), duration, ex.getMessage(), ex);
            throw new DatabaseOperationException("Unexpected error occurred while saving contact request", ex);
        }
    }

    @Override
    public List<ContactUs> findAll() {
        String traceId = MDC.get("traceId");
        long startTime = System.currentTimeMillis();
        log.debug("Entering findAll repository | traceId={}", traceId);

        try {
            List<ContactUs> contactUsList = jdbcTemplate.query(SELECT_ALL_QUERY, contactUsRowMapper);
            long duration = System.currentTimeMillis() - startTime;
            log.info("All contact-us records fetched successfully | traceId={}, totalRecords={}, duration={}ms",
                     traceId, contactUsList.size(), duration);
            log.debug("Exiting findAll repository | traceId={}", traceId);

            return contactUsList;
        } catch (DataAccessException ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Database error fetching contact-us records | traceId={}, duration={}ms, error={}",
                      traceId, duration, ex.getMessage(), ex);
            throw new DatabaseOperationException("Database error occurred while fetching contact requests", ex);
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Unexpected error fetching contact requests | traceId={}, duration={}ms, error={}",
                      traceId, duration, ex.getMessage(), ex);
            throw new DatabaseOperationException("Unexpected error occurred while fetching contact requests", ex);
        }
    }

    @Override
    public ContactUs findById(Long id) {
        log.info("Executing database query to fetch contact request by id | id={}", id);
        try {
            ContactUs contactUs = jdbcTemplate.queryForObject(
                    SELECT_BY_ID_QUERY,
                    contactUsRowMapper,
                    id
            );

            if (contactUs == null) {
                log.warn("No contact request found | id={}", id);
                throw new DatabaseOperationException("Contact request not found");
            }
            log.info("Contact request fetched successfully | id={}", id);
            return contactUs;
        } catch (DataAccessException ex) {
            log.error(
                    "Database query failed while fetching contact request | id={}, error={}",
                    id,
                    ex.getMessage(),
                    ex
            );
            throw new DatabaseOperationException("Database error occurred while fetching contact request", ex);
        } catch (Exception ex) {
            log.error(
                    "Unexpected exception occurred while fetching contact request | id={}, error={}",
                    id,
                    ex.getMessage(),
                    ex
            );
            throw new DatabaseOperationException("Unexpected error occurred while fetching contact request", ex);
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("Executing database query to delete operation for contact-us record | id={}", id);
        try {
            int rowsAffected = jdbcTemplate.update(DELETE_BY_ID_QUERY, id);
            if (rowsAffected == 0) {
                log.warn("Delete operation failed because contact-us record does not exist | id={}", id);
                throw new DatabaseOperationException(
                        "Contact-us record not found with id: " + id
                );
            }

            log.info(
                    "Contact-us record deleted successfully from database | id={}, rowsAffected={}",
                    id,
                    rowsAffected
            );
        } catch (DataAccessException ex) {
            log.error(
                    "Database exception occurred while deleting contact-us record | id={}, error={}",
                    id,
                    ex.getMessage(),
                    ex
            );
            throw new DatabaseOperationException("Database error occurred while deleting contact-us record", ex);
        } catch (Exception ex) {
            log.error(
                    "Unexpected exception occurred while deleting contact-us record | id={}, error={}",
                    id,
                    ex.getMessage(),
                    ex
            );
            throw new DatabaseOperationException("Unexpected error occurred while deleting contact-us record", ex);
        }
    }
}
