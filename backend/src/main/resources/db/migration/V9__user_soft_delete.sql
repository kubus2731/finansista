-- Soft delete użytkowników: zamiast fizycznego DELETE (łamałby FK do requests,
-- comments, activity_log i niszczył audyt) wprowadzamy flagę aktywności.
-- "Usunięty" użytkownik = active = FALSE: nie może się zalogować i nie jest
-- pokazywany na listach, ale jego wnioski i historia pozostają nienaruszone.
ALTER TABLE users ADD active BOOLEAN DEFAULT TRUE NOT NULL;
