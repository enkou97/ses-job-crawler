-- スキルマスターデータ
INSERT INTO skills (name, category, aliases) VALUES
-- プログラミング言語
('Java', 'language', '["java", "JAVA"]'),
('Python', 'language', '["python", "Python3"]'),
('JavaScript', 'language', '["JS", "javascript", "js"]'),
('TypeScript', 'language', '["TS", "typescript", "ts"]'),
('Go', 'language', '["Golang", "golang", "go"]'),
('PHP', 'language', '["php"]'),
('Ruby', 'language', '["ruby"]'),
('C#', 'language', '["csharp", "C Sharp"]'),
('C++', 'language', '["cpp", "C/C++"]'),
('Kotlin', 'language', '["kotlin"]'),
('Swift', 'language', '["swift"]'),
('Scala', 'language', '["scala"]'),
('Rust', 'language', '["rust"]'),

-- フレームワーク
('Spring Boot', 'framework', '["SpringBoot", "Spring", "spring boot"]'),
('React', 'framework', '["react", "React.js", "ReactJS"]'),
('Vue.js', 'framework', '["Vue", "vue", "vuejs"]'),
('Angular', 'framework', '["angular", "AngularJS"]'),
('Next.js', 'framework', '["nextjs", "Next"]'),
('Node.js', 'framework', '["nodejs", "Node"]'),
('Django', 'framework', '["django"]'),
('Flask', 'framework', '["flask"]'),
('Rails', 'framework', '["Ruby on Rails", "RoR"]'),
('Laravel', 'framework', '["laravel"]'),
('.NET', 'framework', '["dotnet", "ASP.NET"]'),

-- データベース
('MySQL', 'database', '["mysql"]'),
('PostgreSQL', 'database', '["postgres", "Postgres", "pgsql"]'),
('Oracle', 'database', '["oracle", "OracleDB"]'),
('SQL Server', 'database', '["MSSQL", "Microsoft SQL Server"]'),
('MongoDB', 'database', '["mongo", "mongodb"]'),
('Redis', 'database', '["redis"]'),

-- クラウド
('AWS', 'cloud', '["Amazon Web Services", "aws"]'),
('Azure', 'cloud', '["Microsoft Azure", "azure"]'),
('GCP', 'cloud', '["Google Cloud", "Google Cloud Platform"]'),

-- その他
('Docker', 'devops', '["docker"]'),
('Kubernetes', 'devops', '["k8s", "K8s"]'),
('Terraform', 'devops', '["terraform", "IaC"]'),
('Git', 'tool', '["git", "GitHub", "GitLab"]'),
('Linux', 'os', '["linux", "RHEL", "CentOS", "Ubuntu"]');
