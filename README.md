https://drive.google.com/drive/folders/1aewCy-2OgiPCPDVAz0Q-j9jI8_u4LL5x?usp=sharing
# Health Tracker Application

**Student:** Maksimov Maksim

**Course:** OOP (Java)

**Date:** 19/04/2026

---

## Description

Health Tracker is a Java desktop application that allows users to manage personal health data including weight, blood pressure, and exercise activities. The application features a graphical user interface (Swing), role-based access control (Admin/User), and persistent data storage using SQLite database.

---

## Objectives

- Implement a working desktop application using Java
- Apply OOP principles (encapsulation, inheritance, polymorphism, abstraction)
- Create a user-friendly GUI with Swing components (JFrame, JList, JComboBox)
- Provide data persistence using SQLite database
- Implement input validation and error handling
- Demonstrate role-based access control

---

## Project Requirements (10 key features)

1. **Add health records** – Users can add Weight, Blood Pressure, or Exercise records
2. **Update existing records** – Modify previously saved records
3. **Delete records** – Remove unwanted entries (Admin only)
4. **View all records** – Display records in a scrollable JList
5. **Input validation** – Date format (YYYY-MM-DD), weight range (0-500 kg), blood pressure format (120/80), exercise text (3-30 letters)
6. **SQLite database storage** – Persistent storage in `health.db`
7. **Login system with roles** – Admin (full access) / User (read-only)
8. **Swing GUI** – User-friendly interface with buttons and form fields
9. **Save/Load from database** – Manual save and load buttons
10. **Auto-load on startup** – Records automatically load when application starts

---

## Documentation

### Class Hierarchy (OOP Structure)
HealthRecord (abstract)
├── WeightRecord
├── BloodPressureRecord
└── ExerciseRecord

text

### Classes and Their Purpose

| Class | Purpose |
|-------|---------|
| `HealthRecord` | Abstract base class with `date` field and abstract `toFile()` method |
| `WeightRecord` | Stores weight in kg, implements `toFile()` and `toString()` |
| `BloodPressureRecord` | Stores blood pressure as string (e.g., "120/80") |
| `ExerciseRecord` | Stores exercise description |
| `DatabaseManager` | Handles SQLite connection, table creation, save/load operations |
| `MainGUI` | Main application window, event handling, login system |

### Key Methods

| Method | Class | Purpose |
|--------|-------|---------|
| `toFile()` | HealthRecord (abstract) | Converts record to CSV format for storage |
| `toString()` | All record classes | Formats record for display in JList |
| `save(ArrayList<HealthRecord>)` | DatabaseManager | Saves all records to SQLite |
| `load()` | DatabaseManager | Loads all records from SQLite |
| `init()` | DatabaseManager | Creates database table if not exists |
| `performLogin()` | MainGUI | Shows login dialog, returns role |
| `addRecord()` | MainGUI | Validates input, creates record |
| `updateRecord()` | MainGUI | Updates selected record |
| `deleteRecord()` | MainGUI | Removes selected record |

### Algorithms

#### 1. Polymorphic Storage (DatabaseManager.save)
For each HealthRecord in ArrayList:
if record instanceof WeightRecord → type="W", value=weight
if record instanceof BloodPressureRecord → type="B", value=pressure
if record instanceof ExerciseRecord → type="E", value=exercise
INSERT INTO records (type, date, value)

text

#### 2. Polymorphic Loading (DatabaseManager.load)
Execute SELECT * FROM records
For each row:
if type="W" → new WeightRecord(date, Double.parseDouble(value))
if type="B" → new BloodPressureRecord(date, value)
if type="E" → new ExerciseRecord(date, value)
Add to ArrayList

text

#### 3. Input Validation (addRecord method)
- **Date:** `LocalDate.parse(d)` – throws exception if invalid format
- **Weight:** `Double.parseDouble(val)` + range check (0-500)
- **Blood Pressure:** Regex `\d{2,3}/\d{2,3}` (e.g., "120/80")
- **Exercise:** Regex `[a-zA-Z ]{3,30}` (only letters and spaces)

### Database Schema

```sql
CREATE TABLE IF NOT EXISTS records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    type TEXT NOT NULL,      -- 'W', 'B', or 'E'
    date TEXT NOT NULL,      -- YYYY-MM-DD format
    value TEXT NOT NULL      -- weight, pressure, or exercise text
);
```

Role-Based UI Control
```java
if (!currentRole.equals("Admin")) {
    updateBtn.setEnabled(false);
    deleteBtn.setEnabled(false);
}
```
}
Challenges Faced and Solutions
Polymorphic database storage
→ Store type code + value as text; reconstruct using instanceof during load.

Date validation
→ Use LocalDate.parse() with try-catch.

Role-based UI updates
→ Check role after login; disable buttons before showing frame.

JList selection event firing twice
→ Check !e.getValueIsAdjusting() in ListSelectionListener.

SQL injection risk
→ Current code uses string concatenation – potential improvement: use PreparedStatement.

Manual save/load required
→ Buttons provided; auto-save not implemented.

Test Cases and Expected Outputs
1. Login Admin

Input: admin / admin

Output: Main window with "Admin mode", all buttons enabled.

2. Login User

Input: user / user

Output: Main window with "User mode", Update/Delete disabled.

3. Add Weight

Input: Date 2026-04-19, Value 75.5, Type Weight

Output: Record shows Weight | 2026-04-19 | 75.5 kg.

4. Add Blood Pressure

Input: Date 2026-04-19, Value 120/80, Type BloodPressure

Output: Record shows BP | 2026-04-19 | 120/80.

5. Add Exercise

Input: Date 2026-04-19, Value Running, Type Exercise

Output: Record shows Exercise | 2026-04-19 | Running.

6. Invalid Date

Input: 19-04-2026

Output: Error dialog "Неверный формат даты! Используйте YYYY-MM-DD".

7. Invalid Weight

Input: -10

Output: Error dialog "Ошибка ввода! Проверьте данные."

8. Invalid Blood Pressure

Input: 1200/800

Output: Error dialog "Ошибка ввода! Проверьте данные."

9. Update Record

Input: Select record → change date → click Update

Output: Record updates in list.

10. Delete Record (Admin)

Input: Select record → click Delete

Output: Record removed from list.

11. Save to DB

Input: Click "Save to DB"

Output: Dialog "Сохранено в базу данных (health.db)".

12. Load from DB

Input: Click "Load from DB"

Output: Dialog "Загружено из базы данных".

13. Data Persistence

Input: Add record → Save → Close → Restart → Load

Output: Previously added records reappear.

Files Used for Storage
health.db – SQLite database with records table. Created automatically in project root.

health_records.txt – Legacy file from initial commit (not used in current version).

How to Run
Add SQLite JDBC driver (sqlite-jdbc-3.36.0.jar) to classpath (already in /src).

Compile all Java files.

Run MainGUI.java.

Login with:

Admin: admin / admin

User: user / user

Sample Session
text
Login: admin / admin
→ Add Weight: 2026-04-19, 75.5
→ Add BP: 2026-04-19, 120/80
→ Save to DB
→ Close application
→ Restart, 

login as user
→ Load from DB
→ Records appear (Update/Delete disabled)
