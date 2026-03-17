package com.example.androidtest;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidtest.adapter.TaskAdapter;
import com.example.androidtest.data.db.TaskEntity;
import com.example.androidtest.ui.viewmodel.TaskListViewModel;
import com.google.gson.JsonParseException;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TaskAdapter adapter;
    private View emptyView;
    private View rootView;

    private TaskListViewModel viewModel;

    private ActivityResultLauncher<String> exportLauncher;
    private ActivityResultLauncher<String[]> importLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        rootView = findViewById(R.id.main);
        emptyView = findViewById(R.id.emptyView);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(TaskListViewModel.class);

        exportLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("application/json"), this::onExportUri);
        importLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), this::onImportUri);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_export) {
                exportLauncher.launch("todo-backup.json");
                return true;
            }
            if (id == R.id.action_import) {
                confirmImportOverwrite();
                return true;
            }
            return false;
        });

        adapter = new TaskAdapter(new TaskAdapter.Listener() {
            @Override
            public void onDoneToggled(TaskEntity task, boolean isDone) {
                viewModel.toggleDone(task, isDone);
            }

            @Override
            public void onItemClicked(TaskEntity task) {
                startActivity(EditTaskActivity.createIntent(MainActivity.this, task.id));
            }
        });
        recyclerView.setAdapter(adapter);

        viewModel.getTasks().observe(this, tasks -> {
            List<TaskEntity> safe = tasks == null ? Collections.emptyList() : tasks;
            adapter.setTasks(safe);
            updateEmptyState(safe);
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }

                TaskEntity removed = adapter.getTaskAt(position);
                viewModel.delete(removed);

                Snackbar.make(rootView, R.string.deleted, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, v -> viewModel.insert(removed))
                        .show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        findViewById(R.id.fabAdd).setOnClickListener(v -> startActivity(EditTaskActivity.createIntent(this, 0L)));
    }

    private void updateEmptyState(List<TaskEntity> tasks) {
        boolean isEmpty = tasks == null || tasks.isEmpty();
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void confirmImportOverwrite() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.import_overwrite_title)
                .setMessage(R.string.import_overwrite_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.import_json, (d, w) -> importLauncher.launch(new String[]{"application/json", "text/*"}))
                .show();
    }

    private void onExportUri(Uri uri) {
        if (uri == null) {
            return;
        }
        viewModel.exportToJson(getContentResolver(), uri, new com.example.androidtest.data.repo.TaskRepository.ResultCallback() {
            @Override
            public void onSuccess() {
                Snackbar.make(rootView, R.string.export_success, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onError(Exception e) {
                showBackupError(e);
            }
        });
    }

    private void onImportUri(Uri uri) {
        if (uri == null) {
            return;
        }
        viewModel.importFromJsonOverwrite(getContentResolver(), uri, new com.example.androidtest.data.repo.TaskRepository.ResultCallback() {
            @Override
            public void onSuccess() {
                Snackbar.make(rootView, R.string.import_success, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onError(Exception e) {
                showBackupError(e);
            }
        });
    }

    private void showBackupError(Exception e) {
        int message;
        if (e instanceof JsonParseException) {
            message = R.string.failed_json;
        } else if (e instanceof IllegalArgumentException) {
            String m = e.getMessage();
            if (m != null && m.startsWith("Unsupported version:")) {
                message = R.string.failed_version;
            } else {
                message = R.string.failed_json;
            }
        } else if (e instanceof IllegalStateException) {
            message = R.string.failed_io;
        } else {
            message = R.string.failed;
        }
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }
}