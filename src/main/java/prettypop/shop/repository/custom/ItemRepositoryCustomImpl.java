package prettypop.shop.repository.custom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import prettypop.shop.entity.Category;
import prettypop.shop.entity.Item;
import prettypop.shop.entity.ItemStatus;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final NamedParameterJdbcTemplate template;

    public ItemRepositoryCustomImpl(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<Item> findTopRatingByCategory(int top) {
        String sql = "SELECT ITEM_ID as ID, *" +
                    "   FROM (SELECT I.*, RANK() OVER (PARTITION BY I.CATEGORY ORDER BY AVG(R.RATING) DESC, COUNT(R.REVIEW_ID) DESC, I.ITEM_ID) AS RANK" +
                    "           FROM ITEM I" +
                    "           LEFT JOIN REVIEW R" +
                    "             ON I.ITEM_ID = R.ITEM_ID" +
                    "          WHERE I.STOCK_QUANTITY >= 0" +
                    "          GROUP BY I.ITEM_ID) SUB" +
                    "  WHERE RANK <= :top";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("top", top);

        log.info("sql={}", sql);
        return template.query(sql, param, itemRowMapper());
    }

    private RowMapper<Item> itemRowMapper() {
        return (rs, rowNum) -> Item.builder()
                .id(rs.getLong("ITEM_ID"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .originalPrice(rs.getInt("ORIGINAL_PRICE"))
                .purchasePrice(rs.getInt("PURCHASE_PRICE"))
                .earnedPoint(rs.getInt("EARNED_POINT"))
                .stockQuantity(rs.getInt("STOCK_QUANTITY"))
                .salesVolume(rs.getInt("SALES_VOLUME"))
                .itemStatus(ItemStatus.valueOf(rs.getString("ITEM_STATUS")))
                .category(Category.valueOf(rs.getString("CATEGORY")))
                .thumbnailImageUrl(rs.getString("THUMBNAIL_IMAGE_URL"))
                .build();
    }
}
