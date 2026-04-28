SELECT c.id, c.first_name, c.last_name, p.phone, c.created_at, c.updated_at
FROM contacts c JOIN phone_numbers p ON p.contact_id = c.id
WHERE p.phone LIKE ?
ORDER BY LOWER(c.last_name), LOWER(c.first_name), p.phone
