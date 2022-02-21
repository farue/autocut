# Use 'create or replace view view_name as' to create a view

# Current balance on tenant accounts
select t.first_name,
       t.last_name,
       last_transactions.balance_after as current_balance,
       tb.id                           as tb_id,
       adr.street_number,
       apt.nr                          as apt,
       ns.id                           as switch,
       ia.port                         as port,
       ns.interface_name               as interface,
       u.email
from (
         select *
         from (select *, row_number() over (partition by lower(transaction_book_id) order by past_transactions.value_date desc, past_transactions.id desc) as rn
               from (select * from transaction where value_date <= now()) past_transactions) grouped_transactions
         where grouped_transactions.rn = 1) last_transactions
         inner join transaction_book tb on tb.id = last_transactions.transaction_book_id
         inner join lease_transaction_book ltb on tb.id = ltb.transaction_book_id
         inner join lease l on ltb.lease_id = l.id
         inner join tenant t on l.id = t.lease_id
         inner join apartment apt on l.apartment_id = apt.id
         inner join address adr on apt.address_id = adr.id
         inner join internet_access ia on apt.internet_access_id = ia.id
         inner join network_switch ns on ia.network_switch_id = ns.id
         left join jhi_user u on t.user_id = u.id
where tb.type = 'CASH' and l.end > now();

# accounts with negative balance in the future
select t.first_name,
       t.last_name,
       last_transactions.balance_after as current_balance,
       date_format(last_transactions.value_date, '%d.%m.%Y')   as negative_on,
       tb.id                           as tb_id,
       adr.street_number,
       apt.nr                          as apt,
       ns.id                           as switch,
       ia.port                         as port,
       ns.interface_name               as interface,
       u.email
from (
         select *
         from (select *,
                      row_number() over (partition by lower(transaction_book_id) order by past_transactions.value_date desc, past_transactions.id desc) as rn
               from (select * from transaction) past_transactions) grouped_transactions
         where grouped_transactions.rn = 1) last_transactions
         inner join transaction_book tb on tb.id = last_transactions.transaction_book_id
         inner join lease_transaction_book ltb on tb.id = ltb.transaction_book_id
         inner join lease l on ltb.lease_id = l.id
         inner join tenant t on l.id = t.lease_id
         inner join apartment apt on l.apartment_id = apt.id
         inner join address adr on apt.address_id = adr.id
         inner join internet_access ia on apt.internet_access_id = ia.id
         inner join network_switch ns on ia.network_switch_id = ns.id
         left join jhi_user u on t.user_id = u.id
where tb.type = 'CASH'
  and l.end > now()
  and last_transactions.balance_after < 0
order by last_transactions.value_date desc;


# Find transaction book ids
select t.first_name, t.last_name, t.id, l.id, ltb.transaction_book_id, tb.type
from tenant t
         inner join lease l on t.lease_id = l.id
         inner join lease_transaction_book ltb on l.id = ltb.lease_id
         inner join transaction_book tb on ltb.transaction_book_id = tb.id;

# Find transactions in transaction book, order newest first. 32 = Farü CASH, 33 = Farü REVENUE
select t.*
from transaction t
where t.transaction_book_id = 5
order by t.value_date desc, t.id desc;

# bank transactions
select *
from bank_transaction bt
         inner join transaction t on bt.id = t.id
order by t.value_date desc;

# internal transactions
select *
from bank_transaction bt
         inner join transaction t on bt.id = t.id
order by t.value_date desc;

# bank accounts
select distinct ten.first_name, ten.last_name, acc.iban
from bank_account acc
         inner join bank_transaction bt on acc.id = bt.contra_bank_account_id
         inner join transaction t on bt.id = t.id
         inner join transaction_link tl on t.id = tl.left_id
         inner join transaction contra on tl.right_id = contra.id
         inner join transaction_book tb on contra.transaction_book_id = tb.id
         inner join lease_transaction_book ltb on tb.id = ltb.transaction_book_id
         inner join lease l on ltb.lease_id = l.id
         inner join tenant ten on l.id = ten.lease_id
order by ten.last_name;

select *
from transaction
where type = 'CORRECTION';


# transactions on Farue CASH
select *
from transaction t
         inner join transaction_book tb on t.transaction_book_id = tb.id
where tb.name = 'FaRue Account' and tb.type = 'CASH'
order by t.value_date desc, t.id desc;

# current balance on transaction book
select *
from transaction t
where t.transaction_book_id = 13 and t.value_date <= now()
order by t.value_date desc, t.id desc;
