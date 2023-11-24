# Bank transactions for accounting
select date_format(convert_tz(t.value_date, 'UTC', 'Europe/Berlin'), '%d.%m.%Y') as "Datum",
       t.description as "Kommentar",
       case
           when t.description REGEXP 'ER[0-9]{4}-[0-9]{3}' then
               regexp_substr(t.description, 'ER[0-9]{4}-[0-9]{3}')
           when t.description REGEXP 'Eig[0-9]{4}-[0-9]{3}' then
               regexp_substr(t.description, 'Eig[0-9]{4}-[0-9]{3}')
           else
               ''
           end as "Beleg-Nr",
       t.value as "Betrag",
       t.balance_after as "Saldo"
from bank_transaction bt
         inner join transaction t on bt.id = t.id
where t.value_date > '2022-10-06'
order by t.value_date;

# account balances for accounting
set @date = '2023-09-24'; # actual accounting day + 1
# select sum(Saldo) as Summe from (
select t.first_name                    as 'Vorname',
       t.last_name                     as 'Nachname',
       last_transactions.balance_after as 'Saldo',
       adr.street_number               as 'Haus Nr.',
       apt.nr                          as 'Whg. Nr.'
from (
         select *
         from (select *,
                      row_number() over (partition by lower(transaction_book_id) order by past_transactions.value_date desc, past_transactions.id desc) as rn
               from (select * from transaction where value_date <= @date) past_transactions) grouped_transactions
#                from (select * from transaction) past_transactions) grouped_transactions
         where grouped_transactions.rn = 1) last_transactions
         inner join transaction_book tb on tb.id = last_transactions.transaction_book_id
         inner join lease_transaction_book ltb on tb.id = ltb.transaction_book_id
         inner join lease l on ltb.lease_id = l.id
         inner join (select t1.*
                     from tenant t1
                              left join tenant t2 on t1.lease_id = t2.lease_id and t1.id > t2.id
                     where t2.id is null) t on l.id = t.lease_id
         inner join apartment apt on l.apartment_id = apt.id
         inner join address adr on apt.address_id = adr.id
where tb.type = 'CASH'
  and l.end > @date
order by (adr.street_number + 0), (apt.nr + 0);
#    order by (adr.street_number + 0), (apt.nr + 0)
# ) balances;
