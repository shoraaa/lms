package com.library.view;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.library.model.Transaction;
import com.library.services.TransactionDAO;
import com.library.util.Localization;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class OverdueTransactionTableView extends TransactionTableView {

    public OverdueTransactionTableView(TableView<Transaction> tableView) {
        super(tableView);
    }

    @Override
    protected List<TableColumn<Transaction, ?>> createColumns() {
        Localization localization = Localization.getInstance();
        List<Supplier<TableColumn<Transaction, ?>>> columnTasks = List.of(
            this::createSelectColumn,
            () -> createTextColumn(localization.getString("user"), transaction -> new SimpleStringProperty(transaction.getUserName())),
            () -> createTextColumn(localization.getString("document"), transaction -> new SimpleStringProperty(transaction.getDocumentTitle())),
            () -> createDateColumn(localization.getString("borrowDate"), transaction -> transaction.getBorrowDate()),
            () -> createDateColumn(localization.getString("returnDate"), transaction -> transaction.getReturnDate()),
            () -> createTextColumn(localization.getString("overdue"), transaction -> new SimpleStringProperty(String.valueOf(ChronoUnit.DAYS.between(transaction.getDueDate(), transaction.getReturnDate() == null ? LocalDate.now() : transaction.getReturnDate()))
            + " " + Localization.getInstance().getString("days"))),
            () -> createTextColumn(localization.getString("status"), transaction -> new SimpleStringProperty(transaction.getStatus())),
            this::createActionColumn
        );

        // Use parallel stream to execute tasks and collect results
        return columnTasks.parallelStream()
            .map(Supplier::get)
            .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> performInitialLoad() {
        return TransactionDAO.getInstance().getOverdueTransactions();
    }
    
}
