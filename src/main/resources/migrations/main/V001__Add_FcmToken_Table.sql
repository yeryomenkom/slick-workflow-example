CREATE TABLE fcm_token(
    token VARCHAR(255) PRIMARY KEY,
    user_id INT NOT NULL,
    device_type VARCHAR(8) NOT NULL,
    metadata JSON,
    time_update TIMESTAMP NOT NULL
);