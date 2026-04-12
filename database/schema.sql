CREATE DATABASE IF NOT EXISTS mma_gym;
USE mma_gym;

--------------------------------------------------------------------------------
USERS
--------------------------------------------------------------------------------

CREATE TABLE users (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role     ENUM('ADMIN','MEMBER') NOT NULL
);

--------------------------------------------------------------------------------
COACHES
--------------------------------------------------------------------------------

CREATE TABLE coaches (
    coach_id  INT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    specialty VARCHAR(100)
);

--------------------------------------------------------------------------------
ROOMS
--------------------------------------------------------------------------------

CREATE TABLE rooms (
    room_id  INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(50) NOT NULL,
    capacity INT NOT NULL
);

--------------------------------------------------------------------------------
CLASSES
--------------------------------------------------------------------------------

CREATE TABLE classes (
    class_id    INT AUTO_INCREMENT PRIMARY KEY,
    class_name  VARCHAR(100) NOT NULL,
    description TEXT,
    class_type  VARCHAR(50),
    skill_level VARCHAR(50),
    capacity    INT NOT NULL
);

--------------------------------------------------------------------------------
CLASS SESSIONS
--------------------------------------------------------------------------------

CREATE TABLE class_sessions (
    session_id       INT AUTO_INCREMENT PRIMARY KEY,
    class_id         INT         NOT NULL,
    day_of_week      VARCHAR(20) NOT NULL,
    start_time       VARCHAR(10) NOT NULL,
    coach_name       VARCHAR(100),
    room             VARCHAR(50),
    duration_minutes INT         NOT NULL,
    is_generated     TINYINT(1)  NOT NULL DEFAULT 0,
    FOREIGN KEY (class_id) REFERENCES classes(class_id) ON DELETE CASCADE
);

--------------------------------------------------------------------------------
MEMBER PROFILES
--------------------------------------------------------------------------------

CREATE TABLE member_profiles (
    member_id  INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT          NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name  VARCHAR(100),
    age        INT,
    height_cm  INT,
    weight_kg  DECIMAL(5,2),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

--------------------------------------------------------------------------------
CLASS REGISTRATIONS
--------------------------------------------------------------------------------

CREATE TABLE class_registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id       INT  NOT NULL,
    session_id      INT  NOT NULL,
    registered_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    week_start_date DATE NOT NULL DEFAULT '2000-01-01',
    UNIQUE KEY unique_registration (session_id, member_id, week_start_date),
    FOREIGN KEY (session_id) REFERENCES class_sessions(session_id)  ON DELETE CASCADE,
    FOREIGN KEY (member_id)  REFERENCES member_profiles(member_id)  ON DELETE CASCADE
);

--------------------------------------------------------------------------------
MEMBERSHIPS
--------------------------------------------------------------------------------

CREATE TABLE memberships (
    membership_id        INT AUTO_INCREMENT PRIMARY KEY,
    membership_name      VARCHAR(100) NOT NULL,
    description          TEXT,
    allowed_martial_arts VARCHAR(255),
    allowed_skill_levels VARCHAR(255)
);

--------------------------------------------------------------------------------
MEMBER MEMBERSHIPS
--------------------------------------------------------------------------------

CREATE TABLE member_memberships (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    member_id     INT NOT NULL,
    membership_id INT NOT NULL,
    FOREIGN KEY (member_id)     REFERENCES member_profiles(member_id) ON DELETE CASCADE,
    FOREIGN KEY (membership_id) REFERENCES memberships(membership_id) ON DELETE CASCADE
);

--------------------------------------------------------------------------------
EVENTS
--------------------------------------------------------------------------------

CREATE TABLE events (
    event_id             INT AUTO_INCREMENT PRIMARY KEY,
    event_name           VARCHAR(100) NOT NULL,
    event_date           DATE,
    location             VARCHAR(100),
    status               VARCHAR(50),
    format               VARCHAR(20),
    allowed_martial_arts VARCHAR(255)
);

--------------------------------------------------------------------------------
EVENT REGISTRATIONS
--------------------------------------------------------------------------------

CREATE TABLE event_registrations (
    registration_id    INT AUTO_INCREMENT PRIMARY KEY,
    event_id           INT       NOT NULL,
    member_id          INT       NOT NULL,
    registered_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    chosen_martial_art VARCHAR(100),
    experience_level   VARCHAR(50),
    FOREIGN KEY (event_id)  REFERENCES events(event_id)           ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member_profiles(member_id) ON DELETE CASCADE
);

--------------------------------------------------------------------------------
MATCHES
--------------------------------------------------------------------------------

CREATE TABLE matches (
    match_id         INT AUTO_INCREMENT PRIMARY KEY,
    event_id         INT          NOT NULL,
    participant1_id  INT          NOT NULL,
    participant2_id  INT          NOT NULL,
    status           VARCHAR(50),
    result           VARCHAR(255),
    round_number     INT,
    winner_member_id INT,
    notes            VARCHAR(255),
    FOREIGN KEY (event_id)        REFERENCES events(event_id)           ON DELETE CASCADE,
    FOREIGN KEY (participant1_id) REFERENCES member_profiles(member_id) ON DELETE CASCADE,
    FOREIGN KEY (participant2_id) REFERENCES member_profiles(member_id) ON DELETE CASCADE
);

--------------------------------------------------------------------------------
LIVE EVENT STATE
--------------------------------------------------------------------------------

CREATE TABLE live_event_state (
    event_id           INT PRIMARY KEY,
    current_match_id   INT,
    current_round      INT        NOT NULL DEFAULT 1,
    round_time_seconds INT        NOT NULL DEFAULT 180,
    remaining_seconds  INT        NOT NULL DEFAULT 180,
    timer_running      TINYINT(1) NOT NULL DEFAULT 0,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE
);
