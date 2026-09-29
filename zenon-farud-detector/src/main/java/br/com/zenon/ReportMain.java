package br.com.zenon;

import br.com.zenon.fraud.TransactionReport;
import br.com.zenon.fraud.TransactionReport.SummaryReport;

import java.nio.file.Path;

public class ReportMain {
    public static void main(String[] args) {
        Path path = Path.of("data/PS_20174392719_1491204439457_log.csv");
        TransactionReport report = new TransactionReport(path);

        SummaryReport summary = report.generateSummaryReport();

        System.out.println("Total de linhas: " + summary.totalLines());
        System.out.println("Total de fraudes: " + summary.totalFrauds());
        System.out.println("Valor total transacionado: " + summary.totalAmount());
    }
}
