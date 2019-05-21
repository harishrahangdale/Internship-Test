package com.selfstudy.internship;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final String URL = "https://fruitvezi.com/harish/resource.json";

    private RecyclerView recyclerView;
    private List<Subject> subjectList;
    private HomeAdapter mAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        subjectList = new ArrayList<>();
        mAdapter = new HomeAdapter(getActivity(), subjectList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        fetchStoreItems();

        return view;
    }

    private void fetchStoreItems() {
        JsonArrayRequest request = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getActivity(), "Couldn't fetch the home items! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<Subject> items = new Gson().fromJson(response.toString(), new TypeToken<List<Subject>>() {
                        }.getType());

                        subjectList.clear();
                        subjectList.addAll(items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }


    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
        private Context context;
        private List<Subject> subjectList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name, progress;
            public ImageView thumbnail;
            public ProgressBar pb;

            public MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.textViewTitle);
                progress = view.findViewById(R.id.progress_percentage);
                thumbnail = view.findViewById(R.id.imageView);
                pb = view.findViewById(R.id.pb);
            }
        }


        public HomeAdapter(Context context, List<Subject> subjectList) {
            this.context = context;
            this.subjectList = subjectList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subject_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final Subject subject = subjectList.get(position);
            holder.name.setText(subject.getTitle());
            holder.progress.setText(subject.getProgress() + "%");
            // Max Progress
            holder.pb.setMax(100);
            // Progress Percentage
            holder.pb.setProgress(Integer.parseInt(subject.getProgress()));
            holder.pb.setScaleY(2f);

            Glide.with(context)
                    .load(subject.getImage())
                    .into(holder.thumbnail);
        }

        @Override
        public int getItemCount() {
            return subjectList.size();
        }
    }
}