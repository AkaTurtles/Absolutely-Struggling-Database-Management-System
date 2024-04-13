--Database set up


CREATE TABLE Trainers (
	trainer_id SERIAL PRIMARY KEY,
	first_name VARCHAR(255) NOT NULL,
	last_name VARCHAR(255) NOT NULL,	
	_username VARCHAR(25) UNIQUE NOT NULL,
	_password VARCHAR(255) NOT NULL
);

CREATE TABLE Admins (
	admin_id SERIAL PRIMARY KEY,
	first_name VARCHAR(255) NOT NULL,
	last_name VARCHAR(255) NOT NULL,
	_username VARCHAR(25) UNIQUE NOT NULL,
	_password VARCHAR(255) NOT NULL	
);

CREATE TABLE Training_Sessions (
	session_id SERIAL PRIMARY KEY,
	date_time TIMESTAMP NOT NULL,
	duration FLOAT NOT NULL,
	description TEXT,
	trainer_id INTEGER REFERENCES Trainers(trainer_id)
);

CREATE TABLE Group_Classes (
	class_id SERIAL PRIMARY KEY,
	date_time TIMESTAMP NOT NULL,
	duration FLOAT NOT NULL,
	description TEXT,
	trainer_id INTEGER REFERENCES Trainers(trainer_id)
);

CREATE TABLE Members (
	member_id SERIAL PRIMARY KEY, 
	first_name VARCHAR(255) NOT NULL,
	last_name VARCHAR(255) NOT NULL,
	goalWeight FLOAT check (goalWeight > 0),
	currentWeight FLOAT check (currentWeight > 0),
	time_days FLOAT check (time_days > 0),
	session_id INTEGER REFERENCES Training_Sessions(session_id),
	class_id INTEGER REFERENCES Group_Classes(class_id),
	_username VARCHAR(25) UNIQUE NOT NULL,
	_password VARCHAR(255) NOT NULL
);

CREATE TABLE Rooms (
	room_number SERIAL PRIMARY KEY,
	room_size VARCHAR(6) check (room_size in ('Small', 'Medium', 'Large')),
	session_id INTEGER REFERENCES Training_Sessions(session_id),
	class_id INTEGER REFERENCES Group_Classes(class_id)
);

CREATE TABLE Equipment (
	equipment_id SERIAL PRIMARY KEY,
	description TEXT,
	session_id INTEGER REFERENCES Training_Sessions(session_id),
	class_id INTEGER REFERENCES Group_Classes(class_id)
);
