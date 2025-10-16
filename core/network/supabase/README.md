# Setup

## \<root folder\>/local.properties file
```properties
SUPABASE_URL=<your Supabase project url>
SUPABASE_KEY=<your publishable key>
```

## Tables
```sql
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
    id uuid PRIMARY KEY,
    username varchar(30) NOT NULL,
    email varchar(100) NOT NULL,
    bio varchar(100),
    created_at timestamptz NOT NULL DEFAULT now(),
    image_path varchar(100)
);

CREATE TABLE IF NOT EXISTS user_statuses (
    user_id uuid PRIMARY KEY REFERENCES users(id),
    last_active_at timestamptz NOT NULL
);

CREATE TABLE IF NOT EXISTS chats (
    id uuid PRIMARY KEY,
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
    chat_id uuid NOT NULL REFERENCES chat_settings(chat_id),
    user_id uuid REFERENCES users(id),
    role participant_role NOT NULL,
    joined_at timestamptz NOT NULL DEFAULT now(),
    left_at timestamptz,
    PRIMARY KEY (chat_id, user_id)
);

CREATE TABLE IF NOT EXISTS messages (
    id uuid PRIMARY KEY,
    sender_id uuid NOT NULL REFERENCES users(id),
    chat_id uuid NOT NULL REFERENCES chats(id),
    content text NOT NULL,
    sent_at timestamptz NOT NULL DEFAULT now(),
    edited_at timestamptz NOT NULL DEFAULT now(),
    is_hidden boolean NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS user_read_markers (
    user_id uuid NOT NULL REFERENCES users(id),
    chat_id uuid NOT NULL REFERENCES chats(id),
    last_read_at timestamptz,
    PRIMARY KEY (user_id, chat_id)
);

CREATE TABLE IF NOT EXISTS message_statuses (
    message_id uuid NOT NULL REFERENCES messages(id),
    user_id uuid NOT NULL REFERENCES users(id),
    is_deleted boolean NOT NULL DEFAULT false,
    PRIMARY KEY (message_id, user_id)
);

CREATE TABLE IF NOT EXISTS attachments (
    id uuid PRIMARY KEY,
    message_id uuid NOT NULL REFERENCES messages(id),
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

CREATE TABLE IF NOT EXISTS events (
    id uuid PRIMARY KEY,
    createdBy uuid REFERENCES users(id),
    chat_id uuid NOT NULL REFERENCES chats(id),
    title varchar(50) NOT NULL,
    description varchar(200),
    created_at timestamptz NOT NULL DEFAULT now(),
    edited_at timestamptz NOT NULL DEFAULT now(),
    start_time timestamptz NOT NULL
);
```

## RPCs
#### send_message_with_attachments
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
