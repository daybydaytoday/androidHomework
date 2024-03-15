package com.example.fragment7.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fragment7.R;
import com.example.fragment7.activity.CrimeActivity;
import com.example.fragment7.model.Crime;
import com.example.fragment7.model.CrimeLab;

import java.util.List;


public class CrimeListFragment extends Fragment {
    private RecyclerView crimeRecyclerView;
    private CrimeAdapter crimeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        crimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    private void updateUI() {

        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (crimeAdapter == null) {
            crimeAdapter = new CrimeAdapter(crimes);
            crimeRecyclerView.setAdapter(crimeAdapter);

        }else{
            crimeAdapter.notifyDataSetChanged();
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Crime mcrime;
        private ImageView sk;

        public CrimeHolder(LayoutInflater layoutInflater, ViewGroup parent) {
            super(layoutInflater.inflate(R.layout.list_crime_item, parent, false));
            itemView.setOnClickListener(this);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date_textview);
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title_textview);
            sk = itemView.findViewById(R.id.sk);
        }

        public void bind(Crime crime) {
            this.mcrime = crime;
            mTitleTextView.setText(crime.getmTitle());
            mDateTextView.setText(crime.getFormatDate());
            sk.setVisibility(crime.getmSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(getActivity(),mcrime.getmTitle() + " clicked!", Toast.LENGTH_SHORT).show();
            // 准备启动活动
            //Intent intent = new Intent(getActivity(), CrimeActivity.class);
            Intent intent = CrimeActivity.newIntent(getActivity(), mcrime.getmId());
            startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {  // 总的创建多少个FragmentItem
            return mCrimes.size();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

}