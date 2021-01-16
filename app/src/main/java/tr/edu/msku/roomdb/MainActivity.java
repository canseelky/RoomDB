package tr.edu.msku.roomdb;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DepartmentFragment.DepartmentListener {

    boolean displayingDepartment = false;
    Department selectedDepartment;
    ArrayList<Department> departments;
    UniversityDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Room.databaseBuilder(this, UniversityDB.class,"uni").build();
        new Thread(){
            public void run(){
                departments = retrieveDepartments();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        androidx.fragment.app.FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
                        ft.add(R.id.container, DepartmentFragment.newInstance(departments), "departments");
                        ft.commit();
                    }
                });
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected", item.getTitle().toString());
        switch (item.getItemId()) {
            case R.id.new2:
                displayingDepartment = !displayingDepartment;
                invalidateOptionsMenu();
                selectedDepartment = new Department();
                departments.add(selectedDepartment);
                androidx.fragment.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                ft.replace(R.id.container,EditDepartmentFragment.newInstance(selectedDepartment, new ArrayList<Course>()),"edit_department");
                ft.addToBackStack(null);
                ft.commit();
                return true;
            case R.id.save:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("LongLogTag")
    public boolean onPrepareOptionsMenu(Menu menu){
        Log.d("onPrepareOptionsMenu new visible", menu.findItem(R.id.new2).isVisible() + "");
        menu.findItem(R.id.new2).setVisible(!displayingDepartment);
        menu.findItem(R.id.save).setVisible(displayingDepartment);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        displayingDepartment = !displayingDepartment;
        invalidateOptionsMenu();
        EditDepartmentFragment editFragment = (EditDepartmentFragment) getSupportFragmentManager().findFragmentByTag("edit_department");
        if (editFragment != null){
            final Department department = editFragment.getDepartment();
            final ArrayList<Course> courses = editFragment.getCourses();
            new Thread(){
                public void run(){
                    saveDepartment(department, courses);
                    departments = retrieveDepartments();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DepartmentFragment departmentFragment = (DepartmentFragment) getSupportFragmentManager().findFragmentByTag("departments");
                            departmentFragment.setDepartments(departments);
                        }
                    });
                }
            }.start();
        }
        super.onBackPressed();
    }
    @Override
    public void onDepartmentSelected(Department department) {
        selectedDepartment = department;
        new Thread(){
            public void run(){
                final ArrayList<Course> courses = getCourses(selectedDepartment);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                        ft.replace(R.id.container,EditDepartmentFragment.newInstance(selectedDepartment, courses),"edit_department");
                        ft.addToBackStack(null);
                        ft.commit();
                        displayingDepartment = !displayingDepartment;
                        invalidateOptionsMenu();
                    }
                });
            }
        }.start();
    }

    public ArrayList<Department> retrieveDepartments(){
        UniversityDAO dao = database.getDAO();
        return new ArrayList<>( dao.getAllDepartments());
    }

    private void saveDepartment(Department department, List<Course> courses) {
        UniversityDAO dao = database.getDAO();
        long deptId = dao.insertDepartment(department);
        for (Course course : courses){
            course.deptId = deptId;
        }
        dao.insertCourses(courses.toArray(new Course[0]));
    }

    private ArrayList<Course> getCourses(Department department) {
        UniversityDAO dao = database.getDAO();
        return new ArrayList<>(dao.getCourses(department.id));
    }

}