INSERT INTO contacts (id, first_name, last_name, created_at, updated_at)
VALUES (?, ?, ?, ?, ?)
ON CONFLICT (id) DO UPDATE
    SET first_name = excluded.first_name,
        last_name  = excluded.last_name,
        updated_at = excluded.updated_at
