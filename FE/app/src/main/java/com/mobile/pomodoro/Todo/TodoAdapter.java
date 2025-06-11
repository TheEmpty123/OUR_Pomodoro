package com.mobile.pomodoro.Todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.pomodoro.R;
import com.mobile.pomodoro.entity.TodoItem;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private final List<TodoItem> todoList;
    private final TodoItemListener listener;

    public interface TodoItemListener {
        void onTodoChecked(TodoItem item, boolean isChecked);
        void onTodoClicked(TodoItem item);
    }
    //lấy ds và xử lý khi tương tác
    public TodoAdapter(List<TodoItem> todoList, TodoItemListener listener) {
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

    //    Gán dl vào item (= bind)
    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        TodoItem item = todoList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final CheckBox cbDone;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTodoTitle);
            cbDone = itemView.findViewById(R.id.cbTodoDone);
        }

        //  Gán dl todo vào view
        public void bind(TodoItem item, TodoItemListener listener) {
            tvTitle.setText(item.getTitle());
            cbDone.setChecked(item.isDone());

            cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isPressed()) {
                    listener.onTodoChecked(item, isChecked);
                }
            });

            itemView.setOnClickListener(v -> listener.onTodoClicked(item));
        }
    }
}