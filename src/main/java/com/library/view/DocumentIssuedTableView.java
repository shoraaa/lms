package com.library.view;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.library.model.Transaction;
import com.library.services.TransactionDAO;
import com.library.util.Localization;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DocumentIssuedTableView extends TransactionTableView {

    public DocumentIssuedTableView(TableView<Transaction> tableView) {
        super(tableView);
    }

    @Override
    protected List<TableColumn<Transaction, ?>> createColumns() {
        Localization localization = Localization.getInstance();
        List<Supplier<TableColumn<Transaction, ?>>> columnTasks = List.of(
            () -> createTextColumn(localization.getString("userId"), transaction -> new SimpleStringProperty(String.valueOf(transaction.getUserId()))),
            () -> createTextColumn(localization.getString("document"), transaction -> new SimpleStringProperty(transaction.getDocumentTitle())),
            () -> createDateColumn(localization.getString("borrowDate"), transaction -> transaction.getBorrowDate()),
            () -> createDateColumn(localization.getString("dueDate"), transaction -> transaction.getDueDate()),
            this::createActionColumn
        );

        // Use parallel stream to execute tasks and collect results
        return columnTasks.parallelStream()
            .map(Supplier::get)
            .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> performInitialLoad() {
        return TransactionDAO.getInstance().getIssuingTransaction();
    }
    
}
