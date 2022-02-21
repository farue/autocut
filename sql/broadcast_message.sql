# add message
start transaction;
insert into broadcast_message(type, start, end, users_only, dismissible)
    value ('WARN', null, '2021-10-13 16:00:00', true, null);
set @broadcast_message_id = last_insert_id();
insert into broadcast_message_text(lang_key, text, message_id)
values
    ('en', '<p class="text-center">Scheduled maintenance today at 6pm. The website will be temporarily unavailable.</p>', @broadcast_message_id),
    ('de', '<p class="text-center">Geplante Wartung heute um 18:00. Die Website wird kurzzeitig nicht verfügbar sein.</p>', @broadcast_message_id);
commit;
