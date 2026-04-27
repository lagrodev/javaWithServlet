CREATE TABLE IF NOT EXISTS phone_numbers (
    id         SERIAL PRIMARY KEY,
    contact_id VARCHAR(36) NOT NULL,
    phone      VARCHAR(50) NOT NULL,
    FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE CASCADE
)
