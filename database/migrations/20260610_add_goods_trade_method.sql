-- 为商品交易方式功能补充数据库字段及相关数据迁移。
ALTER TABLE goods
ADD COLUMN trade_method ENUM('offline', 'online', 'both')
DEFAULT 'offline'
COMMENT '交易方式：offline线下交易 online线上付款 both均可'
AFTER trade_place;
