# all team memberships
select t.first_name, t.last_name, team.name, tm.role, tm.start, tm.end, tm.id
from tenant t
         inner join team_membership tm on t.id = tm.tenant_id
         inner join team on tm.team_id = team.id
order by team.name, (case tm.role when 'LEAD' then 1 when 'DEPUTY' then 2 when 'MEMBER' then 3 end),
         tm.end is null desc, tm.end desc;

# current team memberships
select t.first_name, t.last_name, team.name, tm.role, tm.start, tm.end, tm.id
from tenant t
         inner join team_membership tm on t.id = tm.tenant_id
         inner join team on tm.team_id = team.id
where tm.end is null or tm.end > now()
order by team.name, (case tm.role when 'LEAD' then 1 when 'DEPUTY' then 2 when 'MEMBER' then 3 end);
