-- Update timestamp on USERS
CREATE OR REPLACE TRIGGER TRG_USERS_UPD
BEFORE UPDATE ON USERS
FOR EACH ROW
BEGIN
  :NEW.UPDATED_AT := SYSTIMESTAMP;
END;
/

-- BMI helper (kg & cm in)
CREATE OR REPLACE FUNCTION CALC_BMI(p_weight_kg NUMBER, p_height_cm NUMBER)
RETURN NUMBER
IS
BEGIN
  IF p_weight_kg IS NULL OR p_height_cm IS NULL OR p_height_cm = 0 THEN
    RETURN NULL;
  END IF;
  RETURN ROUND(p_weight_kg / POWER(p_height_cm/100, 2), 2);
END;
/

-- Auto-populate BMI in BODY_METRICS using height from USERS
CREATE OR REPLACE TRIGGER TRG_BMI_BODY_METRICS
BEFORE INSERT OR UPDATE OF WEIGHT_KG ON BODY_METRICS
FOR EACH ROW
DECLARE
  v_height NUMBER;
BEGIN
  SELECT HEIGHT_CM INTO v_height FROM USERS WHERE USER_ID = :NEW.USER_ID;
  :NEW.BMI := CALC_BMI(:NEW.WEIGHT_KG, v_height);
EXCEPTION
  WHEN NO_DATA_FOUND THEN :NEW.BMI := NULL;
END;
/

-- Procedure to create a workout session and return ID
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

-- Procedure to add a set into a session
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
-- Daily summary view (workout minutes + calories)
CREATE OR REPLACE VIEW V_DAILY_SUMMARY AS
SELECT
  u.USER_ID,
  d.the_date,
  NVL(ws.workout_min, 0) AS workout_min,
  NVL(nl.calories, 0)    AS calories
FROM USERS u
CROSS JOIN (
  SELECT TRUNC(SYSDATE - LEVEL + 1) AS the_date
  FROM dual CONNECT BY LEVEL <= 365
) d
LEFT JOIN (
  SELECT USER_ID, SESSION_DATE, SUM(NVL(DURATION_MIN,0)) AS workout_min
  FROM WORKOUT_SESSION
  GROUP BY USER_ID, SESSION_DATE
) ws ON ws.USER_ID = u.USER_ID AND ws.SESSION_DATE = d.the_date
LEFT JOIN (
  SELECT USER_ID, LOG_DATE, SUM(NVL(CALORIES,0)) AS calories
  FROM NUTRITION_LOG
  GROUP BY USER_ID, LOG_DATE
) nl ON nl.USER_ID = u.USER_ID AND nl.LOG_DATE = d.the_date;


--------------------------------------
CREATE OR REPLACE FUNCTION CALCULATE_STEP_METRICS(
    p_steps NUMBER,
    p_activity_type VARCHAR2,
    p_user_weight_kg NUMBER DEFAULT 70,
    p_duration_min NUMBER
) RETURN SYS_REFCURSOR
IS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT 
            p_steps as STEPS_COUNT,
            ROUND(p_steps / sc.STEPS_PER_KM, 2) as DISTANCE_KM,
            ROUND(sc.BASE_MET_VALUE * p_user_weight_kg * (p_duration_min/60), 0) as CALORIES_BURNED,
            sc.BASE_MET_VALUE as MET_VALUE,
            ROUND(p_steps / sc.STEPS_PER_KM / (p_duration_min/60), 2) as AVG_SPEED_KMH
        FROM STEP_CONVERSION sc
        WHERE UPPER(sc.ACTIVITY_TYPE) = UPPER(p_activity_type);
    
    RETURN v_cursor;
END;
/