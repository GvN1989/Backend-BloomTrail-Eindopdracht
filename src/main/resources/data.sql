CREATE TABLE IF NOT EXISTS users (
                                     username VARCHAR(50) PRIMARY KEY,
                                     password VARCHAR(255) NOT NULL,
                                     full_name VARCHAR(100),
                                     email VARCHAR(100) UNIQUE NOT NULL,
                                     enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                     api_key VARCHAR(255)
);


CREATE TABLE IF NOT EXISTS authorities (
                                           username VARCHAR(50) NOT NULL,
                                           authority VARCHAR(50) NOT NULL,
                                           PRIMARY KEY (username, authority),
                                           CONSTRAINT fk_authorities_user FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

INSERT INTO users (username, password, full_name, email, enabled, api_key) VALUES
                                                                                             ('henk','$2a$10$8KhGMxoMIUtKsfwzv/xPaesps2gNWP28mEKHqO9qKNuNulmEDXMQi','Henk Jansen','test@testy.tst',true,'7847493'),
                                                                                             ('alice','$2a$10$rlkxAjkKiApYgOGnQbXSQ.W3tR8QmsKk520mCFIFXvXbkeeEgTBDC', 'Alice Johnson', 'alice@example.com', true, '1234567'),
                                                                                             ('charlie','$2a$10$0NUzILKVMk3OEGE/FxYGJuG2IN8JQkXES47jHBMBNJV.WieAk1HAy', 'Charlie Brown', 'charlie@example.com', true, '3456789'),
                                                                                             ('diana','$2a$10$iB5J6qmeMsDHm02msCu2R.CPHIHTAudAwq868KEqclJ0uAYnNmdPW', 'Diana Prince', 'diana@example.com', true, '4567890');

INSERT INTO authorities (username, authority) VALUES (
                                                         'henk',
                                                         'ADMIN'),
                                                     ('alice', 'COACH'),
                                                     ('charlie', 'USER'),
                                                     ('diana', 'USER');








CREATE TABLE IF NOT EXISTS coaching_programs (
                                                 coaching_program_id SERIAL PRIMARY KEY,
                                                 coaching_program_name VARCHAR(255) NOT NULL,
                                                 goal TEXT,
                                                 start_date DATE NOT NULL,
                                                 end_date DATE NOT NULL,
                                                 progress DOUBLE PRECISION DEFAULT 0.0,
                                                 client_id VARCHAR(50) NOT NULL,
                                                 coach_id VARCHAR(50) NOT NULL,
                                                 FOREIGN KEY (client_id) REFERENCES users(username),
                                                 FOREIGN KEY (coach_id) REFERENCES users(username)
);


INSERT INTO coaching_programs (coaching_program_name, goal, start_date, end_date, progress, client_id, coach_id)
VALUES
    ('Strengths-Based Personal Growth', 'Develop self-awareness and leverage natural talents', '01-04-2024', '01-10-2024', 0.0, 'diana', 'henk'),
    ('Leadership & Influence Mastery', 'Build leadership skills and effectively influence others', '15-03-2024', '15-09-2024', 0.0, 'charlie', 'alice');

CREATE TABLE IF NOT EXISTS steps (
                                     step_id SERIAL PRIMARY KEY,
                                     step_name VARCHAR(255) NOT NULL,
                                     step_goal TEXT,
                                     step_status BOOLEAN,
                                     step_start_date DATE NOT NULL,
                                     step_end_date DATE NOT NULL,
                                     sequence INTEGER NOT NULL,
                                     coaching_program_id BIGINT NOT NULL,
                                     FOREIGN KEY (coaching_program_id) REFERENCES coaching_programs(coaching_program_id) ON DELETE CASCADE
);

INSERT INTO steps (step_name, step_start_date, step_end_date, step_status, step_goal, sequence, coaching_program_id)
VALUES
    ('Discovering Your Strengths', '01-04-2024', '07-04-2024', false, 'Complete StrengthsFinder assessment to identify top strengths', 1, 1),
    ('Understanding Talent Themes', '08-04-2024', '14-04-2024', false, 'Deep dive into individual talent themes and their impact', 2, 1),
    ('Applying Strengths to Daily Life', '15-04-2024', '21-04-2024', false, 'Use strengths to solve problems and make decisions', 3, 1),
    ('Overcoming Weaknesses Through Strengths', '22-04-2024', '28-04-2024', false, 'Learn how to manage weaknesses by leaning into strengths', 4, 1),
    ('Creating a Strengths-Based Development Plan', '29-04-2024', '05-05-2024', false, 'Set up long-term goals and growth strategies', 5, 1);


INSERT INTO steps (step_name, step_start_date, step_end_date, step_status, step_goal, sequence, coaching_program_id)
VALUES
    ('Understanding Strengths-Based Leadership', '15-03-2024', '21-03-2024', false, 'Identify leadership style based on strengths', 1, 2),
    ('Building Effective Communication Skills', '22-03-2024', '28-03-2024', false, 'Develop influence through communication techniques', 2, 2),
    ('Leading with Emotional Intelligence', '01-04-2024', '07-04-2024', false, 'Apply emotional intelligence to lead teams effectively', 3, 2),
    ('Strengths-Based Decision Making', '08-04-2024', '14-04-2024', false, 'Use strengths to make confident and impactful decisions', 4, 2),
    ('Sustaining Leadership Growth', '15-04-2024', '21-04-2024', false, 'Create a plan for continuous leadership development', 5, 2);

CREATE TABLE IF NOT EXISTS sessions (
                                     session_id SERIAL PRIMARY KEY,
                                     session_name VARCHAR(255) NOT NULL,
                                     coach TEXT NOT NULL,
                                     client TEXT NOT NULL,
                                     session_date DATE NOT NULL,
                                     session_time TIME NOT NULL,
                                     location TEXT,
                                     comment TEXT,
                                     step_id BIGINT NOT NULL,
                                     FOREIGN KEY (step_id) REFERENCES steps(step_id) ON DELETE CASCADE
);

INSERT INTO sessions (session_Name, coach, client, session_date, session_time, location, comment,created_at, updated_at, step_id)
VALUES
    ('Kickoff Session', 'henk', 'diana', '08-05-2025', '10:00', 'online', 'Set initial goals','2025-05-01 09:00:00', '2025-05-01 09:00:00', 1),
    ('Reflection Session', 'henk', 'diana', '15-05-2025', '11:30', 'office', 'Reflect on talens','2025-05-08 09:00:00', '2025-05-08 09:00:00',1),
    ('Feedback Session', 'henk', 'diana', '28-05-2025', '14:00', 'online', 'Provide feedback on progress','2025-05-15 09:00:00', '2025-05-15 09:00:00',1),
    ('Introduction Session', 'alice', 'charlie', '05-05-2025', '09:00', 'office', 'Discuss expectations', '2025-04-30 09:00:00', '2025-04-30 09:00:00',6),
    ('Strengths Debrief', 'alice', 'charlie', '12-05-2025', '13:00', 'online', 'Debrief strengths results','2025-05-05 09:00:00', '2025-05-05 09:00:00',6);

