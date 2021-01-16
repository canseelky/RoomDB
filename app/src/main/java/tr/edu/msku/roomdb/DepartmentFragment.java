package tr.edu.msku.roomdb;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DepartmentFragment extends Fragment {

    private static final String ARG_DEPARTMENTS= "departments";
    private ArrayList<Department> departments;
    MyDepartmentRecyclerViewAdapter mAdapter;

    DepartmentListener mListener;

    public DepartmentFragment() {
    }


    @SuppressWarnings("unused")
    public static DepartmentFragment newInstance(ArrayList<Department> departments) {
        DepartmentFragment fragment = new DepartmentFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DEPARTMENTS, departments);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.departments = (ArrayList<Department>) getArguments().getSerializable(ARG_DEPARTMENTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deparment_list, container, false);


        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new MyDepartmentRecyclerViewAdapter(departments, mListener);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DepartmentListener) {
            mListener = (DepartmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " should implement DepartmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setDepartments(ArrayList<Department> departments) {
        this.departments.clear();
        this.departments.addAll(departments);
        mAdapter.notifyDataSetChanged();
    }

    public interface DepartmentListener {
        void onDepartmentSelected(Department item);
    }

}