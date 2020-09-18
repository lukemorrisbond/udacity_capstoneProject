package com.example.capstoneproject.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.capstoneproject.R;
import com.example.capstoneproject.data.TestViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class DashboardFragment extends Fragment {
    private static final String LOG_TAG = DashboardFragment.class.getSimpleName();

    private List<TextView> mDashboardProgressBarTextViewsList;
    private List<RecyclerView> mRecyclerViewsList;
    private List<View> mExpandButtonViewsList;
    private List<ImageButton> mExpandButtonsList;
    private List<TextView> mNoResultsTextViewsList;
    private List<LinearLayout> mRecyclerViewContainersList;

    private Context mContext;
    private TestViewModel mTestViewModel;

    private boolean[] isSelected = { false, false, false};
    private String[] mTestResultOptions = { "Positive", "Negative", "Inconclusive" };


    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            mTestViewModel = new ViewModelProvider(this.getActivity()).get(TestViewModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mDashboardProgressBarTextViewsList = new ArrayList<>();
        mDashboardProgressBarTextViewsList.add(view.findViewById(R.id.wheel_progress_positive_text));
        mDashboardProgressBarTextViewsList.add(view.findViewById(R.id.wheel_progress_negative_text));
        mDashboardProgressBarTextViewsList.add(view.findViewById(R.id.wheel_progress_inconclusive_text));

        mRecyclerViewsList = new ArrayList<>();
        mRecyclerViewsList.add(view.findViewById(R.id.recyclerview_positive));
        mRecyclerViewsList.add(view.findViewById(R.id.recyclerview_negative));
        mRecyclerViewsList.add(view.findViewById(R.id.recyclerview_inconclusive));

        mExpandButtonViewsList = new ArrayList<>();
        mExpandButtonViewsList.add(view.findViewById(R.id.expand_view_button_positive));
        mExpandButtonViewsList.add(view.findViewById(R.id.expand_view_button_negative));
        mExpandButtonViewsList.add(view.findViewById(R.id.expand_view_button_inconclusive));

        mExpandButtonsList = new ArrayList<>();
        mExpandButtonsList.add(view.findViewById(R.id.expand_view_button_image_positive));
        mExpandButtonsList.add(view.findViewById(R.id.expand_view_button_image_negative));
        mExpandButtonsList.add(view.findViewById(R.id.expand_view_button_image_inconclusive));

        mNoResultsTextViewsList = new ArrayList<>();
        mNoResultsTextViewsList.add(view.findViewById(R.id.no_test_results_positive));
        mNoResultsTextViewsList.add(view.findViewById(R.id.no_test_results_negative));
        mNoResultsTextViewsList.add(view.findViewById(R.id.no_test_results_inconclusive));

        mRecyclerViewContainersList = new ArrayList<>();
        mRecyclerViewContainersList.add(view.findViewById(R.id.recyclerview_positive_container));
        mRecyclerViewContainersList.add(view.findViewById(R.id.recyclerview_negative_container));
        mRecyclerViewContainersList.add(view.findViewById(R.id.recyclerview_inconclusive_container));

        for (int i = 0; i < mTestResultOptions.length; i++) {
            RecyclerView currentRecyclerView = mRecyclerViewsList.get(i);
            setUpExpandFunctionality(i);
            currentRecyclerView.setAdapter(new TestAdapter(getActivity()));
            currentRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            ((SimpleItemAnimator) currentRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            int finalI = i;
            mTestViewModel.getTestGroup(mTestResultOptions[i]).observe(getViewLifecycleOwner(), tests -> {
                TestAdapter adapter =(TestAdapter)currentRecyclerView.getAdapter();
                if (adapter != null) {
                    adapter.setTests(tests);
                }
                updateDashboardSummaryCard(finalI, tests.size());
                setRecyclerViewHeight(finalI);
            });
        }

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), RecordTestActivity.class);
            startActivity(intent);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        collapseAllRecyclerViews();
    }

    private void setUpExpandFunctionality(int index) {
        mExpandButtonViewsList.get(index).setOnClickListener(view -> {
            ImageButton expandButton = mExpandButtonsList.get(index);
            if (!isSelected[index]) {
                expandButton.setImageResource(R.drawable.ic_baseline_expand_less_24);
                isSelected[index] = true;
            }
            else {
                expandButton.setImageResource(R.drawable.ic_baseline_expand_more_24);
                isSelected[index] = false;
            }
            setRecyclerViewHeight(index);
        });
    }

    private void updateDashboardSummaryCard(int index, int size) {
        mDashboardProgressBarTextViewsList.get(index).setText(String.valueOf(size));
    }

    private void collapseAllRecyclerViews() {
        isSelected[0] = false;
        isSelected[1] = false;
        isSelected[2] = false;
    }

    private void setRecyclerViewHeight(int index) {
        if (mRecyclerViewsList.get(index).getAdapter() != null) {
            int listLength = mRecyclerViewsList.get(index).getAdapter().getItemCount();
            int itemHeight = 180;
            int maxHeight = 400;
            int containerHeight = Math.min(listLength * itemHeight, maxHeight);
            ViewGroup.LayoutParams recyclerViewParams = mRecyclerViewContainersList.get(index).getLayoutParams();
            ViewGroup.LayoutParams noResultsViewParams = mNoResultsTextViewsList.get(index).getLayoutParams();
            if (listLength > 0) {
                noResultsViewParams.height = 0;
                if (isSelected[index]) {
                    recyclerViewParams.height = containerHeight;
                } else {
                    recyclerViewParams.height = 0;
                }
                mRecyclerViewContainersList.get(index).setLayoutParams(recyclerViewParams);
            } else {
                if (isSelected[index]) {
                    noResultsViewParams.height = 60;
                } else {
                    noResultsViewParams.height = 0;
                }
            }
            mNoResultsTextViewsList.get(index).setLayoutParams(noResultsViewParams);
        }
    }
}