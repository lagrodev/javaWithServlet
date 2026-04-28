INSERT INTO contacts (id, first_name, last_name, created_at, updated_at)
VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'John', 'Smith', now(), now()),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'Robert', 'Johnson', now(), now());

INSERT INTO phone_numbers (contact_id, phone)
VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '+1234567890'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '+1987654321'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', '+1111222333')
