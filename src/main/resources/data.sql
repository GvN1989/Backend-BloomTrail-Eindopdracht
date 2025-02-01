INSERT INTO users (username, password, fullname, email, enabled, apikey) VALUES
('henk','$2a$12$6GvdIyhdCYJ50.dwe3zuHuaQaOqMhRopEdnGxbjuQzQxWcNC.rTbG','Henk Jansen','test@testy.tst',true,'7847493'),
('alice','$2a$12$y8U7AbA/BbN5TuAV7CfOQ.FHRZT3y2PfFxdz6x67LXjYBd9XKjRUK', 'Alice Johnson', 'alice@example.com', true, '1234567'),
('bob','$2a$12$Qe7OghH07L89BZ7eFQXw/e8Y32whC2bF1RkT3Ul4KHJPcEk/TyCGu', 'Bob Smith', 'bob@example.com', true, '2345678'),
('charlie','$2a$12$Aq3BdLfQUtKReDqXCO.TWezOP//9YZH4q/kVxsZKrOSZBKyPcq.YC', 'Charlie Brown', 'charlie@example.com', true, '3456789'),
('diana','$2a$12$R2vF1GmkXWrAkeLxNbcZKO4Tk1gKrOgD6WZ3hskHEkFgCFmdzXz/O', 'Diana Prince', 'diana@example.com', true, '4567890'),
('edward','$2a$12$FBR6K5HcyE4x/2Y.E41AkeMIx5uFZGc3TCyP76tGz/o8MFP2Wj7wq', 'Edward Norton', 'edward@example.com', true, '5678901');

INSERT INTO authorities (username, authority) VALUES (
                                                      'henk',
                                                      'ROLE_ADMIN');

INSERT INTO coaching_programs (coaching_program_name, goal, start_date, end_date, progress, client_id, coach_id)
VALUES
    ('Mindset Mastery', 'Improve mental resilience and focus', '01-03-2024', '01-09-2024', 0.0, 'client1', 'coach1'),
    ('Fitness Transformation', 'Achieve peak physical fitness in 6 months', '15-02-2024', '15-08-2024', 0.0, 'client2', 'coach2');

INSERT INTO steps (step_name, step_start_date, step_end_date, completed, step_goal, sequence, coaching_program_id)
VALUES
    ('Introduction to Mental Resilience', '01-03-2024', '07-03-2024', false, 'Learn the basics of mental resilience', 1, 1),
    ('Daily Meditation Routine', '08-03-2024', '14-03-2024', false, 'Implement meditation for better focus', 2, 1),
    ('Handling Stress Effectively', '15-03-2024', '21-03-2024', false, 'Techniques to manage and reduce stress', 3, 1),
    ('Building Healthy Habits', '22-03-2024', '28-03-2024', false, 'Developing sustainable habits for well-being', 4, 1),
    ('Final Mindset Evaluation', '29-03-2024', '05-04-2024', false, 'Assess progress and set future goals', 5, 1);


INSERT INTO steps (step_name, step_start_date, step_end_date, completed, step_goal, sequence, coaching_program_id)
VALUES
    ('Fitness Assessment', '15-02-2024', '21-02-2024', false, 'Initial fitness evaluation and goal setting', 1, 2),
    ('Strength Training Basics', '22-02-2024', '28-02-2024', false, 'Introduction to weight training', 2, 2),
    ('Cardio and Endurance Building', '01-03-2024', '07-03-2024', false, 'Improving cardiovascular health', 3, 2),
    ('Nutrition and Meal Planning', '08-03-2024', '14-03-2024', false, 'Creating a healthy meal plan', 4, 2),
    ('Final Performance Check', '15-03-2024', '21-03-2024', false, 'Assess progress and next steps', 5, 2);
