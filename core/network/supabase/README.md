# Setup

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
