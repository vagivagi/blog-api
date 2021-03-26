DELETE FROM entry_tag;
DELETE FROM tag;
DELETE FROM category;
DELETE FROM entry;

INSERT INTO entry VALUES (1,'Title','Content','author','2021-03-01T21:00:00','updater','2021-03-02T22:00:00');

INSERT INTO category VALUES (0,1,'demo');

INSERT INTO category VALUES (1,1,'Hello');

INSERT INTO tag VALUES ('demo');
INSERT INTO tag VALUES ('blog');

INSERT INTO entry_tag VALUES (1,'demo');

INSERT INTO entry_tag VALUES (1,'blog');


INSERT INTO entry VALUES (2,'Title2','Content2','vagivagi','2021-04-01T21:00:00','vagivagi','2021-04-02T21:00:00');

INSERT INTO category VALUES (0,2,'demo');

INSERT INTO category VALUES (1,2,'Training');

INSERT INTO tag VALUES ('food');
INSERT INTO tag VALUES ('protein');

INSERT INTO entry_tag VALUES (2,'food');

INSERT INTO entry_tag VALUES (2,'protein');