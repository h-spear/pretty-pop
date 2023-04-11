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
        String sql = "SELECT *" +
                    "   FROM (SELECT I.*, RANK() OVER (PARTITION BY I.category ORDER BY AVG(R.rating) DESC, COUNT(R.review_id) DESC, I.item_id) AS rank" +
                    "           FROM item I" +
                    "           LEFT JOIN review R" +
                    "             ON I.item_id = R.item_id" +
                    "          WHERE I.stock_quantity >= 0" +
                    "          GROUP BY I.item_id) sub" +
                    "  WHERE rank <= :top";

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
