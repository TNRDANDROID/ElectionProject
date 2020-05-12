package com.nic.electionproject.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.electionproject.R;
import com.nic.electionproject.activity.AttendanceScreen;
import com.nic.electionproject.databinding.AttendanceAdapterScreenBinding;
import com.nic.electionproject.pojo.ElectionProject;

import java.util.List;

public class AttendanceNameListAdapter extends PagedListAdapter<ElectionProject, AttendanceNameListAdapter.MyViewHolder> {
    private List<ElectionProject> attendanceNameList;
    private String letter;
    private Context context;
    int count = 0;


    private LayoutInflater layoutInflater;
    private static DiffUtil.ItemCallback<ElectionProject> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ElectionProject>() {
                @Override
                public boolean areItemsTheSame(ElectionProject oldItem, ElectionProject newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(ElectionProject oldItem, ElectionProject newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public AttendanceNameListAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
//        this.attendanceNameList = attendanceNameList;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private AttendanceAdapterScreenBinding attendanceAdapterScreenBinding;

        public MyViewHolder(AttendanceAdapterScreenBinding Binding) {
            super(Binding.getRoot());

            attendanceAdapterScreenBinding = Binding;
        }


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        AttendanceAdapterScreenBinding attendanceAdapterScreenBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.attendance_adapter_screen, viewGroup, false);
        return new MyViewHolder(attendanceAdapterScreenBinding);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

//        holder.attendanceAdapterScreenBinding.card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.attendanceAdapterScreenBinding.checkBox.setChecked(!holder.attendanceAdapterScreenBinding.checkBox.isChecked());
//                if (holder.attendanceAdapterScreenBinding.checkBox.isChecked()) {
//
//                    holder.attendanceAdapterScreenBinding.card.setCardBackgroundColor(Color.parseColor("#00BFA5"));
////                    holder.attendanceAdapterScreenBinding.checkBox.setBackgroundColor(Color.WHITE);
//                    holder.attendanceAdapterScreenBinding.staffName.setTextColor(Color.WHITE);
//
//                  countPlus();
//                    holder.attendanceAdapterScreenBinding.checkBox.setClickable(false);
//                } else {
//
//                    holder.attendanceAdapterScreenBinding.card.setCardBackgroundColor(Color.WHITE);
//                    holder.attendanceAdapterScreenBinding.staffName.setTextColor(Color.BLACK);
//
//                   countMinus();
//                    holder.attendanceAdapterScreenBinding.checkBox.setClickable(true);
//                }
//            }
//
//        });

        holder.attendanceAdapterScreenBinding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                if (ischecked) {
                    holder.attendanceAdapterScreenBinding.card.setCardBackgroundColor(Color.parseColor("#00BFA5"));
//                    holder.attendanceAdapterScreenBinding.checkBox.setBackgroundColor(Color.WHITE);
                    holder.attendanceAdapterScreenBinding.staffName.setTextColor(Color.WHITE);
                    countPlus();
                } else {
                    holder.attendanceAdapterScreenBinding.card.setCardBackgroundColor(Color.WHITE);
                    holder.attendanceAdapterScreenBinding.staffName.setTextColor(Color.BLACK);
                    countMinus();

                }
            }
        });

//        holder.attendanceAdapterScreenBinding.staffName.setText(attendanceNameList.get(position).getStaffName());

//        letter = String.valueOf(attendanceNameList.get(position).getStaffName().charAt(0));
//
//        TextDrawable drawable = TextDrawable.builder()
//                .buildRound(letter, generator.getRandomColor());
//
//        holder.attendanceAdapterScreenBinding.villageFirstLetter.setImageDrawable(drawable);

    }

    public void countPlus() {
        count++;
        ((AttendanceScreen) context).setHeaderCount(count);
    }

    public void countMinus() {
        count--;
        ((AttendanceScreen) context).setHeaderCount(count);
    }

    @Override
    public int getItemCount() {
        return 20;
    }
}
