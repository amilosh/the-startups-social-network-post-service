DROP TABLE post_album;
DROP TABLE likes;
DROP TABLE comment;
DROP TABLE post_ad;
DROP TABLE post_resource;
DROP TABLE posts_hashtags;
DROP TABLE post;
DROP TABLE favorite_albums;
DROP INDEX album_author_title_idx;
DROP TABLE album;
DROP TABLE hashtag;

DELETE
FROM databasechangelog
WHERE filename IN ('db/changelog/changeset/post_V001__initial.sql',
                   'db/changelog/changeset/post_V002__ad.sql',
                   'db/changelog/changeset/post_V003_resource.sql',
                   'db/changelog/changeset/post_V004__hashtag.sql',
                   'db/changelog/changeset/post_V004_addcolumn.sql',
                   'db/changelog/changeset/post_V005_verification.sql',
                   'db/changelog/changeset/post_V006_comments_verification.sql',
                   'db/changelog/changeset/post_V007_alter_post_ad.sql');