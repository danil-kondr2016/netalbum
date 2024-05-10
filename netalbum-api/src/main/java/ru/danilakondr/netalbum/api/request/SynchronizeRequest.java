package ru.danilakondr.netalbum.api.request;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import ru.danilakondr.netalbum.api.data.Change;

import java.util.List;

public class SynchronizeRequest extends Request {
    private List<Change> changes;

    @JsonSetter("changes")
    public List<Change> getChanges() {
        return changes;
    }

    @JsonGetter("images")
    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }
}
