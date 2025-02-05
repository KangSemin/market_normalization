#인기 마켓
EXPLAIN ANALYZE
select m1_0.item_id,i1_0.name,coalesce(sum(m1_0.amount),?),coalesce(min(m1_0.price),?),tc1_0.count from market m1_0 join item i1_0 on i1_0.id=m1_0.item_id left join trade_count tc1_0 on m1_0.item_id=tc1_0.item_id where m1_0.status=? and m1_0.created_at>=date_sub(now(), interval 1 month) group by m1_0.id,m1_0.item_id,i1_0.name,tc1_0.count order by tc1_0.count desc limit ?;

#인기 옥션
EXPLAIN ANALYZE
select a1_0.id,a1_0.item_id,i1_0.name,a1_0.starting_price,b1_0.bid_price,a1_0.due_date,a1_0.bidder_count from bid b1_0 join auction a1_0 on a1_0.id=b1_0.auction_id join item i1_0 on i1_0.id=a1_0.item_id where a1_0.status=? and a1_0.created_at>=date_sub(now(), interval 1 month) group by a1_0.id,a1_0.item_id,i1_0.name,a1_0.starting_price,a1_0.due_date,b1_0.bid_price,a1_0.bidder_count order by a1_0.bidder_count desc limit ?;


#메인 마켓
EXPLAIN ANALYZE
select i1_0.id,i1_0.name,coalesce(sum(m1_0.amount),?),coalesce(min(m1_0.price),?) from market m1_0 join item i1_0 on i1_0.id=m1_0.item_id where m1_0.status=? group by i1_0.id,i1_0.name order by i1_0.id limit ?,?;

#메인 옥션
EXPLAIN ANALYZE
select a1_0.id,a1_0.item_id,i2_0.name,a1_0.starting_price,b1_0.bid_price,a1_0.due_date,a1_0.bidder_count from auction a1_0 join item i2_0 on i2_0.id=a1_0.item_id left join bid b1_0 on a1_0.id=b1_0.auction_id left join item i1_0 on a1_0.item_id=i1_0.id where a1_0.status=? and a1_0.created_at>=date_sub(now(), interval 1 month) group by a1_0.id,a1_0.item_id,i2_0.name,a1_0.starting_price,a1_0.due_date,b1_0.bid_price,a1_0.bidder_count order by a1_0.id limit ?,?;
