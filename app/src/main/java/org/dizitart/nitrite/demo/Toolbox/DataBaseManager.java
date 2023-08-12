package org.dizitart.nitrite.demo.Toolbox;

import static org.dizitart.no2.filters.FluentFilter.where;

import android.content.Context;

import org.dizitart.nitrite.demo.Model.TodoItem;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.FindOptions;
import org.dizitart.no2.common.SortOrder;

import java.util.List;

public class DataBaseManager {

    private final NitriteManager nitriteManager;
    private final String label;

    public DataBaseManager(String label) {
        this.nitriteManager = NitriteManager.getInstance();
        this.label = label;
    }

    public void addTask(Context context, int id, boolean status, String taskName, String taskColor) {
        TodoItem todoItem = new TodoItem();
        todoItem.setId(id);
        todoItem.setColor(taskColor);
        todoItem.setStatus(status);
        todoItem.setTaskName(taskName);
        nitriteManager.getTodoRepository(context, label).insert(todoItem);
    }

    public void removeTask(Context context, int id) {
        nitriteManager.getTodoRepository(context, label).remove(where("id").eq(id));
    }

    public void updateTask(Context context, Boolean taskStatus, int id, String taskName) {
        Document updateDocument = Document.createDocument()
                .put("status", taskStatus)
                .put("taskName", taskName);
        nitriteManager.getTodoRepository(context, label).update(where("id").eq(id), updateDocument);
    }

    public List<TodoItem> readFromDB(Context context) {
        return nitriteManager.getTodoRepository(context, label).find().toList();
    }

    public int getCurrentBiggestId(Context context) {
        TodoItem item = nitriteManager.getTodoRepository(context, label).find(
                FindOptions.orderBy("id", SortOrder.Descending)).firstOrNull();
        if (item == null) return 0;
        return item.getId();
    }
}
