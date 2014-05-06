CREATE TABLE account (
  guid SERIAL NOT NULL,
  username character varying(50) NOT NULL,
  password character varying(50) NOT NULL,
  title character varying(200) DEFAULT NULL,
  psm character varying(200) DEFAULT NULL,
  online int DEFAULT '0',
  CONSTRAINT account_pkey PRIMARY KEY (guid)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE contact (
  id SERIAL NOT NULL,
  o_guid int NOT NULL,
  c_guid int NOT NULL,
  CONSTRAINT contact_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);


CREATE TABLE contact_request (
  id SERIAL NOT NULL,
  o_guid int NOT NULL,
  r_guid int NOT NULL,
  CONSTRAINT contact_request_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);


CREATE TABLE messages (
  id SERIAL NOT NULL,
  o_guid int NOT NULL,
  r_guid int NOT NULL,
  message text NOT NULL,
  fsg text NOT NULL,
  fcg text NOT NULL,
  datetime timestamp NOT NULL,
  CONSTRAINT messages_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);


CREATE TABLE subscribe (
  sid SERIAL NOT NULL,
  title text NOT NULL,
  CONSTRAINT subscribe_pkey PRIMARY KEY (sid)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE subscribe_account (
  id SERIAL NOT NULL,
  sid int NOT NULL,
  guid int NOT NULL,
  CONSTRAINT subscribe_account_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);


CREATE TABLE subscribe_messages (
  id SERIAL NOT NULL,
  o_guid int NOT NULL,
  r_guid int NOT NULL,
  sid int NOT NULL,
  message text NOT NULL,
  fsg text NOT NULL,
  fcg text NOT NULL,
  datetime timestamp NOT NULL,
  CONSTRAINT subscribe_messages_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);