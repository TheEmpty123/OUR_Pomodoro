package com.mobile.pomodoro.Todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.pomodoro.R;
import com.mobile.pomodoro.response_dto.TodoResponseDTO;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private final List<TodoResponseDTO> todoList;
    private final TodoItemListener listener;

    public interface TodoItemListener {
        void onTodoChecked(TodoResponseDTO item, boolean isChecked);
        void onTodoClicked(TodoResponseDTO item);  // sự kiện nhấn vào 1 cái todo trong list để sửa
    }
    // constructor lấy ds và xử lý khi tương tác
    public TodoAdapter(List<TodoResponseDTO> todoList, TodoItemListener listener) {
        this.todoList = todoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Hiển thij layout cho từng item_todo
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    //    Gán dl vào ds item (= bind)
    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        TodoResponseDTO item = todoList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return todoList != null ? todoList.size() : 0;
    }
    // Tính ra số todo trong ds để hiển thị bấy nhiêu cái item todo


    static class TodoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final CheckBox cbDone;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTodoTitle);
            cbDone = itemView.findViewById(R.id.cbTodoDone);
        }

        //  Gán dl todo vào view
        public void bind(TodoResponseDTO item, TodoItemListener listener) {
            if (item == null || listener == null)  return;

            tvTitle.setText(item.getTitle());
            cbDone.setOnCheckedChangeListener(null);
            cbDone.setChecked(item.getIs_done() ==1);

            cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setIs_done(isChecked ? 1 : 0);
                listener.onTodoChecked(item, isChecked);

            });

            itemView.setOnClickListener(v -> listener.onTodoClicked(item));
        }
    }
}