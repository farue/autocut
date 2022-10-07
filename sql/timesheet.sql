# Times since last assembly
select t2.first_name, t2.last_name, sec_to_time(times.time), t.id as timesheet_id
from timesheet t
         left join (select timesheet_id, sum(effective_time) as time
                    from timesheet_time
                    where start >= '2022-03-19'
                    group by timesheet_id) times
                   on t.id = times.timesheet_id
         inner join tenant t2 on t.member_id = t2.id;

# Pretty print times in timesheet
select sec_to_time(effective_time) from timesheet_time
where timesheet_id = 15;

# Projects and tasks
select tp.id   as 'Project ID',
       tp.name as 'Project',
       tt.id   as 'Task ID',
       tt.name as 'Task',
       enabled,
       constant,
       constant_editable,
       factor,
       factor_editable,
       default_timespan
from timesheet_task tt
         inner join rel_timesheet_project__tasks rtt on tt.id = rtt.tasks_id
         inner join timesheet_project tp on rtt.timesheet_project_id = tp.id;

### CORRECTIONS

# Re-calculate effective time
update timesheet_time time
set time.effective_time =
        (select time_to_sec(timediff(time.end, time.start)) * task.factor + task.constant
         from timesheet_task task
         where time.task_id = task.id)
where time.id between 103 and 117;

# Change from Communication to Default task
update timesheet_time time
set time.task_id         = 1,
    time.edited_constant = null,
    time.end             = date_add(time.start, interval time.effective_time second)
where time.timesheet_id = 16
  and task_id = 16;

### INITIALIZATION

insert into timesheet_project(name, owner_id, start, end)
select res.name, res.id, null, null
from (select t.name, t2.id
      from team t
               inner join team_membership tm on t.id = tm.team_id
               inner join tenant t2 on tm.tenant_id = t2.id
      where tm.role = 'LEAD'
        and tm.end is null) res;

insert into timesheet(enabled, member_id)
select true, t2.id
from team t
         inner join team_membership tm on t.id = tm.team_id
         inner join tenant t2 on tm.tenant_id = t2.id
where tm.end is null
  and t2.id not in (select member_id from timesheet)
group by t2.id;

insert into timesheet_task(name, enabled, constant, constant_editable, factor, factor_editable)
select 'Default', true, 0, false, 1, false
from timesheet_project;

insert into timesheet_task(name, enabled, constant, constant_editable, factor, factor_editable)
select 'Communication', true, 900, true, 0, false
from timesheet_project;

insert into timesheet_task(name, enabled, constant, constant_editable, factor, factor_editable)
select 'Tool rental', true, 600, false, 1.2, false
from timesheet_project;

select count(*)
from timesheet_project;

insert into rel_timesheet_project__tasks(tasks_id, timesheet_project_id)
select t.id, p.id
from timesheet_task t,
     timesheet_project p
where t.id = (select p.id + 30);

insert into timesheet_project_member(start, end, project_id, timesheet_id)
select null, null, pid, tid
from (select p.id as pid, t2.id as tid
      from timesheet_project p
               inner join tenant t on p.owner_id = t.id
               inner join timesheet t2 on t.id = t2.member_id) res;

insert into timesheet_project_member(start, end, project_id, timesheet_id)
select concat(tm.start, ' 00:00:00.000000'), null, tp.id, ts.id
from team_membership tm
         inner join team t on tm.team_id = t.id
         inner join tenant t2 on tm.tenant_id = t2.id
         inner join timesheet ts on t2.id = ts.member_id
         inner join timesheet_project tp on t.name = tp.name
where tm.end is null
  and (tp.id, ts.id) not in (select project_id, timesheet_id from timesheet_project_member);

