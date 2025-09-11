-- Update timestamp on USERS
CREATE OR REPLACE TRIGGER TRG_USERS_UPD
BEFORE UPDATE ON USERS
FOR EACH ROW
BEGIN
  :NEW.UPDATED_AT := SYSTIMESTAMP;
END;
/



CREATE OR REPLACE PROCEDURE CREATE_SESSION(
  p_user_id      IN  NUMBER,
  p_session_date IN  DATE,
  p_duration     IN  NUMBER,
  p_notes        IN  VARCHAR2,
  p_session_id   OUT NUMBER
) AS
BEGIN
  INSERT INTO WORKOUT_SESSION(USER_ID, SESSION_DATE, DURATION_MIN, NOTES)
  VALUES (p_user_id, NVL(TRUNC(p_session_date), TRUNC(SYSDATE)), p_duration, p_notes)
  RETURNING SESSION_ID INTO p_session_id;
END;
/

CREATE OR REPLACE PROCEDURE ADD_SET(
    p_session_id IN NUMBER,
    p_exercise_id IN NUMBER,
    p_set_no IN NUMBER,
    p_reps IN NUMBER DEFAULT NULL,
    p_weight_kg IN NUMBER DEFAULT NULL,
    p_duration_min IN NUMBER DEFAULT NULL,
    p_dist_km IN NUMBER DEFAULT NULL,
    p_steps IN NUMBER DEFAULT NULL
) IS
BEGIN
    INSERT INTO WORKOUT_EXERCISE (
        SESSION_ID, EXERCISE_ID, SET_NO, REPS, WEIGHT_KG, 
        DURATION_MIN, DIST_KM, STEPS_COUNT
    ) VALUES (
        p_session_id, p_exercise_id, p_set_no, p_reps, p_weight_kg,
        p_duration_min, p_dist_km, p_steps
    );
END;
/
-- SELECT * 
-- FROM employees 
-- WHERE manager_id IS NULL;