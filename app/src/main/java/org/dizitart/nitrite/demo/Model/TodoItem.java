package org.dizitart.nitrite.demo.Model;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.common.mapper.EntityConverter;
import org.dizitart.no2.common.mapper.NitriteMapper;
import org.dizitart.no2.repository.annotations.Entity;

@Entity
public class TodoItem {
    private Integer id;
    private String taskName;
    private Boolean status;
    private String color;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public static class Converter implements EntityConverter<TodoItem> {

        @Override
        public Class<TodoItem> getEntityType() {
            return TodoItem.class;
        }

        @Override
        public Document toDocument(TodoItem entity, NitriteMapper nitriteMapper) {
            return Document.createDocument()
                    .put("id", entity.getId())
                    .put("taskName", entity.getTaskName())
                    .put("status", entity.getStatus())
                    .put("color", entity.getColor());
        }

        @Override
        public TodoItem fromDocument(Document document, NitriteMapper nitriteMapper) {
            TodoItem item = new TodoItem();
            item.id = document.get("id", Integer.class);
            item.taskName = document.get("taskName", String.class);
            item.status = document.get("status", Boolean.class);
            item.color = document.get("color", String.class);
            return item;
        }
    }
}
