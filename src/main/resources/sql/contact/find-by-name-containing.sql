SELECT c.id, c.first_name, c.last_name, p.phone, c.created_at, c.updated_at
FROM contacts c LEFT JOIN phone_numbers p ON p.contact_id = c.id
WHERE LOWER(c.first_name) LIKE ? OR LOWER(c.last_name) LIKE ?
ORDER BY LOWER(c.last_name), LOWER(c.first_name), p.phone
