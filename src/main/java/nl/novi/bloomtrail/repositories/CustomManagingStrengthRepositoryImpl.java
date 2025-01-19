package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.ManagingStrength;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CustomManagingStrengthRepositoryImpl implements CustomManagingStrengthRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomManagingStrengthRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ManagingStrength> getManagingStrengths(List<Integer> strengthIds) {
        String sql = "SELECT * FROM managingStrengths WHERE Id IN (:strengthIds)";
        String placeholders = String.join(",", strengthIds.stream().map(String::valueOf).toArray(String[]::new));
        sql = sql.replace(":strengthIds", placeholders);

        return jdbcTemplate.query(sql, new RowMapper<ManagingStrength>() {
            @Override
            public ManagingStrength mapRow(ResultSet rs, int rowNum) throws SQLException {
                ManagingStrength strength = new ManagingStrength();
                strength.setStrengthId(rs.getInt("Id"));
                strength.setStrengthNl(rs.getString("Strength_NL"));
                strength.setStrengthEn(rs.getString("Strength_ENG"));
                strength.setContributionDoing(rs.getString("Contribution_Doing"));
                strength.setContributionBeing(rs.getString("Contribution_Being"));
                strength.setNeedTask(rs.getString("Need_Task"));
                strength.setNeedRelationship(rs.getString("Need_Relationship"));
                strength.setExternalTriggers(rs.getString("External_Triggers"));
                strength.setInternalTriggers(rs.getString("Internal_Triggers"));
                strength.setOperatingBelief(rs.getString("Operating_belief"));
                strength.setMisManaged(rs.getString("Miss_Managed"));
                return strength;
            }
        });
    }
}
