package com.elice.nbbang.global.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class TaskManager<T> {
    private Set<T> taskSet = ConcurrentHashMap.newKeySet();

    public boolean addTask(T task) {
        return taskSet.add(task);
    }

    public void removeTask(T task) {
        taskSet.remove(task);
    }
}
