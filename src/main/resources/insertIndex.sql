#tradeCount 초기 데이터 삽입
INSERT INTO trade_count (item_id, count)
SELECT market.item_id, COUNT(*)
FROM trade
JOIN market ON trade.market_id = market.id
GROUP BY market.item_id;

#조회용 인덱스들
CREATE INDEX idx_tradeCount_count_item_desc ON trade_count (count desc, item_id desc);
CREATE INDEX idx_auction_filter ON auction (status, created_at, bidder_count, id);
CREATE INDEX idx_market_status_created_item ON market (status, created_at, item_id, amount, price);

#풀텍스트 인덱스
CREATE FULLTEXT INDEX idx_fulltext ON item (name)