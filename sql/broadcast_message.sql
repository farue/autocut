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

# add message
start transaction;
insert into broadcast_message(type, start, end, users_only, dismissible)
    value ('INFO', null, '2022-06-20 22:00:00', true, null);
set @broadcast_message_id = last_insert_id();
insert into broadcast_message_text(lang_key, text, message_id)
values
    ('en', '<p class="text-center">Summer party: Vote by 20.06.2022 <a href="https://doodle.com/meeting/participate/id/dPNnoEla">https://doodle.com/meeting/participate/id/dPNnoEla</a></p>', @broadcast_message_id),
    ('de', '<p class="text-center">Sommerfest: Bis zum 20.06.2022 abstimmen <a href="https://doodle.com/meeting/participate/id/dPNnoEla">https://doodle.com/meeting/participate/id/dPNnoEla</a></p>', @broadcast_message_id);
commit;

# add message
start transaction;
insert into broadcast_message(type, start, end, users_only, dismissible)
    value ('INFO', null, '2023-09-23 15:00:00', true, null);
set @broadcast_message_id = last_insert_id();
insert into broadcast_message_text(lang_key, text, message_id)
values
    ('en', '<p class="text-center">Come to our general assembly: 23.09. at 13:00 in the table tennis room in <a href="https://maps.app.goo.gl/xK5F7jQ61Je13Bzm9" class="fr-link" target="_blank">OPH</a></p>', @broadcast_message_id),
    ('de', '<p class="text-center">Komm zur Vollversammlung: 23.09. um 13:00 im Tischtennisraum im <a href="https://maps.app.goo.gl/xK5F7jQ61Je13Bzm9" class="fr-link" target="_blank">OPH</a></p>', @broadcast_message_id);
commit;
