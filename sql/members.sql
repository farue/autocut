# verify tenants
select adr.street_number    as strNo,
       a.nr                 as aptNo,
       case
           when a.type = 'SINGLE' then 'FAM'
           when a.type = 'SHARED' then 'WG'
           when a.type = 'BACKUP' then 'AUS'
           when a.type = 'SHORT_TERM' then 'K'
           end              as type,
       t.first_name         as first_name,
       t.last_name          as last_name,
       email,
       activated,
       created_date         as created,
       start,
end,
       verified,
       ia.network_switch_id as switch,
       ia.port              as port,
       ia.ip_1              as IP,
       nss.status           as status,
       l.nr                 as leaseNo,
       u.id                 as userID,
       t.id                 as tenantID,
       l.id                 as leaseID,
       b.id                 as tbId
from tenant t
         inner join lease l on t.lease_id = l.id
         inner join apartment a on l.apartment_id = a.id
         inner join address adr on a.address_id = adr.id
         inner join internet_access ia on a.internet_access_id = ia.id
         inner join network_switch ns on ia.network_switch_id = ns.id
         left join network_switch_status nss
                    on ns.id = nss.network_switch_id and concat(ns.interface_name, '/', ia.port) = nss.port
         left join jhi_user u on t.user_id = u.id
         left join (select *
                    from lease_transaction_book ltb
                             inner join transaction_book tb on ltb.transaction_book_id = tb.id
                    where tb.type = 'CASH') b on l.id = b.lease_id
where end > now()
order by u.created_date desc;
# order by (adr.street_number + 0), (a.nr + 0);


# compare to STW list
select t.first_name         as first_name,
       t.last_name          as last_name,
       l.nr,
       email,
       created_date         as created,
       start,
end
from tenant t
         inner join lease l on t.lease_id = l.id
         left join jhi_user u on t.user_id = u.id
# where end > now()
order by l.nr;

# leases with no tenants
select l.id
from lease l
where l.id not in (select t.lease_id from tenant t);

# expired leases
select t.first_name,
       t.last_name,
       l.id,
       l.start,
       l.end,
       l.nr,
       u.email,
       ia.network_switch_id as switch,
       ia.port
from lease l
         inner join tenant t on l.id = t.lease_id
         inner join jhi_user u on t.user_id = u.id
         inner join apartment a on l.apartment_id = a.id
         inner join internet_access ia on a.internet_access_id = ia.id
where l.end < now()
  and l.end > date_sub(now(), interval 1 month)
order by l.end desc;

# internet access for apartments (IPs)
select adr.street_number, a.nr, i.ip_1, i.network_switch_id, i.port
from internet_access i
         inner join apartment a on i.id = a.internet_access_id
         inner join address adr on a.address_id = adr.id;

# empty apartments
select adr.street_number, apt.nr, apt.type, ia.network_switch_id, ia.port
from apartment apt
         inner join address adr on apt.address_id = adr.id
         inner join internet_access ia on apt.internet_access_id = ia.id
where apt.id not in (select l.apartment_id from lease l where l.end >= now())
order by (adr.street_number + 0), (apt.nr + 0);

# number of active leases
select count(*)
from lease l
where l.end > now();

# delete tenant
start transaction;
set @tenantId = 101;
set @leaseId = (select lease_id
                from tenant
                where id = @tenantId);
set @userId = (select user_id
               from tenant
               where id = @tenantId);
set @tenantsRemaining = ((select count(*)
                          from tenant
                          where lease_id = @leaseId
                            and id <> @tenantId) > 0);
set @anyTransactions = ((select count(*)
                         from transaction t
                                  inner join lease_transaction_book ltb
                                             on t.transaction_book_id = ltb.transaction_book_id
                         where ltb.lease_id = @leaseId) > 0);
set @transactionBooks = (select group_concat(ltb.transaction_book_id)
                         from lease_transaction_book ltb
                         where ltb.lease_id = @leaseId);
select @tenantId, @leaseId, @userId, @tenantsRemaining, @anyTransactions, @transactionBooks;
delete
from lease_transaction_book ltb
where ltb.lease_id = @leaseId;
delete
from transaction_book tb
where find_in_set(tb.id, @transactionBooks);
delete
from tenant
where id = @tenantId;
delete
from jhi_user_authority
where user_id = @userId;
delete
from jhi_user
where id = @userId;
delete
from lease
where id = @leaseId;
commit;
# rollback;

# close tenant account
start transaction;
set @moveOutDate = '2021-09-25';
set @tenantId = 101;
set @leaseId = (select lease_id
                from tenant
                where id = @tenantId);
update lease
set end = @moveOutDate
where id = @leaseId;
update team_membership
set end = @moveOutDate
where tenant_id = @tenantId;

# Select all by apartment
select * from lease l
                  inner join tenant t on l.id = t.lease_id
                  inner join apartment a on l.apartment_id = a.id
                  inner join address adr on a.address_id = adr.id
                  inner join internet_access ia on a.internet_access_id = ia.id
                  inner join network_switch ns on ia.network_switch_id = ns.id
where l.nr = '30-05';

# Search by name
select *
from tenant t
where t.first_name like '%khatih%'
   or t.last_name like '%khatih%';

# Select all by apartment type
select *
from apartment a
         inner join lease l on a.id = l.apartment_id
         inner join tenant t on l.id = t.lease_id
         inner join jhi_user ju on t.user_id = ju.id
where a.type = 'SINGLE' and l.end > now();

# delete unverified member
select * from lease_transaction_book where lease_id = 160;
delete from tenant where id = 167;
delete from lease_transaction_book where lease_id = 160;
delete from lease where id = 160;
delete from transaction_book where id = 366;
delete from jhi_user_authority where user_id = 341;
delete from jhi_user where id = 341;
