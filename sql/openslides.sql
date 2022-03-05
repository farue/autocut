# Note: This only includes members with a valid (not expired) membership. Thus if someone's renewed rental contract has not been entered into the system yet,
# they will not be included.

# generate user import data
set @vv_date = '2022-03-19';
select null                                                                as 'Title',
       t.first_name                                                        as 'Given name',
       t.last_name                                                         as 'Surname',
       '1'                                                                 as 'Active',
       '1'                                                                 as 'Natural person',
       null                                                                as 'Initial password',
       u.email                                                             as 'Email',
       u.login                                                             as 'Username',
       null                                                                as 'Gender',
       u.id                                                                as 'Participant number',
       (select group_concat(teamtable.teamname SEPARATOR ', ')
        from (select case
                         when team.name = 'SPOKESPERSON' then 'Wohnheimsprecher/in'
                         when team.name = 'ASSIGNMENT' then 'BA'
                         when team.name = 'NETWORKING' then 'Netzwerk AG'
                         when team.name = 'WASHING' then 'Wasch AG'
                         when team.name = 'TOOLS' then 'Werkzeug AG'
                         when team.name = 'ACCOUNTING' then 'Kasse'
                         when team.name = 'RECORDER' then 'Protokollführer/in'
                         when team.name = 'ASSOCIATION' then 'Vereins AG'
                         when team.name = 'AUDITOR' then 'Kassenprüfer'
                         when team.name = 'GARDENING' then 'Garten AG'
                         end as teamname
              from team_membership tm
                       inner join team on tm.team_id = team.id
              where tm.tenant_id = t.id
                and tm.start <= @vv_date
                and (tm.end is null or tm.end > @vv_date)) as teamtable)   as 'Structure level',
       '1'                                                                 as 'Vote weight',
       null                                                                as 'Comment',
       null                                                                as 'Is present',
       (select if(spezialRoles is null, 'Delegates', concat('Delegates', ', ', spezialRoles))
        from (select group_concat(DISTINCT roles.role SEPARATOR ', ') as spezialRoles
              from (select case
                               when team.name = 'SPOKESPERSON' and tm.role = 'LEAD' then 'Staff'
                               when team.name = 'NETWORKING' and tm.role = 'LEAD' then 'Admin'
                               when tm.role = 'LEAD' then 'AG' end as role
                    from team_membership tm
                             inner join team on tm.team_id = team.id
                    where tm.tenant_id = t.id
                      and tm.start <= @vv_date
                      and (tm.end is null or tm.end > @vv_date)) roles) r) as 'Groups'
from tenant t
         inner join jhi_user u on t.user_id = u.id
         inner join lease l on t.lease_id = l.id
where t.verified
  and l.start <= @vv_date
  and l.end > @vv_date;
