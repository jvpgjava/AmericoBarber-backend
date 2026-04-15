CREATE TABLE gallery_photos (
    id BIGSERIAL PRIMARY KEY,
    image_data TEXT NOT NULL,
    title VARCHAR(100),
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
