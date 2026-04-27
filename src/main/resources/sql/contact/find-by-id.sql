SELECT c.id, c.first_name, c.last_name, p.phone, c.created_at, c.updated_at
FROM contacts c LEFT JOIN phone_numbers p ON p.contact_id = c.id
WHERE c.id = ?
ORDER BY p.id
