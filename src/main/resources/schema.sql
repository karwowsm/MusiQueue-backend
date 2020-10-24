CREATE TABLE IF NOT EXISTS user_account (
  id UUID NOT NULL,
  username TEXT NOT NULL,
  password TEXT NOT NULL,
  email TEXT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (username),
  UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS room (
  id UUID NOT NULL,
  name TEXT NOT NULL,
  user_queued_tracks_limit INT DEFAULT NULL,
  host_id UUID NOT NULL,
  started_playing_at TIMESTAMP DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE (name),
  FOREIGN KEY (host_id) REFERENCES user_account (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS track (
  id UUID NOT NULL,
  artist TEXT NOT NULL,
  title TEXT NOT NULL,
  duration INT NOT NULL,
  image_url TEXT,
  source TEXT NOT NULL,
  track_id TEXT NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS room_members (
  user_account_id UUID NOT NULL,
  room_id UUID NOT NULL,
  PRIMARY KEY (user_account_id),
  FOREIGN KEY (user_account_id) REFERENCES user_account (id) ON DELETE CASCADE,
  FOREIGN KEY (room_id) REFERENCES room (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS room_tracks (
  id UUID NOT NULL,
  track_id UUID NOT NULL,
  room_id UUID,
  owner_id UUID,
  user_index INT NOT NULL,
  added_at TIMESTAMP NOT NULL,
  index INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (track_id) REFERENCES track (id),
  FOREIGN KEY (room_id) REFERENCES room (id) ON DELETE SET NULL,
  FOREIGN KEY (owner_id) REFERENCES user_account (id) ON DELETE SET NULL,
  UNIQUE (room_id, index)
);

ALTER TABLE room
  ADD COLUMN IF NOT EXISTS current_track_id UUID DEFAULT NULL REFERENCES room_tracks (id);

CREATE TABLE IF NOT EXISTS file_entity (
  path TEXT NOT NULL,
  original_name TEXT,
  content BYTEA NOT NULL,
  PRIMARY KEY (path)
);
