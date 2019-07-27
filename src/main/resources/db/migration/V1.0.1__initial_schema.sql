CREATE TABLE entry (
  entry_id           INTEGER      NOT NULL,
  title              VARCHAR(512) NOT NULL,
  content            LONGTEXT     NOT NULL,
  created_by         VARCHAR(128),
  created_date       DATETIME,
  last_modified_by   VARCHAR(128),
  last_modified_date DATETIME,
  PRIMARY KEY (entry_id)
)
  ENGINE = InnoDB;


CREATE TABLE category (
  category_order INTEGER      NOT NULL,
  entry_id       INTEGER      NOT NULL,
  category_name  VARCHAR(128) NOT NULL,
  PRIMARY KEY (category_order, entry_id),
  FOREIGN KEY (entry_id) REFERENCES entry (entry_id)
    ON DELETE CASCADE
)
  ENGINE = InnoDB;

 CREATE TABLE tag (
  tag_name VARCHAR(255) NOT NULL,
  PRIMARY KEY (tag_name)
);

CREATE TABLE entry_tag (
  entry_id INTEGER      NOT NULL,
  tag_name VARCHAR(255) NOT NULL,
  PRIMARY KEY (entry_id, tag_name),
  FOREIGN KEY (entry_id) REFERENCES entry (entry_id)
    ON DELETE CASCADE,
  FOREIGN KEY (tag_name) REFERENCES tag (tag_name)
    ON DELETE CASCADE
)
  ENGINE = InnoDB;

CREATE INDEX entry_last_modified_date
  ON entry (last_modified_date);

CREATE INDEX category_name_order
  ON category (category_name, category_order);