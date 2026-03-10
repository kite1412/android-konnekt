# Setup

# Project
## \<root folder\>/local.properties file
```properties
SUPABASE_URL=<your Supabase project url>
SUPABASE_KEY=<your publishable key>
```

# Supabase
## Tables
```sql
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Enums
DO $$ BEGIN
    CREATE TYPE chat_type AS ENUM ('personal', 'group', 'chat_room');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE participant_role AS ENUM ('member', 'admin');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE attachment_type AS ENUM ('document', 'image', 'video', 'audio');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Tables
CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    username varchar(30) NOT NULL,
    email varchar(100) NOT NULL,
    bio varchar(100),
    created_at timestamptz NOT NULL DEFAULT now(),
    image_path varchar(100)
);

CREATE TABLE IF NOT EXISTS user_activity_statuses (
    user_id uuid PRIMARY KEY REFERENCES users(id),
    last_active_at timestamptz NOT NULL
);

CREATE TABLE IF NOT EXISTS chats (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    type chat_type NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS chat_settings (
    chat_id uuid PRIMARY KEY REFERENCES chats(id),
    name varchar(50) NOT NULL,
    icon_path varchar(100),
    description varchar(200)
);

CREATE TABLE IF NOT EXISTS chat_permission_settings (
    chat_id uuid PRIMARY KEY REFERENCES chats(id),
    edit_chat_info boolean NOT NULL DEFAULT false,
    send_messages boolean NOT NULL DEFAULT true,
    create_events boolean NOT NULL DEFAULT false,
    manage_members boolean NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS chat_participants (
    chat_id uuid NOT NULL REFERENCES chats(id),
    user_id uuid NOT NULL REFERENCES users(id),
    role participant_role NOT NULL,
    PRIMARY KEY (chat_id, user_id)
);

CREATE TABLE IF NOT EXISTS messages (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    sender_id uuid NOT NULL REFERENCES users(id),
    chat_id uuid NOT NULL REFERENCES chats(id),
    content text NOT NULL,
    sent_at timestamptz NOT NULL DEFAULT now(),
    edited_at timestamptz NOT NULL DEFAULT now(),
    is_hidden boolean NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS user_message_statuses (
    message_id uuid NOT NULL REFERENCES messages(id),
    user_id uuid NOT NULL REFERENCES users(id),
    is_deleted boolean NOT NULL DEFAULT false,
    PRIMARY KEY (message_id, user_id)
);

CREATE TABLE IF NOT EXISTS attachments (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    message_id uuid NOT NULL REFERENCES messages(id),
    chat_id uuid NOT NULL REFERENCES chats(id),
    type attachment_type NOT NULL,
    path varchar(200) NOT NULL,
    name text,
    size bigint
);

CREATE TABLE IF NOT EXISTS attachment_metadata (
    attachment_id uuid PRIMARY KEY REFERENCES attachments(id),
    width integer,
    height integer,
    duration bigint,
    mime_type varchar(200)
);

CREATE TABLE IF NOT EXISTS chat_participant_statuses (
    user_id uuid NOT NULL REFERENCES users(id),
    chat_id uuid NOT NULL REFERENCES chats(id),
    joined_at timestampz NOT NULL,
    cleared_at timestamptz,
    left_at timestamptz,
    archived_at timestamptz,
    last_read_at timestamptz,
    PRIMARY KEY (user_id, chat_id)
);
```

## Enable realtime for tables:
- users
- chat_settings
- chat_permission_settings
- chat_participants
- chat_participant_statuses
- messages
- user_message_statuses

## RPCs
### create_chat
```sql
create or replace function create_chat(
    _type text,
    _participant_ids text[],
    _name text default null,
    _description text default null,
    _icon_path text default null,
    _permission_settings jsonb default null
)
returns jsonb
language plpgsql
as $$
declare
    _user_id uuid := auth.uid();
    _chat_id uuid;
    _chat_row record;
    _setting_row chat_settings;
    _personal_setting jsonb;
    _permission_row chat_permission_settings;
    _pid text;
    _participant_count integer;
    _participants jsonb;
begin
    _participant_count := array_length(_participant_ids, 1);

    if _type = 'personal' and (_participant_count is null or _participant_count != 1) then
        raise exception 'personal chats must have exactly one other participant'
        using errcode = 'P0001';
    end if;

    if _type != 'personal' and _name is null then
        raise exception 'group/broadcast chats require a name'
        using errcode = 'P0002';
    end if;

    insert into chats (type, created_at)
    values (_type::chat_type, now())
    returning id, type, created_at
    into _chat_row;

    _chat_id := _chat_row.id;

    insert into chat_participants (chat_id, user_id, role)
    values (
        _chat_id,
        _user_id,
        case
            when _type = 'personal' then 'member'::participant_role
            else 'admin'::participant_role
        end
    );

    foreach _pid in array _participant_ids
    loop
        insert into chat_participants (chat_id, user_id, role)
        values (_chat_id, _pid::uuid, 'member'::participant_role);
    end loop;

    insert into chat_participant_statuses (chat_id, user_id, joined_at, last_read_at)
    select _chat_id, user_id, now(), now()
    from chat_participants
    where chat_id = _chat_id;

    if _type = 'personal' then
        select jsonb_build_object(
            'chat_id', _chat_id,
            'name', u.username,
            'description', u.bio,
            'icon_path', u.image_path,
            'permission_settings', null
        )
        into _personal_setting
        from users u
        where u.id = (
            select cp.user_id
            from chat_participants cp
            where cp.chat_id = _chat_id
            and cp.user_id != _user_id
            limit 1
        );
    end if;


    select jsonb_agg(
        jsonb_build_object(
            'user', to_jsonb(u),
            'role', cp.role,
            'status', to_jsonb(cps)
        )
    )
    into _participants
    from chat_participants cp
    join users u
        on u.id = cp.user_id
    join chat_participant_statuses cps
        on cps.user_id = cp.user_id
      and cps.chat_id = cp.chat_id
    where cp.chat_id = _chat_id;

    if _type != 'personal' then
        insert into chat_settings (chat_id, name, description, icon_path)
        values (_chat_id, _name, _description, _icon_path)
        returning * into _setting_row;

        if _type = 'group' then
            insert into chat_permission_settings (
                chat_id,
                manage_members,
                send_messages,
                edit_chat_info
            )
            values (
                _chat_id,
                coalesce((_permission_settings->>'manage_members')::boolean, false),
                coalesce((_permission_settings->>'send_messages')::boolean, true),
                coalesce((_permission_settings->>'edit_chat_info')::boolean, false)
            )
            returning * into _permission_row;
        end if;
    end if;

    return jsonb_build_object(
        'id', _chat_id,
        'type', _chat_row.type,
        'created_at', _chat_row.created_at,
        'participants', _participants,
        'setting',
        case
            when _type = 'personal' then _personal_setting
            else jsonb_build_object(
                'chat_id', _setting_row.chat_id,
                'name', _setting_row.name,
                'description', _setting_row.description,
                'icon_path', _setting_row.icon_path,
                'permission_settings',
                case
                    when _permission_row.chat_id is not null
                    then to_jsonb(_permission_row)
                    else null
                end
            )
        end
    );
end;
$$;
```

### join_chat
```sql
create or replace function join_chat(
    _chat_id uuid
)
returns jsonb
language plpgsql
as $$
declare
    _user_id uuid := auth.uid();
    _role participant_role;
    _user_record users;
    _status_record chat_participant_statuses;
begin
    insert into chat_participants (
        chat_id,
        user_id,
        role
    )
    values (
        _chat_id,
        _user_id,
        'member'
    )
    on conflict (chat_id, user_id) do nothing;

    insert into chat_participant_statuses (
        chat_id,
        user_id
    )
    values (
        _chat_id,
        _user_id
    )
    on conflict (chat_id, user_id) do nothing;

    select role
    into _role
    from chat_participants
    where chat_id = _chat_id
      and user_id = _user_id;

    select *
    into _user_record
    from users
    where id = _user_id;

    select *
    into _status_record
    from chat_participant_statuses
    where chat_id = _chat_id
      and user_id = _user_id;

    return jsonb_build_object(
        'role', _role,
        'user', to_jsonb(_user_record),
        'status', to_jsonb(_status_record)
    );
end;
$$;
```

### get_chat_by_id
```sql
create or replace function get_chat_by_id(
    _chat_id uuid
)
returns jsonb
language plpgsql
as $$
declare
    _current_user_id uuid := auth.uid();
    result jsonb;
begin
    select jsonb_build_object(
        'id', c.id,
        'type', c.type,
        'created_at', c.created_at,

        'setting',
        case
            when cs.chat_id is not null then
                jsonb_build_object(
                    'chat_id', cs.chat_id,
                    'name', cs.name,
                    'description', cs.description,
                    'icon_path', cs.icon_path
                )

            when c.type = 'personal' then (
                select jsonb_build_object(
                    'chat_id', c.id,
                    'name', u2.username,
                    'description', u2.bio,
                    'icon_path', u2.image_path
                )
                from chat_participants cp2
                join users u2
                    on u2.id = cp2.user_id
                where cp2.chat_id = c.id
                  and u2.id != _current_user_id
                limit 1
            )

            else null
        end,

        'participants', (
            select jsonb_agg(
                jsonb_build_object(
                    'user', to_jsonb(u),
                    'role', cp.role,
                    'status', to_jsonb(ps)
                )
            )
            from chat_participants cp
            join users u
                on u.id = cp.user_id
            left join chat_participant_statuses ps
                on ps.chat_id = cp.chat_id
               and ps.user_id = cp.user_id
            where cp.chat_id = c.id
        )
    )
    into result
    from chats c
    left join chat_settings cs
        on cs.chat_id = c.id
    where c.id = _chat_id;

    return result;
end;
$$;
```

### get_joined_chats
```sql
create or replace function get_joined_chats(
    _user_id uuid
)
returns jsonb
language plpgsql
security definer
as $$
declare
    result jsonb;
begin
    select jsonb_agg(
        jsonb_build_object(
            'id', c.id,
            'type', c.type,
            'created_at', c.created_at,
            'setting',
                case
                    when cs.chat_id is not null then
                        jsonb_build_object(
                            'chat_id', cs.chat_id,
                            'name', cs.name,
                            'description', cs.description,
                            'icon_path', cs.icon_path
                        )
                    else case
                        when c.type = 'personal' then
                            jsonb_build_object(
                                'chat_id', u.id,
                                'name', u.username,
                                'description', u.bio,
                                'icon_path', u.image_path
                            )
                        else null
                    end
                end,
            'participants',
                (
                    select jsonb_agg(
                        jsonb_build_object(
                            'user', to_jsonb(u),
                            'role', cp.role,
                            'status', to_jsonb(ps)
                        )
                    )
                    from chat_participants cp
                    join users u
                        on u.id = cp.user_id
                    where cp.chat_id = c.id
                )
        )
    )
    into result
    from chats c
    join chat_participants p
        on p.chat_id = c.id
    join chat_participant_statuses ps
        on ps.user_id = _user_id
        and ps.chat_id = c.id
    left join chat_settings cs
        on cs.chat_id = c.id
    left join users u
        on u.id = _user_id
    where p.user_id = _user_id;

    return coalesce(result, '[]'::jsonb);
end;
$$;
```

### send_message_with_attachments
```sql
create or replace function send_message_with_attachments(
    _chat_id uuid,
    _sender_id uuid,
    _content text,
    _attachments jsonb default '[]'::jsonb
)
returns jsonb
language plpgsql
as $$
declare
    new_message messages%rowtype;
    new_attachments jsonb;
begin
    insert into messages (chat_id, sender_id, content)
    values (_chat_id, _sender_id, _content)
    returning * into new_message;

    if jsonb_array_length(_attachments) > 0 then
        insert into attachments (message_id, chat_id, type, path, name, size)
        select
            new_message.id,
            _chat_id,
            (att->>'type')::attachment_type,
            att->>'path',
            att->>'name',
            nullif(att->>'size', 'null')::bigint
        from jsonb_array_elements(_attachments) as att;
    
        select coalesce(jsonb_agg(row_to_json(a)), '[]'::jsonb)
        into new_attachments
        from attachments a
        where a.message_id = new_message.id;
    else
        new_attachments := '[]'::jsonb;
    end if;

    return jsonb_build_object(
       'message', row_to_json(new_message),
       'attachments', coalesce(to_json(new_attachments), '[]'::json)
    );
end;
$$;
```

### get_chat_participants
```sql
create or replace function get_chat_participants(
    _chat_id uuid
)
returns jsonb
language plpgsql
as $$
declare
    result jsonb;
begin
    select jsonb_agg(
        jsonb_build_object(
            'chat_id', _chat_id,
            'user', to_jsonb(u),
            'role', cp.role,
            'status', to_jsonb(cps)
        )
    )
    into result
    from chat_participants cp
    join users u on u.id = cp.user_id
    join chat_participant_statuses cps on cps.user_id = cp.user_id
    where cp.chat_id = _chat_id and cps.left_at is null and cps.chat_id = _chat_id;
    
    return coalesce(result, '[]'::jsonb);
end;
$$;
```

### update_chat_participant_status
```sql
create or replace function update_chat_participant_status(
    _user_id uuid,
    _chat_id uuid,
    _update_cleared_at boolean default null,
    _update_left_at boolean default null,
    _update_archived_at boolean default null,
    _update_last_read_at boolean default null
)
returns chat_participant_statuses
language plpgsql
as $$
declare
    _result chat_participant_statuses;
begin
    update chat_participant_statuses
    set
        cleared_at = case
            when _update_cleared_at is true then now()
            when _update_cleared_at is false then null
            else cleared_at
        end,
        left_at = case
            when _update_left_at is true then now()
            when _update_left_at is false then null
            else left_at
        end,
        archived_at = case
            when _update_archived_at is true then now()
            when _update_archived_at is false then null
            else archived_at
        end,
        last_read_at = case
            when _update_last_read_at is true then now()
            when _update_last_read_at is false then null
            else last_read_at
        end
    where user_id = _user_id
      and chat_id = _chat_id
    returning * into _result;

    return _result;
end;
$$;
```

## Buckets
- icon
- chat-media

***note:*** you need to configure policies for each bucket