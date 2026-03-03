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
      'joined_at', cp.joined_at,
      'left_at', cp.left_at
    )
  )
  into result
  from chat_participants cp
  join users u on u.id = cp.user_id
  where cp.chat_id = _chat_id and cp.left_at is null;

  return coalesce(result, '[]'::jsonb);
end;
$$;
```

### update_chat_participant_status
```sql
create or replace function update_chat_participant_status(
    _user_id uuid,
    _chat_id uuid,
    _update_cleared_at boolean default false,
    _update_left_at boolean default false,
    _update_archived_at boolean default false,
    _update_last_read_at boolean default false
)
returns chat_participant_statuses
language plpgsql
as $$
declare
    _result chat_participant_statuses;
begin
    update chat_participant_statuses
    set
        cleared_at   = case when _update_cleared_at   then now() else cleared_at end,
        left_at      = case when _update_left_at      then now() else left_at end,
        archived_at  = case when _update_archived_at  then now() else archived_at end,
        last_read_at = case when _update_last_read_at then now() else last_read_at end
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