CREATE TABLE IF NOT EXISTS users (
                                     username VARCHAR(50) PRIMARY KEY,
                                     password VARCHAR(255) NOT NULL,
                                     full_name VARCHAR(100),
                                     email VARCHAR(100) UNIQUE NOT NULL,
                                     enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                     api_key VARCHAR(255)
);

INSERT INTO users (username, password, full_name, email, enabled, api_key) VALUES
('henk','$2a$10$Pq2fS8wJ/s5morEL6YZW6.MHQD3jTU4G62HdD4F4cryO7UXV3cIuW','Henk Jansen','test@testy.tst',true,'7847493'),
('alice','$2a$10$a66zyevDCm5FGEFO630R.u6n0t39vnSu0PAOboGwD1y1tkSO3pK9G', 'Alice Johnson', 'alice@example.com', true, '1234567'),
('bob','$2a$10$KJX3hu08ZWdYjpmfRaV.CO5yHkZMSw588GyQbrz6Xfe1fq7TZ1JsG', 'Bob Smith', 'bob@example.com', true, '2345678'),
('charlie','$2a$10$Bl6CuP6cutZCT5ecLyN/N.PuLy5qDChvt7NlhsvbACBUZn9JuIipm', 'Charlie Brown', 'charlie@example.com', true, '3456789'),
('diana','$2a$12$R2vF1GmkXWrAkeLxNbcZKO4Tk1gKrOgD6WZ3hskHEkFgCFmdzXz/O', 'Diana Prince', 'diana@example.com', true, '4567890'),
('edward','$2a$10$B9OklibPTW4pf1U4jnFuxeCoSa0MKoFNESn.9teiLJYcoyNjDNJzG', 'Edward Norton', 'edward@example.com', true, '5678901');


CREATE TABLE IF NOT EXISTS authorities (
                                           username VARCHAR(50) NOT NULL,
                                           authority VARCHAR(50) NOT NULL,
                                           PRIMARY KEY (username, authority),
                                           FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

INSERT INTO authorities (username, authority) VALUES (
                                                      'henk',
                                                      'ROLE_ADMIN'),
('alice', 'ROLE_COACH'),
('bob', 'ROLE_USER'),
('charlie', 'ROLE_USER'),
('diana', 'ROLE_USER'),
('edward', 'ROLE_USER');

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
    ('Strengths-Based Personal Growth', 'Develop self-awareness and leverage natural talents', '01-04-2024', '01-10-2024', 0.0, 'diana', 'alice'),
    ('Leadership & Influence Mastery', 'Build leadership skills and effectively influence others', '15-03-2024', '15-09-2024', 0.0, 'charlie', 'henk');

CREATE TABLE IF NOT EXISTS steps (
                                     step_id SERIAL PRIMARY KEY,
                                     step_name VARCHAR(255) NOT NULL,
                                     step_goal TEXT,
                                     completed BOOLEAN,
                                     step_start_date DATE NOT NULL,
                                     step_end_date DATE NOT NULL,
                                     sequence INTEGER NOT NULL,
                                     coaching_program_id BIGINT NOT NULL,
                                     FOREIGN KEY (coaching_program_id) REFERENCES coaching_programs(coaching_program_id) ON DELETE CASCADE
);

INSERT INTO steps (step_name, step_start_date, step_end_date, completed, step_goal, sequence, coaching_program_id)
VALUES
    ('Discovering Your Strengths', '01-04-2024', '07-04-2024', false, 'Complete StrengthsFinder assessment to identify top strengths', 1, 1),
    ('Understanding Talent Themes', '08-04-2024', '14-04-2024', false, 'Deep dive into individual talent themes and their impact', 2, 1),
    ('Applying Strengths to Daily Life', '15-04-2024', '21-04-2024', false, 'Use strengths to solve problems and make decisions', 3, 1),
    ('Overcoming Weaknesses Through Strengths', '22-04-2024', '28-04-2024', false, 'Learn how to manage weaknesses by leaning into strengths', 4, 1),
    ('Creating a Strengths-Based Development Plan', '29-04-2024', '05-05-2024', false, 'Set up long-term goals and growth strategies', 5, 1);


INSERT INTO steps (step_name, step_start_date, step_end_date, completed, step_goal, sequence, coaching_program_id)
VALUES
    ('Understanding Strengths-Based Leadership', '15-03-2024', '21-03-2024', false, 'Identify leadership style based on strengths', 1, 2),
    ('Building Effective Communication Skills', '22-03-2024', '28-03-2024', false, 'Develop influence through communication techniques', 2, 2),
    ('Leading with Emotional Intelligence', '01-04-2024', '07-04-2024', false, 'Apply emotional intelligence to lead teams effectively', 3, 2),
    ('Strengths-Based Decision Making', '08-04-2024', '14-04-2024', false, 'Use strengths to make confident and impactful decisions', 4, 2),
    ('Sustaining Leadership Growth', '15-04-2024', '21-04-2024', false, 'Create a plan for continuous leadership development', 5, 2);
