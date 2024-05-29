/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.connect;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author danko
 */
public class SessionTable extends AbstractTableModel {
    private final List<Session> sessionList;
    private static final ResourceBundle bundle = ResourceBundle.getBundle("ru/danilakondr/netalbum/client/connect/Strings");
    private static final String[] COLUMN_NAMES = new String[]{
        bundle.getString("sessionTable.URL"),
        bundle.getString("sessionTable.sessionKey"),
        bundle.getString("sessionTable.folderPath"),
        bundle.getString("sessionTable.connectionType")
    };
    
    public SessionTable() {
        super();
        this.sessionList = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return sessionList.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
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
            case 3:
                Session.Type t = sessionList.get(rowIndex).getSessionType();
                if (t == null)
                    return "";
                return t.getLocalizedName();
            default:
                throw new ArrayIndexOutOfBoundsException();
        }
    }

    public void addSession(Session session) {
        sessionList.add(session);
        fireTableRowsInserted(sessionList.size()-1, sessionList.size()-1);
    }
    
    public void removeSession(Session session) {
        int index = sessionList.indexOf(session);
        if (index == -1)
            return;
        
        sessionList.remove(index);
        fireTableRowsDeleted(index, index);
    }
    
    public Session getSessionAt(int index) {
        return sessionList.get(index);
    }
}
