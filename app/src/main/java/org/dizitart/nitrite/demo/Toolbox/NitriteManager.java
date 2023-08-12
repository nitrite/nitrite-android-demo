package org.dizitart.nitrite.demo.Toolbox;

import static org.dizitart.no2.common.util.Iterables.setOf;

import android.content.Context;

import org.dizitart.nitrite.demo.Model.TodoItem;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.common.mapper.SimpleDocumentMapper;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.dizitart.no2.repository.ObjectRepository;

public class NitriteManager {
    private static NitriteManager _instance = new NitriteManager();
    private static final String dbName = "task.db";
    private static Nitrite db;

    private NitriteManager() {}

    public static NitriteManager getInstance() {
        return _instance;
    }

    private void openDb(Context context) {
        if (db != null) {
            db.close();
        }

        MVStoreModule storeModule = MVStoreModule.withConfig()
                .filePath(context.getDatabasePath(dbName))
                .build();
        SimpleDocumentMapper documentMapper = new SimpleDocumentMapper();
        documentMapper.registerEntityConverter(new TodoItem.Converter());

        db = Nitrite.builder()
                .loadModule(storeModule)
                .loadModule(() -> setOf(documentMapper))
                .openOrCreate();
    }

    public ObjectRepository<TodoItem> getTodoRepository(Context context, String label) {
        if (db == null) {
            openDb(context);
        }

        return db.getRepository(TodoItem.class, label);
    }
}
