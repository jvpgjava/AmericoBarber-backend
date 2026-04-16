CREATE TABLE availability_breaks (
    availability_id BIGINT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (availability_id) REFERENCES availability (id) ON DELETE CASCADE
);

-- Migrate existing breaks
INSERT INTO availability_breaks (availability_id, start_time, end_time)
SELECT id, break_start_time, break_end_time FROM availability
WHERE break_start_time IS NOT NULL AND break_end_time IS NOT NULL;

-- Drop columns from availability
ALTER TABLE availability DROP COLUMN break_start_time;
ALTER TABLE availability DROP COLUMN break_end_time;
