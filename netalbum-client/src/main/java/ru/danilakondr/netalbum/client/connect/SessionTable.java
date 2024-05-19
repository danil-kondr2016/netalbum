/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.connect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author danko
 */
public class SessionTable implements TableModel {
    private final List<Session> sessionList;
    private final Set<TableModelListener> listeners;
    private static final String[] COLUMN_NAMES = new String[]{
        "URL-адрес сервера", "Ключ сессии", "Путь к папке с фотографиями"
    };
    
    public SessionTable() {
        this.sessionList = new ArrayList<>();
        this.listeners = new HashSet<>();
    }

    @Override
    public int getRowCount() {
        return sessionList.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return COLUMN_NAMES[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return sessionList.get(rowIndex).getUrl();
            case 1:
                return sessionList.get(rowIndex).getSessionId();
            case 2:
                return sessionList.get(rowIndex).getPath();
            default:
                throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                sessionList.get(rowIndex).setUrl(Objects.toString(aValue));
                break;
            case 1:
                sessionList.get(rowIndex).setSessionId(Objects.toString(aValue));
                break;
            case 2:
                sessionList.get(rowIndex).setPath(Objects.toString(aValue));
                break;
            default:
                throw new ArrayIndexOutOfBoundsException();
        }
        
        notifyAboutUpdate(rowIndex, columnIndex);
    }
    
    private void notifyAboutUpdate(int rowIndex, int columnIndex) {
        TableModelEvent evt = new TableModelEvent(this, rowIndex, columnIndex);

        for (TableModelListener l: listeners) {
            l.tableChanged(evt);
        }
    }
    
    private void notifyAboutDelete(int row) {
        TableModelEvent evt = new TableModelEvent(this, 
                row, row, 
                TableModelEvent.ALL_COLUMNS, 
                TableModelEvent.DELETE);

        for (TableModelListener l: listeners) {
            l.tableChanged(evt);
        }
    }
    
    private void notifyAboutAdd(int row) {
        TableModelEvent evt = new TableModelEvent(this, 
                row, row, 
                TableModelEvent.ALL_COLUMNS, 
                TableModelEvent.INSERT);

        for (TableModelListener l: listeners) {
            l.tableChanged(evt);
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }
    
    public void addSession(Session session) {
        sessionList.add(session);
        notifyAboutAdd(sessionList.size() - 1);
    }
    
    public void removeSession(Session session) {
        int index = sessionList.indexOf(session);
        sessionList.remove(index);
        notifyAboutDelete(index);
    }
}
