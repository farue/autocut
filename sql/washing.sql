# Find tenants that have used the washing system in the past
select t.first_name, t.last_name, t.id
from wash_history w
         inner join tenant t on w.using_tenant_id = t.id
group by t.id;

# Show washing history
select t.first_name                        as first,
       t.last_name                         as last,
       h.using_date                        as date,
       concat(wm.type, ' ', wm.identifier) as machine,
       concat(wp.name, ' ', coalesce(wp.subprogram, '')) as program
from wash_history h
         inner join wash_machine wm on h.machine_id = wm.id
         inner join wash_machine_program wmp on h.program_id = wmp.id
         inner join wash_program wp on wmp.program_id = wp.id
         inner join tenant t on h.using_tenant_id = t.id
order by using_date desc;

# Show washing history for tenant
select t.first_name                        as first,
       t.last_name                         as last,
       h.using_date                        as date,
       concat(wm.type, ' ', wm.identifier) as machine,
       concat(wp.name, ' ', coalesce(wp.subprogram, '')) as program
from wash_history h
         inner join wash_machine wm on h.machine_id = wm.id
         inner join wash_machine_program wmp on h.program_id = wmp.id
         inner join wash_program wp on wmp.program_id = wp.id
         inner join tenant t on h.using_tenant_id = t.id
where t.id = 42
order by using_date desc;

# double washing bookings
select t.first_name, t.last_name, double_entries.id1, double_entries.id2, double_entries.date1, double_entries.date2, double_entries.machine_id, u.email
from (
         select *, row_number() over (PARTITION BY res.id1 ORDER BY id2) as rn
         from (
                  select h1.using_tenant_id as tenant_id,
                         h1.id              as id1,
                         h2.id              as id2,
                         h1.using_date      as date1,
                         h2.using_date      as date2,
                         h1.machine_id
                  from wash_history as h1
                           cross join wash_history as h2
                  where h1.id < h2.id
                    and h1.using_tenant_id = h2.using_tenant_id
                    and h1.machine_id = h2.machine_id
                    and h1.status = 'COMPLETED'
                    and h2.status = 'COMPLETED'
                    and date_add(h1.using_date, interval 10 minute) >= h2.using_date) res) double_entries
         inner join tenant t on double_entries.tenant_id = t.id
         left join jhi_user u on t.user_id = u.id
where rn = 1
order by date1 desc;
