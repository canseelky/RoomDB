package tr.edu.msku.roomdb;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UniversityDAO {


    @Query("Select * from department")
    List<Department> getAllDepartments();

    @Query("Select * from course where deptId = :deptId")
    List<Course> getCourses(long deptId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDepartment(Department dept);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourses(Course... courses);

    @Update
    void updateDepartment(Department dept);

    @Update
    void updateCourse(Course dept);

    @Delete
    void deleteDepartment(Department dept);

    @Delete
    void deleteCourse(Course dept);
}
