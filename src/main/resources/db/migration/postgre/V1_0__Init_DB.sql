CREATE EXTENSION "uuid-ossp";

CREATE TABLE users(
	id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
	username VARCHAR(100) NOT NULL UNIQUE,
	password VARCHAR(200) NOT NULL,
	user_role VARCHAR(100) NOT NULL
);

CREATE TABLE notes(
	id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
	title VARCHAR(200) NOT NULL,
	text VARCHAR(10000) NOT NULL,
	access_type VARCHAR(100) NOT NULL,
	user_id UUID,
	FOREIGN KEY (user_id) REFERENCES users
);
