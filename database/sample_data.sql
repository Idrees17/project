USE mma_gym;

--------------------------------------------------------------------------------
USERS
Admin login  : username = admin   / password = admin123
Member login : username = member1 / password = member123
--------------------------------------------------------------------------------

INSERT INTO users (username, password, role) VALUES
('admin',   'admin123',  'ADMIN'),
('member1', 'member123', 'MEMBER');

--------------------------------------------------------------------------------
COACHES
--------------------------------------------------------------------------------

INSERT INTO coaches (name, specialty) VALUES
('John Smith',   'Boxing'),
('Carlos Reyes', 'Jiu Jitsu'),
('Mike Davis',   'MMA');

--------------------------------------------------------------------------------
ROOMS
--------------------------------------------------------------------------------

INSERT INTO rooms (name, capacity) VALUES
('Main Mat',    20),
('Boxing Ring', 15),
('Cage',        10);

--------------------------------------------------------------------------------
CLASSES
--------------------------------------------------------------------------------

INSERT INTO classes (class_name, description, class_type, skill_level, capacity) VALUES
('Beginner Boxing',    'Introduction to boxing fundamentals.',         'Boxing',    'Beginner',              15),
('Beginner Jiu Jitsu', 'Ground work and basic submission techniques.', 'Jiu Jitsu', 'Beginner',              15),
('Intermediate MMA',   'Mixed martial arts for intermediate level.',   'MMA',       'Intermediate/Advanced', 10),
('Beginner MMA',       'Introduction to mixed martial arts.',          'MMA',       'Beginner',              12),
('Advanced Jiu Jitsu', 'Advanced grappling and competition prep.',     'Jiu Jitsu', 'Intermediate/Advanced',  8);

--------------------------------------------------------------------------------
CLASS SESSIONS
--------------------------------------------------------------------------------

INSERT INTO class_sessions (class_id, day_of_week, start_time, coach_name, room, duration_minutes, is_generated) VALUES
(1, 'Monday',    '09:00', 'John Smith',   'Boxing Ring', 60, 0),
(1, 'Wednesday', '09:00', 'John Smith',   'Boxing Ring', 60, 0),
(1, 'Friday',    '09:00', 'John Smith',   'Boxing Ring', 60, 0),
(2, 'Monday',    '10:30', 'Carlos Reyes', 'Main Mat',    60, 0),
(2, 'Thursday',  '10:30', 'Carlos Reyes', 'Main Mat',    60, 0),
(3, 'Tuesday',   '18:00', 'Mike Davis',   'Cage',        90, 0),
(3, 'Thursday',  '18:00', 'Mike Davis',   'Cage',        90, 0),
(4, 'Wednesday', '18:00', 'Mike Davis',   'Main Mat',    60, 0),
(5, 'Saturday',  '10:00', 'Carlos Reyes', 'Main Mat',    90, 0);

--------------------------------------------------------------------------------
MEMBER PROFILE
user_id 2 = member1
--------------------------------------------------------------------------------

INSERT INTO member_profiles (user_id, first_name, last_name, age, height_cm, weight_kg) VALUES
(2, 'James', 'Taylor', 25, 178, 75.50);

--------------------------------------------------------------------------------
MEMBERSHIPS
--------------------------------------------------------------------------------

INSERT INTO memberships (membership_name, description, allowed_martial_arts, allowed_skill_levels) VALUES
('Beginner Plan',  'Access to all beginner level classes.',                 'Boxing,Jiu Jitsu,MMA',                                      'Beginner'),
('Full Access',    'Unlimited access to all classes and levels.',           'Boxing,Jiu Jitsu,MMA,Kickboxing,Muay Thai,Wrestling,Hyrox', 'Beginner,Intermediate/Advanced'),
('Grappling Plan', 'Access to all Jiu Jitsu and Wrestling classes.',        'Jiu Jitsu,Wrestling',                                       'Beginner,Intermediate/Advanced');

--------------------------------------------------------------------------------
ASSIGN MEMBERSHIP TO MEMBER
member_id 1 = James Taylor / membership_id 1 = Beginner Plan
--------------------------------------------------------------------------------

INSERT INTO member_memberships (member_id, membership_id) VALUES
(1, 1);

--------------------------------------------------------------------------------
EVENTS
--------------------------------------------------------------------------------

INSERT INTO events (event_name, event_date, location, status, format, allowed_martial_arts) VALUES
('Summer Interclub 2025', '2025-07-20', 'Main Gym Floor', 'Open',     'MATCHES', 'Boxing,MMA,Jiu Jitsu'),
('Autumn Open 2025',      '2025-10-15', 'Main Gym Floor', 'Upcoming', 'MATCHES', 'Boxing,Kickboxing,Muay Thai');
